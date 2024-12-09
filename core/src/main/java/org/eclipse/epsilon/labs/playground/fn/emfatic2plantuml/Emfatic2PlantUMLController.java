package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;

@Controller(Emfatic2PlantUMLController.PATH)
public class Emfatic2PlantUMLController {
    public static final String PATH = "/emfatic2plantuml";

    @Inject
    ModelDiagramRenderer renderer;

    @ExecuteOn(TaskExecutors.IO)
    @Post
    public MetamodelDiagramResponse render(@Body Emfatic2PlantUMLRequest request) {
        try {
            return renderer.generateMetamodelDiagram(request.getEmfatic());
        } catch (Throwable e) {
            var response = new MetamodelDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

}
