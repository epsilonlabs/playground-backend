package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.core.EmfaticResource;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Post;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Controller("/flexmi2plantuml")
public class Flexmi2PlantUMLController {

    @Options("/")
    public HttpResponse<Void> options() {
        return HttpResponse.<Void>noContent()
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .header("Access-Control-Max-Age", "3600");
    }

    @Post("/")
    public PlantUMLDiagramResponse convert(@Body FlexmiToPlantUMLRequest request) {
        try {
            return run(getInMemoryFlexmiModel(request.getFlexmi(), request.getEmfatic()));
        } catch (Exception e) {
            var response = new PlantUMLDiagramResponse();
            response.setError(e.getMessage());
            response.setOutput(e.getMessage());
            return response;
        }
    }

    private PlantUMLDiagramResponse run(InMemoryEmfModel model, Variable... variables) throws Exception {
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
        PlantUMLDiagramResponse diag = new PlantUMLDiagramResponse();
        diag.setModelDiagram(output);

        return diag;
    }

    protected InMemoryEmfModel getInMemoryFlexmiModel(String flexmi, String emfatic) throws Exception {
        ResourceSet resourceSet = new ResourceSetImpl();
        EPackage ePackage = getEPackage(emfatic);
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new FlexmiResourceFactory());
        Resource resource = resourceSet.createResource(URI.createURI("flexmi.flexmi"));
        resource.load(new ByteArrayInputStream(flexmi.getBytes()), null);

        InMemoryEmfModel model = new InMemoryEmfModel(resource);
        model.setName("M");
        return model;
    }

    protected EPackage getEPackage(String emfatic) throws Exception {
        if (emfatic == null || emfatic.trim().isEmpty())
            return EcorePackage.eINSTANCE;

        EmfaticResource emfaticResource = new EmfaticResource(URI.createURI("emfatic.emf"));
        emfaticResource.load(new ByteArrayInputStream(emfatic.getBytes()), null);
        if (emfaticResource.getParseContext().hasErrors()) {
            throw new RuntimeException(emfaticResource.getParseContext().getMessages()[0].getMessage());
        } else {
            return (EPackage) emfaticResource.getContents().get(0);
        }
    }
}
