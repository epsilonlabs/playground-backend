package org.eclipse.epsilon.labs.playground.fn.run;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("/epsilon")
public interface RunEpsilonClient {
    @Options("/")
    HttpResponse<Void> options();

    @Post("/")
    EpsilonExecutionResponse execute(@Body RunEpsilonRequest request);
}
