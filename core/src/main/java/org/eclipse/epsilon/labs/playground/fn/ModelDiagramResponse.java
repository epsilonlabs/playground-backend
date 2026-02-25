package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ModelDiagramResponse extends AbstractPlaygroundResponse {
    private String modelDiagram;
    private String modelDiagramSource;

    public String getModelDiagram() {
        return modelDiagram;
    }

    public void setModelDiagram(String modelDiagram) {
        this.modelDiagram = modelDiagram;
    }

    public String getModelDiagramSource() {
        return modelDiagramSource;
    }

    public void setModelDiagramSource(String modelDiagramSource) {
        this.modelDiagramSource = modelDiagramSource;
    }
}
