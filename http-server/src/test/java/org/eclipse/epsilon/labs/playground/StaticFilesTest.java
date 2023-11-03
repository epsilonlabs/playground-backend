package org.eclipse.epsilon.labs.playground;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Flux;

@MicronautTest
public class StaticFilesTest {

    @Client("/")
    @Inject
    HttpClient client;

    @Test
    public void iconsCSS() {
        var call = Flux.from(client.exchange("/icons.css"));
        var response = call.blockFirst();
        assertEquals(HttpStatus.OK, response.getStatus());
    }

}
