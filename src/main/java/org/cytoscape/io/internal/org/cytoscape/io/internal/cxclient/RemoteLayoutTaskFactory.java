package org.cytoscape.io.internal.org.cytoscape.io.internal.cxclient;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class RemoteLayoutTaskFactory extends AbstractNetworkViewTaskFactory {


    private final CXServiceClient client;
    private final String layoutName;


    public RemoteLayoutTaskFactory(final CXServiceClient client, final String layoutName) {
        this.client = client;
        this.layoutName = layoutName;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetworkView view) {
        return new TaskIterator(new RemoteLayoutTask(view, client, layoutName));
    }


    @Override
    public boolean isReady(CyNetworkView view) {
        if (view == null) return false;

        return true;
    }
}
