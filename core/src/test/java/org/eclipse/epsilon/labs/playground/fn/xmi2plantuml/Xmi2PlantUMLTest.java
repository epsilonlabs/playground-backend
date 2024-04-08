package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Xmi2PlantUMLTest {
    
    @Inject
    Xmi2PlantUMLClient client;

    @Client(Xmi2PlantUMLController.PATH)
    @Inject
    HttpClient rawClient;

    @Test
    public void corsPreflight() {
        var request = HttpRequest
            .OPTIONS("/")
            .header(HttpHeaders.ORIGIN, "http://localhost:1234")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST");

        var response = rawClient.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void singlePackage() {
        var req = new XmiToPlantUMLRequest();

        req.setXmi("<?xml version=\"1.0\" encoding=\"ASCII\"?>\n"
            + "<test_lang:Model xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" "
            + "xmlns:test_lang=\"http://www.example.org/test_lang\"> \n"
            + "</test_lang:Model>\n"
        );

        req.setEmfatic("@namespace(uri=\"http://www.example.org/test_lang\", prefix=\"test_lang\") \n\n"
            + "package test_lang; \n"
            + "class Model { } \n"
        );

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

}
