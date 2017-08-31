package org.cytoscape.io.internal.cxclient;

import org.cxio.aspects.datamodels.ATTRIBUTE_DATA_TYPE;
import org.cxio.aspects.datamodels.AbstractAttributesAspectElement;
import org.cxio.aspects.datamodels.EdgeAttributesElement;
import org.cxio.aspects.datamodels.NodeAttributesElement;
import org.cxio.core.interfaces.AspectElement;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteAttributeTask extends AbstractNetworkTask {

    private final CXServiceClient client;



    private static final Map<ATTRIBUTE_DATA_TYPE, Class<?>> DATA_TYPE_MAP = new HashMap<>();
    static {
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.BOOLEAN, Boolean.class);
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.STRING, String.class);
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.LONG, Long.class);
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.INTEGER, Integer.class);
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.FLOAT, Float.class);
        DATA_TYPE_MAP.put(ATTRIBUTE_DATA_TYPE.DOUBLE, Double.class);
    }

    @Tunable(description="Service URL")
    public String url = "http://localhost/";

    public RemoteAttributeTask(CyNetwork network, final CXServiceClient client) {
        super(network);
        this.client = client;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        taskMonitor.setProgress(-1);
        taskMonitor.setTitle("Calling Remote CI Service");
        taskMonitor.setStatusMessage("Preparing data...");

        final Map<String, List<AspectElement>> aspectMap = client.callService(url, network, taskMonitor);


        final List<AspectElement> nodeAttr = aspectMap.get(NodeAttributesElement.ASPECT_NAME);
        final List<AspectElement> edgeAttr = aspectMap.get(EdgeAttributesElement.ASPECT_NAME);

        if(nodeAttr != null) {
            applyAttr(nodeAttr, network.getDefaultNodeTable());
        }

        if(edgeAttr != null) {
            applyAttr(edgeAttr, network.getDefaultEdgeTable());
        }

    }

    private void applyAttr(List<AspectElement> nodeAttr, CyTable table) {
        nodeAttr.forEach(aspectElement -> assignAttr(aspectElement, table));
    }


    private void assignAttr(AspectElement aspectElement, CyTable table) {

        AbstractAttributesAspectElement ae = (AbstractAttributesAspectElement) aspectElement;

        final String colName = ae.getName();
        final CyColumn col = table.getColumn(colName);
        final ATTRIBUTE_DATA_TYPE dataType = ae.getDataType();
        final Class<?> type = DATA_TYPE_MAP.get(dataType);

        System.out.println("Type = " + type);
        if(col == null) {
            table.createColumn(colName, type,false);
        }


        final List<Long> ids = ae.getPropertyOf();
        ids.forEach(
                id-> table.getRow(id)
                        .set(colName, getData(type, ae.getValue())));
    }

    private Object getData(final Class<?> type, final String valStr) {
        if(type == String.class) return valStr;

        if( type == Double.class || type == Float.class ) {
            return Double.parseDouble(valStr);
        } else if(type == Integer.class) {
            return Integer.parseInt(valStr);
        } else if(type == Long.class) {
            return Long.parseLong(valStr);
        } else if(type == Boolean.class) {
            return Boolean.parseBoolean(valStr);
        } else {
            return valStr;
        }
    }
}
