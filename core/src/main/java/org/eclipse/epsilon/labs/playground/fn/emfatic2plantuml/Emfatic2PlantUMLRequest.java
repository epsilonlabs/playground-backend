package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Emfatic2PlantUMLRequest {
    private String emfatic;

    public String getEmfatic() {
        return emfatic;
    }

    public void setEmfatic(String emfatic) {
        this.emfatic = emfatic;
    }

}
