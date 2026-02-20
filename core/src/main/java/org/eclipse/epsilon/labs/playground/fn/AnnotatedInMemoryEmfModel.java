package org.eclipse.epsilon.labs.playground.fn;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.execute.introspection.AbstractPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;

import java.util.*;
import java.util.stream.Collectors;

public class AnnotatedInMemoryEmfModel extends InMemoryEmfModel {

    protected AnnotatedEmfPropertyGetter propertyGetter = new AnnotatedEmfPropertyGetter();
    protected InMemoryEmfModel inMemoryEmfModel = null;

    public AnnotatedInMemoryEmfModel(String name, Resource modelImpl) {
        super(name, modelImpl);
    }

    public AnnotatedInMemoryEmfModel(Resource modelImpl) {
        super(modelImpl);
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
    public Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
        return type;
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

    public InMemoryEmfModel getInMemoryEmfModel() {
        if (inMemoryEmfModel == null) {
            inMemoryEmfModel = new InMemoryEmfModel(this.modelImpl);
        }
        return inMemoryEmfModel;
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
                        String value = eAnnotation.getDetails().get(property);
                        if (isEol(value)) {
                            return runEol(value, Variable.createReadOnlyVariable("self", ((EObjectStructuralFeature) object).getEObject()));
                        }
                        else {
                            return value;
                        }
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
                            if (isEol(value)) {
                                return runEol(value, Variable.createReadOnlyVariable("self", object));
                            }
                            else if (caret) {
                                return eObject.eGet(eObject.eClass().getEStructuralFeature(value));
                            } else {
                                return value;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    protected boolean isEol(String value) {
        return value.startsWith("eol:");
    }

    protected Object runEol(String code, Variable... variables) throws EolRuntimeException {
        try {
            code = code.substring(4);
            code = "return " + code + ";";
            EolModule module = new EolModule();
            module.parse(code);
            for (Variable variable : variables) {
                module.getContext().getFrameStack().put(variable);
            }
            module.getContext().getModelRepository().addModel(getInMemoryEmfModel());
            return module.execute();
        } catch (Exception e) {
            throw new EolInternalException(e);
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
            if (eStructuralFeature.isMany()) {
                return (Collection<Object>) value;
            }
            else if (value != null) {
                return Arrays.asList(value);
            }
            else {
                return Collections.emptyList();
            }
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