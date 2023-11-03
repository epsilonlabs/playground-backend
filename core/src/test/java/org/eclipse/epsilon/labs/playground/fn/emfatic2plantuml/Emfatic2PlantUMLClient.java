package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Emfatic2PlantUMLController.PATH)
public interface Emfatic2PlantUMLClient {
    @Post("/")
    MetamodelDiagramResponse render(@Body Emfatic2PlantUMLRequest request);
}
