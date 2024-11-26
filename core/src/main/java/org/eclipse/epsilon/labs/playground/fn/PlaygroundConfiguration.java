package org.eclipse.epsilon.labs.playground.fn;

import java.time.Duration;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

/**
 * Centralizes the various configuration options of the Playground.
 * Should be used via Micronaut injection.
 */
@Singleton
public class PlaygroundConfiguration {
    
    /**
     * Timeout for running Epsilon scripts. If the script runs
     * for longer than this, it will be terminated and its
     * thread will be interrupted.
     */
    @Value("${playground.timeout.millis:60000}")
    protected int timeoutMilliseconds;

    public Duration getScriptTimeout() {
        return Duration.ofMillis(timeoutMilliseconds);
    }

}
