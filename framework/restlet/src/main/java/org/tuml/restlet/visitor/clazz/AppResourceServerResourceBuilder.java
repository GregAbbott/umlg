package org.tuml.restlet.visitor.clazz;

import org.eclipse.uml2.uml.Model;
import org.opaeum.java.metamodel.OJField;
import org.opaeum.java.metamodel.OJPackage;
import org.opaeum.java.metamodel.OJPathName;
import org.opaeum.java.metamodel.annotation.OJAnnotatedClass;
import org.opaeum.java.metamodel.annotation.OJAnnotatedInterface;
import org.opaeum.java.metamodel.annotation.OJAnnotatedOperation;
import org.opaeum.java.metamodel.annotation.OJAnnotationValue;
import org.opaeum.java.metamodel.annotation.OJEnum;
import org.opaeum.java.metamodel.annotation.OJEnumLiteral;
import org.tuml.framework.Visitor;
import org.tuml.generation.Workspace;
import org.tuml.javageneration.util.TinkerGenerationUtil;
import org.tuml.restlet.util.TumlRestletGenerationUtil;

public class AppResourceServerResourceBuilder extends BaseServerResourceBuilder implements Visitor<Model> {

	public AppResourceServerResourceBuilder(Workspace workspace) {
		super(workspace);
	}

	@Override
	public void visitBefore(Model model) {

		OJAnnotatedInterface annotatedInf = new OJAnnotatedInterface("RootServerResource");
		OJPackage ojPackage = new OJPackage("restlet");
		annotatedInf.setMyPackage(ojPackage);
		addToSource(annotatedInf);

		OJAnnotatedClass annotatedClass = new OJAnnotatedClass("RootServerResourceImpl");
		annotatedClass.setSuperclass(TumlRestletGenerationUtil.ServerResource);
		annotatedClass.setMyPackage(ojPackage);
		annotatedClass.addToImplementedInterfaces(annotatedInf.getPathName());
		addToSource(annotatedClass);

		addDefaultConstructor(annotatedClass);
		addGetRootObjectRepresentation(model, annotatedInf, annotatedClass);
		addToRouterEnum(model, annotatedClass);
	}

	@Override
	public void visitAfter(Model model) {
	}

	private void addGetRootObjectRepresentation(Model model, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass) {
		OJAnnotatedOperation getInf = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
		annotatedInf.addToOperations(getInf);
		getInf.addAnnotationIfNew(new OJAnnotationValue(TumlRestletGenerationUtil.Get, "json"));

		OJAnnotatedOperation get = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
		get.addToThrows(TumlRestletGenerationUtil.ResourceException);
		annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
		TinkerGenerationUtil.addOverrideAnnotation(get);

		OJField json = new OJField("json", new OJPathName("java.lang.StringBuilder"));
		json.setInitExp("new StringBuilder()");
		get.getBody().addToLocals(json);
		get.getBody().addToStatements("json.append(\"{\\\"data\\\": [{\\\"name\\\": \\\"APP\\\"}]\")");
		get.getBody().addToStatements("json.append(\", \\\"meta\\\": [\")");
		
		
		get.getBody().addToStatements("json.append(\"{\\\"qualifiedName\\\": \\\"" + model.getQualifiedName() + "\\\"}\")");
		get.getBody().addToStatements("json.append(\", \")");
		
		get.getBody().addToStatements("json.append(RootRuntimePropertyEnum.asJson())");
		annotatedClass.addToImports("org.tuml.root.Root.RootRuntimePropertyEnum");
		get.getBody().addToStatements("json.append(\"]}\")");
		get.getBody().addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");
		
		annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
		annotatedClass.addToOperations(get);
	}

	private void addToRouterEnum(Model model, OJAnnotatedClass annotatedClass) {
		OJEnum routerEnum = (OJEnum) this.workspace.findOJClass("restlet.RestletRouterEnum");
		OJEnumLiteral ojLiteral = new OJEnumLiteral("ROOT");

		OJField uri = new OJField();
		uri.setType(new OJPathName("String"));
		uri.setInitExp("\"\"");
		ojLiteral.addToAttributeValues(uri);

		OJField serverResourceClassField = new OJField();
		serverResourceClassField.setType(new OJPathName("java.lang.Class"));
		serverResourceClassField.setInitExp(annotatedClass.getName() + ".class");
		ojLiteral.addToAttributeValues(serverResourceClassField);
		routerEnum.addToImports(annotatedClass.getPathName());
		routerEnum.addToImports(TumlRestletGenerationUtil.ServerResource);

		routerEnum.addToLiterals(ojLiteral);

		OJAnnotatedOperation attachAll = routerEnum.findOperation("attachAll", TumlRestletGenerationUtil.Router);
		attachAll.getBody().addToStatements(routerEnum.getName() + "." + ojLiteral.getName() + ".attach(router)");
	}

}
