package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Controller(Flexmi2PlantUMLController.PATH)
public class Flexmi2PlantUMLController {
    public static final String PATH = "/flexmi2plantuml";

    @Inject
    ModelLoader modelLoader;

    @Post("/")
    public ModelDiagramResponse convert(@Body FlexmiToPlantUMLRequest request) {
        try {
            return generateModelDiagram(modelLoader.getInMemoryFlexmiModel(request.getFlexmi(), request.getEmfatic()));
        } catch (Throwable e) {
            var response = new ModelDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

    public ModelDiagramResponse generateModelDiagram(Model model, Variable... variables) throws Exception {
        EglModule module = new EglModule();
        module.parse(getClass().getResource("/flexmi2plantuml.egl").toURI());
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
