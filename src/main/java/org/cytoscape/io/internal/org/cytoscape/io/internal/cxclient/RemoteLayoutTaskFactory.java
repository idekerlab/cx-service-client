package org.cytoscape.io.internal.org.cytoscape.io.internal.cxclient;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class RemoteLayoutTaskFactory extends AbstractNetworkViewTaskFactory {


    private final CXServiceClient client;

    public RemoteLayoutTaskFactory(final CXServiceClient client) {
        this.client = client;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetworkView view) {
        return new TaskIterator(new RemoteLayoutTask(view, client));
    }


    @Override
    public boolean isReady(CyNetworkView view) {
        if (view == null) return false;

        return true;
    }
}
