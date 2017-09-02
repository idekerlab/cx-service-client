package org.cytoscape.io.internal.cxclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.cxio.core.CxReader;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.util.CxioUtil;
import org.cytoscape.ci.model.CIResponse;
import org.cytoscape.io.internal.IOFactoryManager;
import org.cytoscape.io.internal.reader.LoadNetworkStreamTaskFactoryImpl;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CXServiceClient {


    // CXNetwork Writer
    private CyNetworkViewWriterFactory writerFactory;
    private InputStreamTaskFactory readerFactory;

    private final LoadNetworkStreamTaskFactoryImpl loadNetworkTF;

    private IOFactoryManager manager;



    public CXServiceClient(IOFactoryManager manager, LoadNetworkStreamTaskFactoryImpl loadNetworkTF) {
        this.manager = manager;
        this.loadNetworkTF = loadNetworkTF;
    }


    public Map<String, List<AspectElement>> callService(
            final String url, CyNetwork network, TaskMonitor tm) throws IOException, UnirestException {

        return encode(url, network, tm);

    }

    public void callService(
            final String url, TaskMonitor tm) throws Exception {

        importGraph(url, tm);

    }

    private void importGraph(final String url, final TaskMonitor tm) throws Exception {


        if(readerFactory == null) {
            readerFactory = manager.getReaderFactory();
        }

        Unirest.setTimeouts(10000000, 60000000);
        HttpResponse<JsonNode> jsonResponse = Unirest
                .post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("[]")
                .asJson();

        InputStream is = jsonResponse.getRawBody();

        ObjectMapper objectMapper = new ObjectMapper();
        CIResponse<?> res = objectMapper.readValue(is, CIResponse.class);

        if(res.errors.size()!= 0) {
            System.out.println(res.errors);
            throw new IOException("Remote graph generator returned errors");
        }

        final ByteArrayInputStream dataIs = new ByteArrayInputStream(objectMapper.writeValueAsBytes(res.data));

        InputStreamTaskFactory readerTF = readerFactory;
        TaskIterator itr = readerTF.createTaskIterator(dataIs, "RemoteGraph");
        CyNetworkReader reader = (CyNetworkReader) itr.next();
        TaskIterator tasks = loadNetworkTF.createTaskIterator("Generated", reader);


        while (tasks.hasNext()) {
            final Task task = tasks.next();
            task.run(tm);
        }

    }

    private final Map<String, List<AspectElement>> decode(InputStream is, TaskMonitor tm) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CIResponse<?> res = objectMapper.readValue(is, CIResponse.class);

        if(res.errors.size()!= 0) {
            System.out.println(res.errors);

            throw new IOException("Remote Layout returned errors");
        }

        tm.setStatusMessage("Processing result...");
        final CxReader reader = CxReader.createInstance(
                objectMapper.writeValueAsString(res.data),
                CxioUtil.getAllAvailableAspectFragmentReaders());
        Map<String, List<AspectElement>> aspectMap = CxReader.parseAsMap(reader);
        is.close();

        return aspectMap;
    }

    public Map<String, List<AspectElement>> encode(String url, final CyNetwork network, TaskMonitor tm) throws IOException, UnirestException {

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // This is a CXWriter object
        if(this.writerFactory == null) {
            writerFactory = manager.getCxFactory();
            if(writerFactory == null) {
                throw new NullPointerException("Could not find CX Writer");
            }
        }

        final CyWriter writer = this.writerFactory.createWriter(stream, network);
        String jsonString = null;
        try {
            writer.run(null);
            jsonString = stream.toString("UTF-8");
            stream.close();
        } catch (Exception e) {
            throw new IOException();
        }

        tm.setStatusMessage("Sending CX to remote service...");

        Unirest.setTimeouts(10000000, 60000000);

        HttpResponse<JsonNode> jsonResponse = Unirest
                .post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonString)
                .asJson();

        InputStream raw = jsonResponse.getRawBody();
        return decode(raw, tm);
    }

}
