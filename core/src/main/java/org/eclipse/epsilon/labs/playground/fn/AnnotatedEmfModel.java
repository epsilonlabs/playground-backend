package org.eclipse.epsilon.labs.playground.fn;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.core.EmfaticResourceFactory;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.AbstractPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

// This should extend InMemoryEmfModel instead
public class AnnotatedEmfModel extends EmfModel {

    protected AnnotatedEmfPropertyGetter propertyGetter = new AnnotatedEmfPropertyGetter();

    public static void main(String[] args) throws Exception {
        try {
            // Register the Flexmi and Emfatic parsers with EMF
            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("flexmi", new FlexmiResourceFactory());
            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emf", new EmfaticResourceFactory());

            EglModule module = new EglModule();
            module.parse(new File("core/src/main/resources/annotatedflexmi2plantuml.egl"));
            AnnotatedEmfModel model = new AnnotatedEmfModel();
            model.setName("M");
            model.setModelFile(new File("core/src/main/resources/samples/filesystem.flexmi").getAbsolutePath());
            model.setMetamodelFile(new File("core/src/main/resources/samples/filesystem.emf").getAbsolutePath());
            model.load();
            module.getContext().getModelRepository().addModel(model);
            System.out.println(module.execute());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected Collection<EObject> allContentsFromModel() {
        return super.allContentsFromModel().stream().filter(c -> !getEAnnotations(c).isEmpty()).collect(Collectors.toList());
    }

    @Override
    protected EObject createInstanceInModel(String type) throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
        throw new EolNotInstantiableModelElementTypeException(this.name, type);
    }

    @Override
    protected boolean deleteElementInModel(Object instance) throws EolRuntimeException {
        throw new EolRuntimeException("Model " + this.name + " is read-only");
    }

    @Override
    protected Collection<EObject> getAllOfKindFromModel(String kind) throws EolModelElementTypeNotFoundException {
        return getAllOfTypeFromModel(kind);
    }

    @Override
    public boolean isOfKind(Object instance, String metaClass) throws EolModelElementTypeNotFoundException {
        return isOfType(instance, metaClass);
    }

    @Override
    protected Collection<EObject> getAllOfTypeFromModel(String type) throws EolModelElementTypeNotFoundException {
        return allContentsFromModel().stream().filter(o -> {
            try {
                return isOfType(o, stripAt(type));
            } catch (EolModelElementTypeNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isOfType(Object instance, String metaClass) throws EolModelElementTypeNotFoundException {
        return getEAnnotations((EObject) instance).stream().anyMatch(eAnnotation -> eAnnotation.getSource().equals(stripAt(metaClass)));
    }

    protected List<EAnnotation> getEAnnotations(EObject instance) {
        Stack<EAnnotation> eAnnotations = new Stack<>();
        for (EAnnotation annotation : instance.eClass().getEAnnotations()) {
            eAnnotations.push(annotation);
        }
        for (EClass superType : instance.eClass().getEAllSuperTypes()) {
            for (EAnnotation annotation : superType.getEAnnotations()) {
                eAnnotations.push(annotation);
            }
        }
        return eAnnotations;
    }

    @Override
    public boolean hasType(String type) {
        return type.startsWith("@");
    }

    protected String stripAt(String type) {
        return type.startsWith("@") ? type.substring(1) : type;
    }

    @Override
    public boolean knowsAboutProperty(Object instance, String property) {
        return owns(instance) || (instance instanceof EObjectStructuralFeature && owns(((EObjectStructuralFeature) instance).getEObject()));
    }

    @Override
    public IPropertyGetter getPropertyGetter() {
        return propertyGetter;
    }

    class AnnotatedEmfPropertyGetter extends AbstractPropertyGetter {

        @Override
        public Object hasProperty(Object object, String property) {
            return object instanceof EObject || object instanceof EObjectStructuralFeature;
        }

        @Override
        public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {

            if (object instanceof EObjectStructuralFeature) {
                for (EAnnotation eAnnotation : ((EObjectStructuralFeature) object).getEStructuralFeature().getEAnnotations()) {
                    if (eAnnotation.getDetails().containsKey(property)) {
                        return eAnnotation.getDetails().get(property);
                    }
                }
            } else {
                EObject eObject = (EObject) object;
                boolean at = property.startsWith("@");

                if (at) {
                    property = property.substring(1);
                    ArrayList<EObjectStructuralFeature> objectStructuralFeatures = new ArrayList<>();
                    for (EStructuralFeature eStructuralFeature : eObject.eClass().getEAllStructuralFeatures()) {
                        if (eStructuralFeature.getEAnnotation(property) != null) {
                            objectStructuralFeatures.add(new EObjectStructuralFeature(eObject, eStructuralFeature));
                        }
                    }
                    return objectStructuralFeatures;
                } else {
                    boolean caret = property.startsWith("^");
                    if (caret) {
                        property = property.substring(1);
                    }

                    for (EAnnotation eAnnotation : getEAnnotations(eObject)) {
                        if (eAnnotation.getDetails().containsKey(property)) {

                            String value = eAnnotation.getDetails().get(property);
                            if (caret) {
                                return eObject.eGet(eObject.eClass().getEStructuralFeature(value));
                            } else {
                                if (value.startsWith("eol:")) {
                                    // TODo
                                    return null;
                                } else {
                                    return value;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    class EObjectStructuralFeature {
        protected EObject eObject;
        protected EStructuralFeature eStructuralFeature;

        public EObjectStructuralFeature(EObject eObject, EStructuralFeature eStructuralFeature) {
            this.eObject = eObject;
            this.eStructuralFeature = eStructuralFeature;
        }

        public Collection<Object> getValues() {
            Object value = getValue();
            if (value instanceof Collection<?>) return (Collection<Object>) value;
            else return Arrays.asList(value);
        }

        public Object getValue() {
            return eObject.eGet(eStructuralFeature);
        }

        public EObject getEObject() {
            return eObject;
        }

        public EStructuralFeature getEStructuralFeature() {
            return eStructuralFeature;
        }
    }
}