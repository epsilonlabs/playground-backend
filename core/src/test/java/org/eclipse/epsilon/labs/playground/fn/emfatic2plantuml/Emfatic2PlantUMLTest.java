package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;
import org.eclipse.epsilon.labs.playground.fn.PlaygroundTest;
import org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Flexmi2PlantUMLRequest;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class Emfatic2PlantUMLTest extends PlaygroundTest {
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

    @Test
    public void psl() throws Exception {
        process("psl.emf", "psl-diagram.puml");
    }

    public void process(String emfatic, String puml) throws Exception {
        var req = new Emfatic2PlantUMLRequest();

        req.setEmfatic(getResourceAsString("/plantuml/" + emfatic));
        
        MetamodelDiagramResponse result = client.render(req);

        Path path = Path.of("src/test/resources/plantuml/actual/" + puml);
        Files.createDirectories(path.getParent());

        assertNull(result.getError());
        Files.writeString(path, result.getMetamodelDiagramSource(), StandardCharsets.UTF_8);
        assertNull(result.getOutput());
        assertNotNull(result.getMetamodelDiagram());
        assertEquals(getResourceAsString("/plantuml/" + puml), result.getMetamodelDiagramSource());
    }
}
