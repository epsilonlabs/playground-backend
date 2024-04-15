package org.eclipse.epsilon.labs.playground.fn;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.Model;

import jakarta.inject.Singleton;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Singleton
public class ModelDiagramRenderer {

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
