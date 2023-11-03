package org.eclipse.epsilon.labs.playground.fn;

import java.util.Map;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("/tools")
public interface ToolDescriptorControllerClient {
    
    @Get("/")
    Map<String, Object> render();

}