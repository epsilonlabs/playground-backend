package org.eclipse.epsilon.labs.playground.fn.emfatic2graph;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;

@Controller(Emfatic2GraphController.PATH)
public class Emfatic2GraphController {
    public static final String PATH = "/emfatic2graph";

    @Inject
    ModelDiagramRenderer renderer;

    @ExecuteOn(TaskExecutors.IO)
    @Post
    public MetamodelGraphResponse render(@Body Emfatic2GraphRequest request) {
        try {
            return renderer.generateMetamodelGraph(request.getEmfatic());
        } catch (Throwable e) {
            var response = new MetamodelGraphResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

}
