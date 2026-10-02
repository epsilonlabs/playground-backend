package org.eclipse.epsilon.labs.playground.fn.emfatic2graph;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Emfatic2GraphRequest {
    private String emfatic;

    public String getEmfatic() {
        return emfatic;
    }

    public void setEmfatic(String emfatic) {
        this.emfatic = emfatic;
    }

}
