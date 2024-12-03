package org.eclipse.epsilon.labs.playground.fn.tooldesc;

import java.util.HashMap;

import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml.Emfatic2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Flexmi2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.run.RunEpsilonController;
import org.eclipse.epsilon.labs.playground.fn.xmi2plantuml.Xmi2PlantUMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/tools")
public class ToolDescriptorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolDescriptorController.class);

    @Get("/")
    public HttpResponse<String> render(HttpRequest<?> request) {
        try {
            EglModule module = new EglModule();
            module.parse(getClass().getResource("/mdenet_tool.egl").toURI());

            var urls = new HashMap<String, String>();
            urls.put("epsilon", resolve(request, RunEpsilonController.PATH));
            urls.put("flexmi2plantuml", resolve(request, Flexmi2PlantUMLController.PATH));
            urls.put("xmi2plantuml", resolve(request, Xmi2PlantUMLController.PATH));
            urls.put("emfatic2plantuml", resolve(request, Emfatic2PlantUMLController.PATH)); 

            module.getContext().getFrameStack().put(
                Variable.createReadOnlyVariable("urls", urls)
            );

            return HttpResponse.ok("" + module.execute()).contentType(MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return HttpResponse.serverError("Failed to render the tools configuration: error has been logged");
        }
    }

    private String resolve(HttpRequest<?> request, String path) {
        // Recent versions of MDENet EP support {{BASE-URL}}, so we do not need to use the Micronaut resolver
        return "{{BASE-URL}}" + path;
    }

}
