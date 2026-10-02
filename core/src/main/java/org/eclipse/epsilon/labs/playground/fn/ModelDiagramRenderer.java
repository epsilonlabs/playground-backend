package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.Model;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.labs.playground.execution.ScriptTimeoutTerminator;
import org.eclipse.epsilon.labs.playground.fn.emfatic2graph.MetamodelGraphResponse;
import org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml.MetamodelDiagramResponse;
import org.eclipse.epsilon.labs.playground.fn.flexmi2graph.ModelGraphResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    String annotationErrors = validateGraphicalSyntaxAnnotations(emfatic);
    if (annotationErrors != null) {
      diag.setError(annotationErrors);
      return diag;
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

  public MetamodelGraphResponse generateMetamodelGraph(String emfatic) throws Exception {
    MetamodelGraphResponse response = new MetamodelGraphResponse();
    response.setMetamodelGraph(emfatic2graph(emfatic));
    return response;
  }

  public Map<String, Object> generateModelGraph(Model model, Variable... variables) throws Exception {
    return model2graph(model, variables);
  }

  @Cacheable("flexmi-to-graph")
  public ModelGraphResponse generateGraphFromFlexmi(String flexmi, String emfatic) throws Exception {
    ModelGraphResponse response = new ModelGraphResponse();

    String annotationErrors = validateGraphicalSyntaxAnnotations(emfatic);
    if (annotationErrors != null) {
      response.setError(annotationErrors);
      return response;
    }

    Model model = modelLoader.getInMemoryFlexmiModel(flexmi, emfatic);
    response.setModelGraph(model2graph(model));
    return response;
  }

  /**
   * Validates the graphical syntax annotations of an Emfatic metamodel.
   *
   * @return the messages of the unsatisfied constraints (one per line), or
   *         <code>null</code> if the metamodel is not annotated or its annotations are valid
   */
  protected String validateGraphicalSyntaxAnnotations(String emfatic) throws Exception {
    if (!modelLoader.isAnnotated(emfatic)) return null;

    InMemoryEmfModel emfaticModel = modelLoader.getInMemoryEmfaticModel(emfatic);
    EvlModule module = new EvlModule();
    module.parse(getClass().getResource("/graphical-syntax-annotations.evl").toURI());
    module.getContext().getModelRepository().addModel(emfaticModel);
    module.execute();
    if (module.getContext().getUnsatisfiedConstraints().isEmpty()) return null;

    StringBuffer buffer = new StringBuffer();
    for (UnsatisfiedConstraint uc : module.getContext().getUnsatisfiedConstraints()) {
      buffer.append(uc.getMessage() + System.lineSeparator());
    }
    return buffer.toString();
  }

  protected Map<String, Object> model2graph(Model model, Variable... variables) throws Exception {
    String script = "/model2graph.eol";
    if (model instanceof AnnotatableInMemoryEmfModel) {
      model = ((AnnotatableInMemoryEmfModel) model).toAnnotatedInMemoryEmfModel();
      ((AnnotatedInMemoryEmfModel) model).getOperationContributors().add(new PlantUMLOperationContributor());
      script = "/annotatedmodel2graph.eol";
    }
    model.setName("M");
    return runGraphScript(script, model, variables);
  }

  @Cacheable("emfatic-to-graph")
  protected Map<String, Object> emfatic2graph(String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryEmfaticModel(emfatic);
    model.setName("M");
    return runGraphScript("/ecore2graph.eol", model);
  }

  /**
   * Runs an EOL script that returns a graph made of maps and sequences, and
   * converts it to plain Java maps and lists that can be serialised to JSON.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Object> runGraphScript(String script, Model model, Variable... variables) throws Exception {
    EolModule module = new EolModule();
    module.parse(getClass().getResource(script).toURI());
    module.getContext().getModelRepository().addModel(model);
    module.getContext().getFrameStack().put(variables);
    module.getContext().getOperationContributorRegistry().add(new PlantUMLOperationContributor());
    timeoutTerminator.scheduleScriptTimeout(module);

    try {
      return (Map<String, Object>) toJsonValue(module.execute());
    }
    finally {
      module.getContext().getModelRepository().dispose();
      module.getContext().dispose();
    }
  }

  protected static Object toJsonValue(Object value) {
    if (value == null || value instanceof String || value instanceof Boolean || value instanceof Number) {
      return value;
    }
    else if (value instanceof Map<?, ?> map) {
      Map<String, Object> result = new LinkedHashMap<>();
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        result.put(entry.getKey() + "", toJsonValue(entry.getValue()));
      }
      return result;
    }
    else if (value instanceof Collection<?> collection) {
      List<Object> result = new ArrayList<>();
      for (Object item : collection) {
        result.add(toJsonValue(item));
      }
      return result;
    }
    else {
      return value + "";
    }
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
