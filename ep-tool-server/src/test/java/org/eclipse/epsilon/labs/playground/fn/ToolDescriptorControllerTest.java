package org.eclipse.epsilon.labs.playground.fn;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class ToolDescriptorControllerTest {
    
    @Inject
    ToolDescriptorControllerClient client;

    @Test
    public void render() {
        // Check that the render method returns valid JSON
        var result = client.render();
        assertNotNull(result);
        assertTrue(result.containsKey("tool"));
    }

}
