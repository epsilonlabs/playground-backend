package org.eclipse.epsilon.labs.playground.fn.run;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public class RunEpsilonRequest {
    
    @NotBlank
    private String language;

    @NotBlank
    private String program;

    private String flexmi, xmi;
    private String emfatic;
    private String json;

    private String secondProgram, secondFlexmi, secondXmi, secondEmfatic;

    private String thirdFlexmi, thirdXmi, thirdEmfatic;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getFlexmi() {
        return flexmi;
    }

    public void setFlexmi(String flexmi) {
        this.flexmi = flexmi;
    }
   
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

    public String getSecondProgram() {
        return secondProgram;
    }

    public void setSecondProgram(String secondProgram) {
        this.secondProgram = secondProgram;
    }

    public String getSecondFlexmi() {
        return secondFlexmi;
    }

    public void setSecondFlexmi(String secondFlexmi) {
        this.secondFlexmi = secondFlexmi;
    }
    
    public String getSecondXmi() {
        return secondXmi;
    }

    public void setSecondXmi(String secondXmi) {
        this.secondXmi = secondXmi;
    }

    public String getSecondEmfatic() {
        return secondEmfatic;
    }

    public void setSecondEmfatic(String secondEmfatic) {
        this.secondEmfatic = secondEmfatic;
    }

    public String getThirdFlexmi() {
        return thirdFlexmi;
    }

    public void setThirdFlexmi(String thirdFlexmi) {
        this.thirdFlexmi = thirdFlexmi;
    }

    public String getThirdXmi() {
        return thirdXmi;
    }

    public void setThirdXmi(String thirdXmi) {
        this.thirdXmi = thirdXmi;
    }
    
    public String getThirdEmfatic() {
        return thirdEmfatic;
    }

    public void setThirdEmfatic(String thirdEmfatic) {
        this.thirdEmfatic = thirdEmfatic;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
