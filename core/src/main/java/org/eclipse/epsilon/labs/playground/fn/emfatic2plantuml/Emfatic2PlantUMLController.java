package org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.labs.playground.execution.ScriptTimeoutTerminator;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Controller(Emfatic2PlantUMLController.PATH)
public class Emfatic2PlantUMLController {
    public static final String PATH = "/emfatic2plantuml";

    @Inject
    ScriptTimeoutTerminator timeoutTerminator;

    @Inject
    ModelLoader loader;

    @ExecuteOn(TaskExecutors.IO)
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
        EglModule module = new EglModule();
        module.parse(getClass().getResource("/emfatic2plantuml.egl").toURI());
        module.getContext().getModelRepository().addModel(loader.getInMemoryEmfaticModel(emfatic));
        timeoutTerminator.scheduleScriptTimeout(module);

        try {
            String plantUml = module.execute() + "";

            SourceStringReader reader = new SourceStringReader(plantUml);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();

            return new String(os.toByteArray(), Charset.forName("UTF-8"));
        } finally {
            module.getContext().getModelRepository().dispose();
            module.getContext().dispose();
        }
    }
}
