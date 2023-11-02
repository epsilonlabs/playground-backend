package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import org.eclipse.epsilon.labs.playground.fn.MetamodelDiagramResponse;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("/emfatic2plantuml")
public interface Emfatic2PlantUMLClient {
    @Options("/")
    HttpResponse<Void> options();

    @Post("/")
    MetamodelDiagramResponse render(@Body Emfatic2PlantUMLRequest request);
}
