package org.tuml.restlet.visitor.clazz;

import java.util.Set;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.opaeum.java.metamodel.OJConstructor;
import org.opaeum.java.metamodel.OJField;
import org.opaeum.java.metamodel.OJPathName;
import org.opaeum.java.metamodel.OJSimpleStatement;
import org.opaeum.java.metamodel.OJVisibilityKind;
import org.opaeum.java.metamodel.annotation.OJAnnotatedClass;
import org.opaeum.java.metamodel.annotation.OJAnnotatedOperation;
import org.opaeum.java.metamodel.annotation.OJEnum;
import org.opaeum.java.metamodel.annotation.OJEnumLiteral;
import org.tuml.framework.Visitor;
import org.tuml.generation.Workspace;
import org.tuml.javageneration.util.PropertyWrapper;
import org.tuml.javageneration.util.TumlClassOperations;
import org.tuml.javageneration.visitor.BaseVisitor;

public class AddTumlUriFieldToRuntimePropertyEnum extends BaseVisitor implements Visitor<Class> {

	public AddTumlUriFieldToRuntimePropertyEnum(Workspace workspace) {
		super(workspace);
	}

	@Override
	public void visitBefore(Class clazz) {
		OJAnnotatedClass annotatedClass = findOJClass(clazz);
		OJEnum ojEnum = annotatedClass.findEnum(TumlClassOperations.propertyEnumName(clazz));

		addUriToObject(clazz, ojEnum);
		addUriToToJson(clazz, annotatedClass);

		OJField uriPrimitiveField = new OJField();
		uriPrimitiveField.setType(new OJPathName("String"));
		uriPrimitiveField.setName("tumlUri");
		ojEnum.addToFields(uriPrimitiveField);

		OJAnnotatedOperation getter = new OJAnnotatedOperation("getTumlUri", uriPrimitiveField.getType());
		getter.getBody().addToStatements("return this." + uriPrimitiveField.getName());
		ojEnum.addToOperations(getter);

		OJConstructor constructor = ojEnum.getConstructors().iterator().next();
		constructor.addParam(uriPrimitiveField.getName(), uriPrimitiveField.getType());
		constructor.getBody().addToStatements("this." + uriPrimitiveField.getName() + " = " + uriPrimitiveField.getName());

		Set<Property> properties = TumlClassOperations.getAllProperties(clazz);
		for (Property property : properties) {
			PropertyWrapper pWrap = new PropertyWrapper(property);
			if (!(pWrap.isDerived() || pWrap.isDerivedUnion())) {
				OJEnumLiteral literal = ojEnum.findLiteral(pWrap.fieldname());
				addTumlUriToLiteral(clazz, pWrap, literal);
			}
		}
		addTumlUriToLiteral(clazz, null, ojEnum.findLiteral("id"));
		// This is for root objects that have a literal to to model
		OJEnumLiteral modelLiteral = ojEnum.findLiteral(clazz.getModel().getName());
		if (modelLiteral != null) {
			addTumlUriToLiteral(clazz, null, modelLiteral);
		}
	}

	public static void addTumlUriToLiteral(Class clazz, PropertyWrapper pWrap, OJEnumLiteral literal) {
		String uri;
		if (literal.getName().equals(clazz.getModel().getName())) {
			uri = "\"/" + clazz.getModel().getName() + "\"";
		} else {
			if (clazz != null && pWrap != null) {
				uri = "\"/" + clazz.getModel().getName() + "/" + pWrap.getOwningType().getName().toLowerCase() + "s/{"
						+ pWrap.getOwningType().getName().toLowerCase() + "Id}/" + literal.getName() + "\"";
			} else {
				uri = "\"\"";
			}
		}
		OJField uriAttribute = new OJField();
		uriAttribute.setType(new OJPathName("String"));
		uriAttribute.setInitExp(uri);
		literal.addToAttributeValues(uriAttribute);

		OJField jsonField = literal.findAttributeValue("json");
		StringBuilder sb = new StringBuilder();
		sb.append(", \\\"tumlUri\\\": \\");
		sb.append(uri.substring(0, uri.length() - 1) + "\\\"");
		String initExp = jsonField.getInitExp();
		int indexOf = initExp.lastIndexOf("}");
		initExp = initExp.substring(0, indexOf) + sb.toString() + "}\"";

		jsonField.setInitExp(initExp);
	}

	private void addUriToToJson(Class clazz, OJAnnotatedClass annotatedClass) {
		if (clazz.getGeneralizations().isEmpty()) {
			OJAnnotatedOperation toJson = annotatedClass.findOperation("toJson");
			OJSimpleStatement s = (OJSimpleStatement) toJson.getBody().findStatement("uri");
			s.setExpression("sb.append(\"\\\"uri\\\": \" + getUri())");
			OJAnnotatedOperation toJsonWithoutCompositeParent = annotatedClass.findOperation("toJsonWithoutCompositeParent");
			s = (OJSimpleStatement) toJsonWithoutCompositeParent.getBody().findStatement("uri");
			s.setExpression("sb.append(\"\\\"uri\\\": \" + getUri())");
		}
		OJAnnotatedOperation getUri = new OJAnnotatedOperation("getUri");
		getUri.setReturnType(new OJPathName("String"));
		getUri.setVisibility(OJVisibilityKind.PROTECTED);
		getUri.getBody().addToStatements("return (\"\\\"\" + " + TumlClassOperations.propertyEnumName(clazz) + ".getUriToObject() + \"\\\"\")");
		annotatedClass.addToOperations(getUri);
	}

	private void addUriToObject(Class clazz, OJEnum ojEnum) {
		OJAnnotatedOperation getUriToObject = new OJAnnotatedOperation("getUriToObject", new OJPathName("String"));
		getUriToObject.setStatic(true);
		getUriToObject.getBody().addToStatements(
				"return " + "\"/" + clazz.getModel().getName() + "/" + TumlClassOperations.getPathName(clazz).getLast().toLowerCase() + "s/{"
						+ TumlClassOperations.getPathName(clazz).getLast().toLowerCase() + "Id}\"");
		ojEnum.addToOperations(getUriToObject);

		OJAnnotatedOperation asJson = ojEnum.findOperation("asJson");
		OJSimpleStatement s = (OJSimpleStatement) asJson.getBody().findStatement("uri");
		s.setExpression("sb.append(\"\\\"uri\\\": \\\"\" + getUriToObject() + \"\\\", \")");
	}

	@Override
	public void visitAfter(Class element) {

	}

}