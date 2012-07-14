package org.tuml.javageneration.ocl.visitor;

import java.util.List;

import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Parameter;

public interface HandleIteratorExp {
	String handleIteratorExp(IteratorExp<Classifier, Parameter> callExp, String sourceResult, List<String> variableResults, String bodyResult);
}
