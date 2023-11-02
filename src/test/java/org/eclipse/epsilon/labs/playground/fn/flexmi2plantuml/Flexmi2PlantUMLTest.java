package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Flexmi2PlantUMLTest {
    
    @Inject
    Flexmi2PlantUMLClient client;

    @Test
    public void singlePackage() {
        var req = new FlexmiToPlantUMLRequest();
        req.setFlexmi("<?nsuri http://www.eclipse.org/emf/2002/Ecore?>\n<package name=\"p1\"/>");
        req.setEmfatic("");

        PlantUMLDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

}
