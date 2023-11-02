package org.eclipse.epsilon.labs.playground.fn;

public abstract class AbstractPlaygroundResponse {
    private String output;
    private String error;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
