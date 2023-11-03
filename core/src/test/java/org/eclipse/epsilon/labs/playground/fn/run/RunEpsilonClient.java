package org.eclipse.epsilon.labs.playground.fn.run;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(RunEpsilonController.PATH)
public interface RunEpsilonClient {
    @Post("/")
    EpsilonExecutionResponse execute(@Body RunEpsilonRequest request);
}
