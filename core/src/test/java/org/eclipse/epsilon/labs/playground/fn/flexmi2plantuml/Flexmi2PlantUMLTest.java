package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;
import org.eclipse.epsilon.labs.playground.fn.PlaygroundTest;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Flexmi2PlantUMLTest extends PlaygroundTest {

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
        var req = new Flexmi2PlantUMLRequest();
        req.setFlexmi("<?nsuri http://www.eclipse.org/emf/2002/Ecore?>\n<package name=\"p1\"/>");
        req.setEmfatic("");

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

    @Test
    public void ccl() throws Exception {
        process("ccl.emf", "ccl.flexmi", "ccl.puml");
    }

    @Test
    public void dml() throws Exception {
        process("dml.emf", "dml.flexmi", "dml.puml");
    }

    @Test
    public void stm() throws Exception {
        process("stm.emf", "stm.flexmi", "stm.puml");
    }

    @Test
    public void filesystem() throws Exception {
        process("filesystem.emf", "filesystem.flexmi", "filesystem.puml");
    }
    
    @Test
    public void psl() throws Exception {
        process("psl.emf", "psl.flexmi", "psl.puml");
    }

    @Test
    public void psl2() throws Exception {
        process("psl2.emf", "psl2.flexmi", "psl2.puml");
    }
    
    @Test
    public void eglTemplate() {
        var req = new Flexmi2PlantUMLRequest();
        req.setFlexmi("<_><foo/><:template name=\"foo\"><content language=\"EGL\"></content></:template></_>");
        req.setEmfatic("");

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }
    
    public void process(String emfatic, String flexmi, String puml) throws Exception {
        var req = new Flexmi2PlantUMLRequest();

        req.setEmfatic(getResourceAsString("/flexmi2plantuml/" + emfatic));
        req.setFlexmi(getResourceAsString("/flexmi2plantuml/" + flexmi));

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
        assertEquals(getResourceAsString("/flexmi2plantuml/" + puml), result.getModelDiagramSource());
    }

}
