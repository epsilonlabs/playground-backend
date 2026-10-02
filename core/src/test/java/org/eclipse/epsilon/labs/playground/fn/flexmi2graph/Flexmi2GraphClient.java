package org.eclipse.epsilon.labs.playground.fn.flexmi2graph;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(Flexmi2GraphController.PATH)
public interface Flexmi2GraphClient {
    @Post("/")
    ModelGraphResponse convert(@Body Flexmi2GraphRequest request);
}
