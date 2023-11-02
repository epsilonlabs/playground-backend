package org.eclipse.epsilon.labs.playground.fn.run;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public class RunEpsilonRequest {
    
    @NotBlank
    private String language;

    @NotBlank
    private String program;

    @NotBlank
    private String flexmi;

    @NotBlank
    private String emfatic;

    private String secondProgram, secondFlexmi, secondEmfatic;

    private String thirdFlexmi, thirdEmfatic;

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

    public String getThirdEmfatic() {
        return thirdEmfatic;
    }

    public void setThirdEmfatic(String thirdEmfatic) {
        this.thirdEmfatic = thirdEmfatic;
    }
}
