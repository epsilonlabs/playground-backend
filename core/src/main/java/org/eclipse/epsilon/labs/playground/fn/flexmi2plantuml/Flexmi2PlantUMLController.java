package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundController;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Controller("/flexmi2plantuml")
public class Flexmi2PlantUMLController extends AbstractPlaygroundController {

    @Inject
    ModelLoader modelLoader;

    @Post("/")
    public ModelDiagramResponse convert(@Body FlexmiToPlantUMLRequest request) {
        try {
            return run(modelLoader.getInMemoryFlexmiModel(request.getFlexmi(), request.getEmfatic()));
        } catch (Exception e) {
            var response = new ModelDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

    public ModelDiagramResponse run(InMemoryEmfModel model, Variable... variables) throws Exception {
        EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter();
        module.parse(Flexmi2PlantUMLController.class.getResource("/flexmi2plantuml.egl").toURI());
        model.setName("M");
        module.getContext().getModelRepository().addModel(model);
        module.getContext().getFrameStack().put(variables);
        String plantUml = module.execute() + "";

        SourceStringReader reader = new SourceStringReader(plantUml);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        String output = new String(os.toByteArray(), Charset.forName("UTF-8"));
        ModelDiagramResponse diag = new ModelDiagramResponse();
        diag.setModelDiagram(output);

        return diag;
    }

}
