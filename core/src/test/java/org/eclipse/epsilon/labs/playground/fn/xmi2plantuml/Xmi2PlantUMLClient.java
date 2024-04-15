package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Xmi2PlantUMLController.PATH)
public interface Xmi2PlantUMLClient {
    @Post("/")
    ModelDiagramResponse convert(@Body Xmi2PlantUMLRequest request);
}
