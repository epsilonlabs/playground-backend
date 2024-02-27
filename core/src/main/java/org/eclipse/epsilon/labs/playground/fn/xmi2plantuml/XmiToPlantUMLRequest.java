package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class XmiToPlantUMLRequest {

    private String xmi;
    private String emfatic;

    public String getXmi() {
        return xmi;
    }

    public void setXmi(String xmi) {
        this.xmi = xmi;
    }

    public String getEmfatic() {
        return emfatic;
    }

    public void setEmfatic(String emfatic) {
        this.emfatic = emfatic;
    }
}
