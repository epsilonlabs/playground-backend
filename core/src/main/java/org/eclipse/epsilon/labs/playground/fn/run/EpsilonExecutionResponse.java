package org.eclipse.epsilon.labs.playground.fn.run;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public class EpsilonExecutionResponse extends AbstractPlaygroundResponse {
    
    @Serdeable
    public static class GeneratedFile {
        private String path, content;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private String targetModelDiagram;
    private String validatedModelDiagram;
    private String patternMatchedModelDiagram;
    private String generatedText;
    private List<GeneratedFile> generatedFiles;

    public List<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

    public void setGeneratedFiles(List<GeneratedFile> generatedFiles) {
        this.generatedFiles = generatedFiles;
    }

    public String getGeneratedText() {
        return generatedText;
    }

    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    public String getPatternMatchedModelDiagram() {
        return patternMatchedModelDiagram;
    }

    public void setPatternMatchedModelDiagram(String patternMatchedModelDiagram) {
        this.patternMatchedModelDiagram = patternMatchedModelDiagram;
    }

    public String getValidatedModelDiagram() {
        return validatedModelDiagram;
    }

    public void setValidatedModelDiagram(String validatedModelDiagram) {
        this.validatedModelDiagram = validatedModelDiagram;
    }

    public String getTargetModelDiagram() {
        return targetModelDiagram;
    }

    public void setTargetModelDiagram(String targetModelDiagram) {
        this.targetModelDiagram = targetModelDiagram;
    }

}
