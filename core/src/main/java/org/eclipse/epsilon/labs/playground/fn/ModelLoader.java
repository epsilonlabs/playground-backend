package org.eclipse.epsilon.labs.playground.fn;

import java.io.ByteArrayInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.core.EmfaticResource;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;

import jakarta.inject.Singleton;

@Singleton
public class ModelLoader {

	public InMemoryEmfModel getInMemoryEmfaticModel(String emfatic) throws Exception {
		InMemoryEmfModel model = new InMemoryEmfModel(getEPackage(emfatic).eResource());
		model.setName("M");
		return model;
	}    

    public InMemoryEmfModel getInMemoryFlexmiModel(String flexmi, String emfatic) throws Exception {
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

    public EPackage getEPackage(String emfatic) throws Exception {
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
