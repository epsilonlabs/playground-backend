package org.eclipse.epsilon.labs.playground.fn;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributorRegistry;

import java.util.ArrayList;
import java.util.List;

public class AnnotatableInMemoryEmfModel extends InMemoryEmfModel {
    
    public AnnotatableInMemoryEmfModel(Resource modelImpl) {
        super(modelImpl);
    }

    public AnnotatedInMemoryEmfModel toAnnotatedInMemoryEmfModel() {
        return new AnnotatedInMemoryEmfModel(this.name, this.modelImpl);
    }
    
}
