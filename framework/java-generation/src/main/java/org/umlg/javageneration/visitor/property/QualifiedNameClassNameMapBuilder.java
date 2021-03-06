package org.umlg.javageneration.visitor.property;

import org.eclipse.uml2.uml.Property;
import org.umlg.java.metamodel.OJConstructor;
import org.umlg.java.metamodel.OJField;
import org.umlg.java.metamodel.OJPackage;
import org.umlg.java.metamodel.OJPathName;
import org.umlg.java.metamodel.OJVisibilityKind;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.java.metamodel.annotation.OJAnnotatedOperation;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.javageneration.util.PropertyWrapper;
import org.umlg.javageneration.util.UmlgGenerationUtil;
import org.umlg.javageneration.visitor.BaseVisitor;

/**
 * Every property gets a entry in a global map that contains the property type's
 * java class
 * 
 */
public class QualifiedNameClassNameMapBuilder extends BaseVisitor implements Visitor<Property> {

	public QualifiedNameClassNameMapBuilder(Workspace workspace) {
		super(workspace);
	}

	@Override
	public void visitBefore(Property p) {
		OJAnnotatedClass globalMap = this.workspace.findOJClass(UmlgGenerationUtil.QualifiedNameClassMap.toJavaString());
		if (globalMap == null) {
			globalMap = new OJAnnotatedClass(UmlgGenerationUtil.QualifiedNameClassMap.getLast());
			OJPackage ojPackage = new OJPackage(UmlgGenerationUtil.UmlgRootPackage.toJavaString());
			globalMap.setMyPackage(ojPackage);
			addINSTANCE(globalMap);
			addMap(globalMap);
			createPropertyClassAdderMethod(globalMap);
			addToSource(globalMap);
			OJConstructor constructor = addPrivateConstructor(globalMap);
			constructor.getBody().addToStatements("addAllEntries()");
			addGetForQualifiedName(globalMap);
		}
		addEntries(globalMap, p);
	}

	private void addGetForQualifiedName(OJAnnotatedClass globalMap) {
		OJAnnotatedOperation get = new OJAnnotatedOperation("get", new OJPathName("Class<?>"));
		get.addParam("qualifiedName", "String");
		get.getBody().addToStatements("return this." + UmlgGenerationUtil.QualifiedNameClassMapName + ".get(qualifiedName)");
		globalMap.addToOperations(get);
	}

	private void addEntries(OJAnnotatedClass globalMap, Property p) {
		PropertyWrapper pWrap = new PropertyWrapper(p);
		OJAnnotatedOperation addAllEntries = globalMap.findOperation("addAllEntries");
		addAllEntries.getBody().addToStatements("this." + UmlgGenerationUtil.QualifiedNameClassMapName + ".put(\"" + p.getQualifiedName() +
				"\", " + pWrap.javaBaseTypePath().getLast() + ".class)");
		globalMap.addToImports(pWrap.javaBaseTypePath());
	}

	private void addINSTANCE(OJAnnotatedClass globalMap) {
		OJField instance = new OJField("INSTANCE", globalMap.getPathName());
		instance.setVisibility(OJVisibilityKind.PUBLIC);
		instance.setStatic(true);
		instance.setInitExp("new " + globalMap.getPathName().getLast() + "()");
		globalMap.addToFields(instance);
	}

	private void addMap(OJAnnotatedClass globalMap) {
		OJField map = new OJField(UmlgGenerationUtil.QualifiedNameClassMapName, new OJPathName("java.util.Map").addToGenerics("String").addToGenerics(
				"Class<?>"));
		map.setVisibility(OJVisibilityKind.PRIVATE);
		map.setInitExp("new HashMap<String, Class<?>>()");
		globalMap.addToImports(new OJPathName("java.util.HashMap"));
		globalMap.addToImports(new OJPathName("java.util.Map"));
		globalMap.addToFields(map);
	}

	private OJAnnotatedOperation createPropertyClassAdderMethod(OJAnnotatedClass globalMap) {
		OJAnnotatedOperation addAllEntries = new OJAnnotatedOperation("addAllEntries");
		addAllEntries.setVisibility(OJVisibilityKind.PRIVATE);
		globalMap.addToOperations(addAllEntries);
		return addAllEntries;
	}

	private OJConstructor addPrivateConstructor(OJAnnotatedClass globalMap) {
		OJConstructor constructor = new OJConstructor();
		constructor.setVisibility(OJVisibilityKind.PRIVATE);
		globalMap.addToConstructors(constructor);
		return constructor;
	}

	@Override
	public void visitAfter(Property p) {
	}

}
