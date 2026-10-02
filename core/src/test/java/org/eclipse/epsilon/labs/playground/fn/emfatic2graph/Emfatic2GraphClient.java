package org.eclipse.epsilon.labs.playground.fn.emfatic2graph;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Emfatic2GraphController.PATH)
public interface Emfatic2GraphClient {
    @Post("/")
    MetamodelGraphResponse render(@Body Emfatic2GraphRequest request);
}
