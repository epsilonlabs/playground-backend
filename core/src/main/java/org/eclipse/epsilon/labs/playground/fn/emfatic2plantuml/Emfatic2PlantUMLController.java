package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Controller(Emfatic2PlantUMLController.PATH)
public class Emfatic2PlantUMLController {
    public static final String PATH = "/emfatic2plantuml";

    @Inject
    ModelLoader loader;

    @Post("/")
    public MetamodelDiagramResponse render(@Body Emfatic2PlantUMLRequest request) {
        var response = new MetamodelDiagramResponse();

        try {
            String plantuml = run(request.getEmfatic());
            response.setMetamodelDiagram(plantuml);
        } catch (Throwable e) {
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
        }

        return response;
    }

   	protected String run(String emfatic) throws Exception {
		EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter();
		module.parse(new File("src/main/resources/emfatic2plantuml.egl"));
		module.getContext().getModelRepository().addModel(loader.getInMemoryEmfaticModel(emfatic));
		String plantUml = module.execute() + "";
		
		SourceStringReader reader = new SourceStringReader(plantUml);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
		os.close();

		return new String(os.toByteArray(), Charset.forName("UTF-8"));
	}
}
