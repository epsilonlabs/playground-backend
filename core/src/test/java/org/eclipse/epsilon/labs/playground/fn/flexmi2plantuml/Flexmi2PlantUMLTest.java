package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Flexmi2PlantUMLTest {
    
    @Inject
    Flexmi2PlantUMLClient client;

    @Client(Flexmi2PlantUMLController.PATH)
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
        var req = new FlexmiToPlantUMLRequest();
        req.setFlexmi("<?nsuri http://www.eclipse.org/emf/2002/Ecore?>\n<package name=\"p1\"/>");
        req.setEmfatic("");

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

}
