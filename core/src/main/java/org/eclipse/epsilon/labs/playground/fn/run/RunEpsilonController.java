package org.eclipse.epsilon.labs.playground.fn.run;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.IEglModule;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.Model;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.flock.FlockModule;
import org.eclipse.epsilon.labs.playground.execution.ScriptTimeoutTerminator;
import org.eclipse.epsilon.labs.playground.fn.ModelDiagramRenderer;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;
import org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Flexmi2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.run.EpsilonExecutionResponse.GeneratedFile;
import org.eclipse.epsilon.labs.playground.fn.run.egl.StringGeneratingTemplateFactory;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(RunEpsilonController.PATH)
public class RunEpsilonController {
	public static final String PATH = "/epsilon";

	@Inject
	ScriptTimeoutTerminator timeoutTerminator;

	@Inject
	ModelLoader loader;

	@Inject
	ModelDiagramRenderer renderer;

	@Inject
	Flexmi2PlantUMLController flexmiController;

	@ExecuteOn(TaskExecutors.IO)
	@Post("/")
	public EpsilonExecutionResponse execute(@Body RunEpsilonRequest request) {
		var response = new EpsilonExecutionResponse();

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			run(request, bos, response);

			response.setOutput(bos.toString());
		} catch (Throwable e) {
			response.setError(e.getMessage());
			response.setOutput(e.getMessage());
		}

		return response;
	}

	public void run(RunEpsilonRequest request, OutputStream outputStream, EpsilonExecutionResponse response)
			throws Exception {
		final String language = request.getLanguage();

		IEolModule module = createModule(language);
		module.parse(request.getProgram(), new File("/program." + language));
		if (!module.getParseProblems().isEmpty()) {
			response.setError(module.getParseProblems().get(0).toString());
			return;
		}

		module.getContext().setOutputStream(new PrintStream(outputStream));
		timeoutTerminator.scheduleScriptTimeout(module);

		try {
			switch (language) {
				case "etl":
					runEtl((EtlModule) module, request, response);
					return;
				case "flock":
					runFlock((FlockModule) module, request, response);
					return;
				case "evl":
					runEvl((EvlModule) module, request, response);
					return;
				case "epl":
					runEpl((EplModule) module, request, response);
					return;
				case "egl":
					runEgl((IEglModule) module, request, response);
					return;
				case "egx":
					runEgx((EgxModule) module, request, response);
					return;
				case "eml":
					runEml((EmlModule) module, request, response);
					return;
				default:
					runEol((EolModule) module, request);
			}
		} finally {
			module.getContext().getModelRepository().dispose();
			module.getContext().dispose();
		}
	}

	protected void runEml(EmlModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		EclModule eclModule = new EclModule();

		eclModule.parse(request.getSecondProgram(), new File("/program.ecl"));
		if (!eclModule.getParseProblems().isEmpty()) {
			response.setError(eclModule.getParseProblems().get(0).toString());
			return;
		}

		Model leftModel = getFirstModel(request);
		leftModel.setName("Left");
		leftModel.getAliases().add("Source");

		InMemoryEmfModel rightModel;
		// The MDENet EP and Playground originally sent "undefined" as default value
		// across all parameters
		if (request.getXmi() != null && !"undefined".equals(request.getXmi())) {
			rightModel = loader.getInMemoryXmiModel(request.getThirdXmi(), request.getThirdEmfatic());
		} else {
			rightModel = loader.getInMemoryFlexmiModel(request.getThirdFlexmi(), request.getThirdEmfatic());
		}
		rightModel.setName("Right");
		rightModel.getAliases().add("Source");

		InMemoryEmfModel mergedModel = loader.getBlankInMemoryModel(request.getSecondEmfatic());
		mergedModel.setName("Merged");
		mergedModel.getAliases().add("Target");

		eclModule.getContext().getModelRepository().addModel(leftModel);
		eclModule.getContext().getModelRepository().addModel(rightModel);

		MatchTrace matchTrace = eclModule.execute();

		module.getContext().setMatchTrace(matchTrace.getReduced());
		module.getContext().getModelRepository().addModel(leftModel);
		module.getContext().getModelRepository().addModel(rightModel);
		module.getContext().getModelRepository().addModel(mergedModel);

		module.execute();

		response.setTargetModelDiagram(renderer.generateModelDiagram(mergedModel).getModelDiagram());
	}

	protected void runEtl(EtlModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		Model sourceModel = getFirstModel(request);
		sourceModel.setName("Source");
		InMemoryEmfModel targetModel = loader.getBlankInMemoryModel(request.getSecondEmfatic());
		targetModel.setName("Target");

		module.getContext().getModelRepository().addModel(sourceModel);
		module.getContext().getModelRepository().addModel(targetModel);

		module.execute();

		response.setTargetModelDiagram(renderer.generateModelDiagram(targetModel).getModelDiagram());
	}

	protected void runFlock(FlockModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {

		Model originalModel = getFirstModel(request);
		originalModel.setName("Original");
		InMemoryEmfModel migratedModel = loader.getBlankInMemoryModel(request.getSecondEmfatic());
		migratedModel.setName("Migrated");

		module.getContext().getModelRepository().addModel(originalModel);
		module.getContext().getModelRepository().addModel(migratedModel);

		module.getContext().setOriginalModel(originalModel);
		module.getContext().setMigratedModel(migratedModel);

		module.execute();

		response.setTargetModelDiagram(renderer.generateModelDiagram(migratedModel).getModelDiagram());
	}

	protected void runEvl(EvlModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		Model model = getFirstModel(request);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();

		response.setValidatedModelDiagram(renderer.generateModelDiagram(model,
				Variable.createReadOnlyVariable("unsatisfiedConstraints",
						module.getContext().getUnsatisfiedConstraints()))
				.getModelDiagram());
	}

	protected void runEpl(EplModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		Model model = getFirstModel(request);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();

		response.setPatternMatchedModelDiagram(renderer.generateModelDiagram(model,
				Variable.createReadOnlyVariable("matches",
						module.getContext().getPatternMatchTrace().getMatches()))
				.getModelDiagram());
	}

	protected void runEgl(IEglModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		Model model = getFirstModel(request);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		String generatedText = module.execute() + "";
		response.setGeneratedText(generatedText);
	}

	protected void runEgx(EgxModule module, RunEpsilonRequest request, EpsilonExecutionResponse response)
			throws Exception {
		Model model = getFirstModel(request);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);

		// Regardless of which template EGX tries to parse, it will end up parsing the
		// EGL template in the example
		((StringGeneratingTemplateFactory) module.getTemplateFactory()).setTemplateCode(request.getSecondProgram());
		module.execute();

		var generatedFiles = new ArrayList<GeneratedFile>();
		response.setGeneratedFiles(generatedFiles);

		Map<String, String> results = ((StringGeneratingTemplateFactory) module.getTemplateFactory()).getResults();
		for (Entry<String, String> entry : results.entrySet()) {
			var genFile = new GeneratedFile();
			genFile.setPath(entry.getKey());
			genFile.setContent(entry.getValue());
			generatedFiles.add(genFile);
		}
	}

	protected void runEol(EolModule module, RunEpsilonRequest request) throws Exception {
		Model model = getFirstModel(request);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();
	}

	protected Model getFirstModel(RunEpsilonRequest request) throws Exception {
		// The MDENet EP and Playground originally sent "undefined" as default value
		// across all parameters
		if (request.getXmi() != null && !"undefined".equals(request.getXmi())) {
			return loader.getInMemoryXmiModel(request.getXmi(), request.getEmfatic());
		} else if (request.getFlexmi() != null && !"undefined".equals(request.getFlexmi())) {
			return loader.getInMemoryFlexmiModel(request.getFlexmi(), request.getEmfatic());
		} else if (request.getJson() != null && !"undefined".equals(request.getJson())) {
			return loader.getInMemoryJsonModel(request.getJson());
		} else {
			throw new IllegalArgumentException(
					"Request does not have a valid first model (either flexmi + emfatic or json)");
		}
	}

	protected IEolModule createModule(String language) {
		switch (language) {
			case "etl":
				return new EtlModule();
			case "flock":
				return new FlockModule();
			case "evl":
				return new EvlModule();
			case "epl":
				return new EplModule();
			case "egl":
				return new EglModule();
			case "egx":
				return new EgxModule(new StringGeneratingTemplateFactory());
			case "eml":
				return new EmlModule();
			default:
				return new EolModule();
		}
	}
}
