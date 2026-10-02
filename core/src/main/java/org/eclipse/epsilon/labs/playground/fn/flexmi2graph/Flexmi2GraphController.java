package org.eclipse.epsilon.labs.playground.fn.flexmi2graph;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(Flexmi2GraphController.PATH)
public class Flexmi2GraphController {
    public static final String PATH = "/flexmi2graph";

    @Inject
    ModelDiagramRenderer renderer;

    @ExecuteOn(TaskExecutors.IO)
    @Post
    public ModelGraphResponse convert(@Body Flexmi2GraphRequest request) {
        try {
            return renderer.generateGraphFromFlexmi(request.getFlexmi(), request.getEmfatic());
        } catch (Throwable e) {
            var response = new ModelGraphResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

}
