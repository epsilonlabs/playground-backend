package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Flexmi2PlantUMLRequest {

    private String flexmi;
    private String emfatic;

    public String getFlexmi() {
        return flexmi;
    }

    public void setFlexmi(String flexmi) {
        this.flexmi = flexmi;
    }

    public String getEmfatic() {
        return emfatic;
    }

    public void setEmfatic(String emfatic) {
        this.emfatic = emfatic;
    }
}
