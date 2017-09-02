package org.cytoscape.io.internal.cxclient;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoteGraphTaskFactory extends AbstractTaskFactory {

    private final CXServiceClient client;

    public RemoteGraphTaskFactory(final CXServiceClient client) {
        this.client = client;
    }

    @Override
    public TaskIterator createTaskIterator() {
        return new TaskIterator(new RemoteGraphTask(client));
    }
}