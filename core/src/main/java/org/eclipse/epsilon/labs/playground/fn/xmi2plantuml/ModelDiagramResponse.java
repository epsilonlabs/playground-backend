package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ModelDiagramResponse extends AbstractPlaygroundResponse {
    private String modelDiagram;

    public String getModelDiagram() {
        return modelDiagram;
    }

    public void setModelDiagram(String modelDiagram) {
        this.modelDiagram = modelDiagram;
    }


}
