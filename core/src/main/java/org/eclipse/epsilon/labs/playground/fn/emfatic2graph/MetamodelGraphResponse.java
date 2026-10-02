package org.eclipse.epsilon.labs.playground.fn.emfatic2graph;

import java.util.Map;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MetamodelGraphResponse extends AbstractPlaygroundResponse {
    private Map<String, Object> metamodelGraph;

    public Map<String, Object> getMetamodelGraph() {
        return metamodelGraph;
    }

    public void setMetamodelGraph(Map<String, Object> metamodelGraph) {
        this.metamodelGraph = metamodelGraph;
    }
}
