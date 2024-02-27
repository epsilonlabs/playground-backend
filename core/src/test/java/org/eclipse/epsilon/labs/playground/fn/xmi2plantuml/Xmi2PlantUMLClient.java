package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Xmi2PlantUMLController.PATH)
public interface Xmi2PlantUMLClient {
    @Post("/")
    ModelDiagramResponse convert(@Body XmiToPlantUMLRequest request);
}
