package org.eclipse.epsilon.labs.playground.fn.flexmi2graph;

import java.util.Map;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ModelGraphResponse extends AbstractPlaygroundResponse {
    private Map<String, Object> modelGraph;

    public Map<String, Object> getModelGraph() {
        return modelGraph;
    }

    public void setModelGraph(Map<String, Object> modelGraph) {
        this.modelGraph = modelGraph;
    }
}
