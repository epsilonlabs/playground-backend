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
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.IEglModule;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.flock.FlockModule;
import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundController;
import org.eclipse.epsilon.labs.playground.fn.ModelLoader;
import org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Flexmi2PlantUMLController;
import org.eclipse.epsilon.labs.playground.fn.run.EpsilonExecutionResponse.GeneratedFile;
import org.eclipse.epsilon.labs.playground.fn.run.egl.StringGeneratingTemplateFactory;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/epsilon")
public class RunEpsilonController extends AbstractPlaygroundController {

    @Inject
    ModelLoader loader;

    @Inject
    Flexmi2PlantUMLController flexmiController;

    @Post("/")
    public EpsilonExecutionResponse execute(@Body RunEpsilonRequest request) {
        var response = new EpsilonExecutionResponse();

		try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            run(request.getLanguage(),
            		request.getProgram(), 
            		request.getSecondProgram(), 
            		request.getFlexmi(), 
            		request.getEmfatic(), 
            		request.getSecondFlexmi(), 
            		request.getSecondEmfatic(), 
            		request.getThirdFlexmi(), 
            		request.getThirdEmfatic(), 
            		bos, response);
            response.setOutput(bos.toString());
        } catch (Exception e) {
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
        }

        return response;
    }

    public void run(String language, String program, String secondProgram, String flexmi, String emfatic, String secondFlexmi, String secondEmfatic, String thirdFlexmi, String thirdEmfatic, OutputStream outputStream, EpsilonExecutionResponse response) throws Exception {
		
		IEolModule module = createModule(language);
		module.parse(program, new File("/program." + language));
		if (!module.getParseProblems().isEmpty()) {
            response.setError(module.getParseProblems().get(0).toString());
			return;
		}
		
		module.getContext().setOutputStream(new PrintStream(outputStream));
		
		switch (language) {
			case "etl": runEtl((EtlModule) module, flexmi, emfatic, secondEmfatic, response); return;
			case "flock": runFlock((FlockModule) module, flexmi, emfatic, secondEmfatic, response); return;
			case "evl": runEvl((EvlModule) module, flexmi, emfatic, response); return;
			case "epl": runEpl((EplModule) module, flexmi, emfatic, response); return;
			case "egl": runEgl((IEglModule) module, flexmi, emfatic, response); return;
			case "egx": runEgx((EgxModule) module, secondProgram, flexmi, emfatic, response); return;
			case "eml": runEml((EmlModule) module, secondProgram, flexmi, emfatic, thirdFlexmi, thirdEmfatic, secondEmfatic, response); return;
			default: runEol((EolModule) module, flexmi, emfatic);
		}
		
	}
	
	protected void runEml(EmlModule module, String ecl, String leftFlexmi, String leftEmfatic, String rightFlexmi, String rightEmfatic, String mergedEmfatic, EpsilonExecutionResponse response) throws Exception {
		
		EclModule eclModule = new EclModule();
		
		eclModule.parse(ecl, new File("/program.ecl"));
		if (!eclModule.getParseProblems().isEmpty()) {
			response.setError(eclModule.getParseProblems().get(0).toString());
			return;
		}
		
		InMemoryEmfModel leftModel = loader.getInMemoryFlexmiModel(leftFlexmi, leftEmfatic);
		leftModel.setName("Left");
		leftModel.getAliases().add("Source");
		
		InMemoryEmfModel rightModel = loader.getInMemoryFlexmiModel(rightFlexmi, rightEmfatic);
		rightModel.setName("Right");
		rightModel.getAliases().add("Source");
		
		InMemoryEmfModel mergedModel = loader.getBlankInMemoryModel(mergedEmfatic);
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

        response.setTargetModelDiagram(flexmiController.generateModelDiagram(mergedModel).getModelDiagram());
	}
	
	protected void runEtl(EtlModule module, String flexmi, String emfatic, String secondEmfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel sourceModel = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		sourceModel.setName("Source");
		InMemoryEmfModel targetModel = loader.getBlankInMemoryModel(secondEmfatic);
		targetModel.setName("Target");
		
		module.getContext().getModelRepository().addModel(sourceModel);
		module.getContext().getModelRepository().addModel(targetModel);
		
		module.execute();
		
        response.setTargetModelDiagram(flexmiController.generateModelDiagram(targetModel).getModelDiagram());
	}
	
	protected void runFlock(FlockModule module, String flexmi, String emfatic, String secondEmfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel originalModel = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		originalModel.setName("Original");
		InMemoryEmfModel migratedModel = loader.getBlankInMemoryModel(secondEmfatic);
		migratedModel.setName("Migrated");
		
		module.getContext().getModelRepository().addModel(originalModel);
		module.getContext().getModelRepository().addModel(migratedModel);
		
		module.getContext().setOriginalModel(originalModel);
		module.getContext().setMigratedModel(migratedModel);
		
		module.execute();
		
        response.setTargetModelDiagram(flexmiController.generateModelDiagram(migratedModel).getModelDiagram());
	}
	
	protected void runEvl(EvlModule module, String flexmi, String emfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel model = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();

        response.setValidatedModelDiagram(flexmiController.generateModelDiagram(model, 
				Variable.createReadOnlyVariable("unsatisfiedConstraints",
                module.getContext().getUnsatisfiedConstraints())).getModelDiagram());
	}
	
	protected void runEpl(EplModule module, String flexmi, String emfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel model = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();

        response.setPatternMatchedModelDiagram(flexmiController.generateModelDiagram(model, 
				Variable.createReadOnlyVariable("matches",
                    module.getContext().getPatternMatchTrace().getMatches()))
                .getModelDiagram());
	}

	protected void runEgl(IEglModule module, String flexmi, String emfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel model = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		String generatedText = module.execute() + "";
        response.setGeneratedText(generatedText);
	}

	protected void runEgx(EgxModule module, String templateCode, String flexmi, String emfatic, EpsilonExecutionResponse response) throws Exception {
		InMemoryEmfModel model = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		
		// Regardless of which template EGX tries to parse, it will end up parsing the EGL template in the example
		((StringGeneratingTemplateFactory) module.getTemplateFactory()).setTemplateCode(templateCode);
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
	
	protected void runEol(EolModule module, String flexmi, String emfatic) throws Exception {
		InMemoryEmfModel model = loader.getInMemoryFlexmiModel(flexmi, emfatic);
		model.setName("M");
		module.getContext().getModelRepository().addModel(model);
		module.execute();
	}

	protected IEolModule createModule(String language) {
		switch (language) {
		case "etl": return new EtlModule();
		case "flock": return new FlockModule();
		case "evl": return new EvlModule();
		case "epl": return new EplModule();
		case "egl": return new EglTemplateFactoryModuleAdapter();
		case "egx": return new EgxModule(new StringGeneratingTemplateFactory());
		case "eml": return new EmlModule();
		default: return new EolModule();
		}
	}
}
