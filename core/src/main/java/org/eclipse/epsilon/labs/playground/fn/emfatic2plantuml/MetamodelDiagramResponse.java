package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MetamodelDiagramResponse extends AbstractPlaygroundResponse {
    private String metamodelDiagram;
    private String metamodelDiagramSource;

    public String getMetamodelDiagram() {
        return metamodelDiagram;
    }

    public void setMetamodelDiagram(String modelDiagram) {
        this.metamodelDiagram = modelDiagram;
    }

    public String getMetamodelDiagramSource() {
        return metamodelDiagramSource;
    }

    public void setMetamodelDiagramSource(String metamodelDiagramSource) {
        this.metamodelDiagramSource = metamodelDiagramSource;
    }
}
