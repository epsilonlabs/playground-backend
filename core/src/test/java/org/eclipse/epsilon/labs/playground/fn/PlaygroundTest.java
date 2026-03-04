package org.eclipse.epsilon.labs.playground.fn;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PlaygroundTest {

    protected String getResourceAsString(String resource) throws IOException {
        var inputStream = getClass().getResourceAsStream(resource);
        return CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }
}
