package org.cytoscape.org.cytoscape.io.cxclient;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

public class CXServiceClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void callService() throws Exception {



//        CXServiceClient client = new CXServiceClient();

//        client.callService(new URL("http://localhost?"));
    }

    @Test
    public void testClient() throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/sample.cx"));

        String inputString = new String(encoded, Charset.defaultCharset());


        assertNotNull(inputString);

        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://localhost/")
                .header("accept", "application/json")
                .body(inputString)
                .asJson();
        assertNotNull(jsonResponse);



        InputStream body = jsonResponse.getRawBody();
    }

}