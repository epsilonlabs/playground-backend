package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Flexmi2PlantUMLController.PATH)
public interface Flexmi2PlantUMLClient {
    @Post("/")
    ModelDiagramResponse convert(@Body Flexmi2PlantUMLRequest request);
}
