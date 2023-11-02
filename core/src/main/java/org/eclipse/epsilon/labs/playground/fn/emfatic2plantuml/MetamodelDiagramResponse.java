package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MetamodelDiagramResponse extends AbstractPlaygroundResponse {
    private String metamodelDiagram;

    public String getMetamodelDiagram() {
        return metamodelDiagram;
    }

    public void setMetamodelDiagram(String modelDiagram) {
        this.metamodelDiagram = modelDiagram;
    }
}
