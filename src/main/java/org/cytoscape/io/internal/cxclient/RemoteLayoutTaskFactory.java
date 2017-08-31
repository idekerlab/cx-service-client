package org.cytoscape.io.internal.cxclient;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class RemoteLayoutTaskFactory extends AbstractNetworkViewTaskFactory {

    private final CXServiceClient client;

    private final NetworkTaskFactory fitContent;


    public RemoteLayoutTaskFactory(final CXServiceClient client, final NetworkTaskFactory fitContent) {
        this.client = client;
        this.fitContent = fitContent;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetworkView view) {
        System.out.println(fitContent);
        return new TaskIterator(
                new RemoteLayoutTask(view, client),
                fitContent.createTaskIterator(view.getModel()).next()
        );
    }


    @Override
    public boolean isReady(CyNetworkView view) {
        if (view == null) return false;

        return true;
    }
}
