package org.cytoscape.io.internal.org.cytoscape.io.internal.cxclient;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

public class RemoteLayoutTask extends AbstractNetworkViewTask {


    private final CXServiceClient client;

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

        System.out.println("Calling remote service...");


        client.callService("", view.getModel());

    }
}
