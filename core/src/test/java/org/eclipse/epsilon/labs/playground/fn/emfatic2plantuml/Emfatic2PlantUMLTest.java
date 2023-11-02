package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Emfatic2PlantUMLTest {
    @Inject
    Emfatic2PlantUMLClient client;
    
    @Test
    public void singleClass() {
        var req = new Emfatic2PlantUMLRequest();
        req.setEmfatic(String.join("\n", 
            "@namespace(uri=\"example\", prefix=\"ex\")",
            "package example;",
            "class Foo {}"));

        var response = client.render(req);
        assertNull(response.getError());
        assertNotNull(response.getMetamodelDiagram());
    }
}
