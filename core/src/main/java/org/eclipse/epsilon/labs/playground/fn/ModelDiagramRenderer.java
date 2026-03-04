package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.Model;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.labs.playground.execution.ScriptTimeoutTerminator;
import org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml.MetamodelDiagramResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Singleton
public class ModelDiagramRenderer {

  @Inject
  ModelLoader modelLoader;

  @Inject
  ScriptTimeoutTerminator timeoutTerminator;

  public MetamodelDiagramResponse generateMetamodelDiagram(String emfatic) throws Exception {
    MetamodelDiagramResponse response = new MetamodelDiagramResponse();
    String plantuml = emfatic2plantuml(emfatic);
    response.setMetamodelDiagramSource(plantuml);
    response.setMetamodelDiagram(renderPlantUML(plantuml));
    return response;
  }

  public ModelDiagramResponse generateModelDiagram(Model model, Variable... variables) throws Exception {
    ModelDiagramResponse diag = new ModelDiagramResponse();
    String plantuml = model2plantuml(model, variables);
    diag.setModelDiagramSource(plantuml);
    diag.setModelDiagram(renderPlantUML(plantuml));
    return diag;
  }

  @Cacheable("flexmi-to-svg")
  public ModelDiagramResponse generateDiagramFromFlexmi(String flexmi, String emfatic) throws Exception {
    ModelDiagramResponse diag = new ModelDiagramResponse();

    if (modelLoader.isAnnotated(emfatic)) {
      InMemoryEmfModel emfaticModel = modelLoader.getInMemoryEmfaticModel(emfatic);
      EvlModule module = new EvlModule();
      module.parse(getClass().getResource("/graphical-syntax-annotations.evl").toURI());
      module.getContext().getModelRepository().addModel(emfaticModel);
      module.execute();
      if (!module.getContext().getUnsatisfiedConstraints().isEmpty()) {
        StringBuffer buffer = new StringBuffer();
        for (UnsatisfiedConstraint uc : module.getContext().getUnsatisfiedConstraints()) {
          buffer.append(uc.getMessage() + System.lineSeparator());
        }
        diag.setError(buffer.toString());
        return diag;
      }
    }
    
    Model model = modelLoader.getInMemoryFlexmiModel(flexmi, emfatic);
    String plantuml = model2plantuml(model);
    diag.setModelDiagramSource(plantuml);
    diag.setModelDiagram(renderPlantUML(plantuml));
    return diag;
  }



  @Cacheable("xmi-to-svg")
  public ModelDiagramResponse generateDiagramFromXmi(String xmi, String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryXmiModel(xmi, emfatic);
    ModelDiagramResponse diag = new ModelDiagramResponse();
    String plantuml = model2plantuml(model);
    diag.setModelDiagramSource(plantuml);
    diag.setModelDiagram(renderPlantUML(plantuml));
    return diag;
  }

  protected String model2plantuml(Model model, Variable... variables) throws Exception {
    EglModule module = new EglModule();

    if (model instanceof AnnotatableInMemoryEmfModel) {
      model = ((AnnotatableInMemoryEmfModel) model).toAnnotatedInMemoryEmfModel();
      ((AnnotatedInMemoryEmfModel) model).getOperationContributors().add(new PlantUMLOperationContributor());
    }
    
    String template = model instanceof AnnotatedInMemoryEmfModel ?
            "/annotatedmodel2plantuml.egl" : "/model2plantuml.egl";
    module.parse(getClass().getResource(template).toURI());
    model.setName("M");
    module.getContext().getModelRepository().addModel(model);
    module.getContext().getFrameStack().put(variables);
    module.getContext().getOperationContributorRegistry().add(new PlantUMLOperationContributor());
    timeoutTerminator.scheduleScriptTimeout(module);

    try {
      return module.execute() + "";
    }
    finally {
      module.getContext().getModelRepository().dispose();
      module.getContext().dispose();
    }
  }

  @Cacheable("emfatic-to-plantuml")
  protected String emfatic2plantuml(String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryEmfaticModel(emfatic);
    model.setName("M");

    EglModule module = new EglModule();
    module.parse(getClass().getResource("/ecore2plantuml.egl").toURI());
    module.getContext().getModelRepository().addModel(model);
    module.getContext().getOperationContributorRegistry().add(new PlantUMLOperationContributor());
    timeoutTerminator.scheduleScriptTimeout(module);

    try {
      return module.execute() + "";
    } finally {
      module.getContext().getModelRepository().dispose();
      module.getContext().dispose();
    }
  }

  @Cacheable("plantuml-to-svg")
  protected String renderPlantUML(String plantUml) throws IOException {
    SourceStringReader reader = new SourceStringReader(plantUml);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DiagramDescription diagramDescription = reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
//    if (diagramDescription.getDescription().equalsIgnoreCase("(Error)")) {
//      return plantUml;
//    }
//    else {
      return os.toString(StandardCharsets.UTF_8);
//    }

  }

}
