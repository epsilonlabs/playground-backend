package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PlantUMLDiagramResponse {
    private String modelDiagram;
    private String output;
    private String error;

    public String getModelDiagram() {
        return modelDiagram;
    }

    public void setModelDiagram(String modelDiagram) {
        this.modelDiagram = modelDiagram;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
