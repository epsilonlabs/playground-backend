package org.eclipse.epsilon.labs.playground.fn.xmi2plantuml;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(Xmi2PlantUMLController.PATH)
public class Xmi2PlantUMLController {
    public static final String PATH = "/xmi2plantuml";

    @Inject
    ModelDiagramRenderer renderer;

    @ExecuteOn(TaskExecutors.IO)
    @Post("/")
    public ModelDiagramResponse convert(@Body Xmi2PlantUMLRequest request) {
        try {
            return renderer.generateDiagramFromXmi(request.getXmi(), request.getEmfatic());
        } catch (Throwable e) {
            var response = new ModelDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

}
