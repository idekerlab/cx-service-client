package org.cytoscape.io.internal.cxclient;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

public class RemoteAttributeTask extends AbstractNetworkTask {

    private final CXServiceClient client;

    public RemoteAttributeTask(CyNetwork network, final CXServiceClient client) {
        super(network);
        this.client = client;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

    }
}
