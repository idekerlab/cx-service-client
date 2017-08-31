package org.cytoscape.io.internal.cxclient;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoteAttributeTaskFactory extends AbstractNetworkTaskFactory {

    private final CXServiceClient client;

    public RemoteAttributeTaskFactory(final CXServiceClient client) {
        this.client = client;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetwork network) {
        return new TaskIterator(new RemoteAttributeTask(network, client));
    }


    @Override
    public boolean isReady(CyNetwork network) {
        if (network == null) return false;

        return true;
    }
}
