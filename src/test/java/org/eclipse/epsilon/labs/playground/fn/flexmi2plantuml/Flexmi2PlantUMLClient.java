package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("/flexmi2plantuml")
public interface Flexmi2PlantUMLClient {
    @Options("/")
    HttpResponse<Void> options();

    @Post("/")
    PlantUMLDiagramResponse convert(@Body FlexmiToPlantUMLRequest request);
}
