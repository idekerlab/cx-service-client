package org.cytoscape.io.internal.cxclient;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class RemoteGraphTask extends AbstractTask {


    private final CXServiceClient client;


    @Tunable(description="Service URL")
    public String url = "http://localhost/";


    public RemoteGraphTask(final CXServiceClient client) {
        this.client = client;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        taskMonitor.setProgress(-1);
        taskMonitor.setTitle("Calling Remote CI Service");
        taskMonitor.setStatusMessage("Preparing data...");

        client.callService(url, taskMonitor);

    }
}



