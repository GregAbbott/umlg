package org.tuml.javageneration.ocl.visitor.java;

import java.util.List;

import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.tuml.javageneration.ocl.visitor.HandleOperationExp;

public class OclEqualExprToJava implements HandleOperationExp {

	@Override
	public String handleOperationExp(OperationCallExp<Classifier, Operation> oc, String sourceResult, List<String> argumentResults) {
		if (argumentResults.size() != 1) {
			throw new IllegalStateException("The equals operation must have one and only one argument!");
		}
		StringBuilder result = new StringBuilder();
		result.append(sourceResult);
		result.append(".equals(");
		result.append(argumentResults.get(0));
		result.append(")");
		return result.toString();
	}

}
