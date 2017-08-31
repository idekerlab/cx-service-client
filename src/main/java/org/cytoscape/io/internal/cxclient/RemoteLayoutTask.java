package org.cytoscape.io.internal.cxclient;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.core.interfaces.AspectElement;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import java.util.List;
import java.util.Map;

import static org.cxio.aspects.datamodels.CartesianLayoutElement.ASPECT_NAME;

public class RemoteLayoutTask extends AbstractNetworkViewTask {


    private final CXServiceClient client;


    @Tunable(description="Service URL")
    public String url = "http://localhost/";

    @Tunable(description = "Scale factor (applied for the result returned from layout service)")
    public Double scale = 1.0;

    /**
     * A base class for tasks that need to operate on a network view.
     *
     * @param view must be a non-empty network view for descendants to operate on
     */
    public RemoteLayoutTask(CyNetworkView view, final CXServiceClient client) {
        super(view);
        this.client = client;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        taskMonitor.setProgress(-1);
        taskMonitor.setTitle("Calling Remote CI Service");
        taskMonitor.setStatusMessage("Preparing data...");

        Map<String, List<AspectElement>> layoutMap = client.callService(url, view.getModel(), taskMonitor);

        System.out.println("================================== result");
        System.out.println("================================== W: "
                + view.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH));
        System.out.println("================================== h: "
                + view.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT));


        final List<AspectElement> layouts = layoutMap.get(ASPECT_NAME);

        if(layouts == null) {
            throw new IllegalStateException("Layout result is empty.");
        }

        layouts.forEach(
                aspect -> setPosition(
                        view, ((CartesianLayoutElement)aspect), scale));


    }

    private final void setPosition(CyNetworkView view, CartesianLayoutElement le, Double scale) {
        final View<CyNode> nv = view.getNodeView(view.getModel().getNode(le.getNode()));

        nv.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, Double.parseDouble(le.getX())* scale);
        nv.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, Double.parseDouble(le.getY()) * scale);

    }
}
