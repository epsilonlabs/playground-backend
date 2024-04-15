package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import org.eclipse.epsilon.eol.models.Model;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller(Flexmi2PlantUMLController.PATH)
public class Flexmi2PlantUMLController {
    public static final String PATH = "/flexmi2plantuml";

    @Inject
    ModelLoader modelLoader;

    @Inject
    ModelDiagramRenderer renderer;

    @Post("/")
    public ModelDiagramResponse convert(@Body Flexmi2PlantUMLRequest request) {
        try {
            Model model = modelLoader.getInMemoryFlexmiModel(request.getFlexmi(), request.getEmfatic());
            return renderer.generateModelDiagram(model);
        } catch (Throwable e) {
            var response = new ModelDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

}
