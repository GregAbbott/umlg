package org.tuml.javageneration.visitor.clazz;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.tuml.java.metamodel.OJField;
import org.tuml.java.metamodel.OJIfStatement;
import org.tuml.java.metamodel.OJPathName;
import org.tuml.java.metamodel.OJSimpleStatement;
import org.tuml.java.metamodel.OJSwitchCase;
import org.tuml.java.metamodel.OJSwitchStatement;
import org.tuml.java.metamodel.annotation.OJAnnotatedClass;
import org.tuml.java.metamodel.annotation.OJAnnotatedOperation;
import org.tuml.framework.Visitor;
import org.tuml.generation.Workspace;
import org.tuml.javageneration.util.PropertyWrapper;
import org.tuml.javageneration.util.TinkerGenerationUtil;
import org.tuml.javageneration.util.TumlClassOperations;
import org.tuml.javageneration.visitor.BaseVisitor;

public class ClassRuntimePropertyImplementorVisitor extends BaseVisitor implements Visitor<Class> {

    public ClassRuntimePropertyImplementorVisitor(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void visitBefore(Class clazz) {
        OJAnnotatedClass annotatedClass = findOJClass(clazz);
        addInitialiseProperty(annotatedClass, clazz);
        addGetMetaDataAsJson(annotatedClass, clazz);
        RuntimePropertyImplementor.addTumlRuntimePropertyEnum(annotatedClass, TumlClassOperations.propertyEnumName(clazz), clazz,
                TumlClassOperations.getAllProperties(clazz), TumlClassOperations.hasCompositeOwner(clazz), clazz.getModel().getName());
        addGetQualifiers(annotatedClass, clazz);
        addGetSize(annotatedClass, clazz);
    }

    @Override
    public void visitAfter(Class clazz) {
    }

    private void addInitialiseProperty(OJAnnotatedClass annotatedClass, Class clazz) {
        OJAnnotatedOperation initialiseProperty = new OJAnnotatedOperation("initialiseProperty");
        TinkerGenerationUtil.addOverrideAnnotation(initialiseProperty);
        initialiseProperty.addParam("tumlRuntimeProperty", TinkerGenerationUtil.tumlRuntimePropertyPathName.getCopy());
        initialiseProperty.addParam("inverse", "boolean");
        if (!clazz.getGeneralizations().isEmpty()) {
            initialiseProperty.getBody().addToStatements("super.initialiseProperty(tumlRuntimeProperty, inverse)");
        }
        annotatedClass.addToOperations(initialiseProperty);

        OJField runtimePropoerty = new OJField("runtimeProperty", new OJPathName(TumlClassOperations.propertyEnumName(clazz)));
        initialiseProperty.getBody().addToLocals(runtimePropoerty);
        OJIfStatement ifInverse = new OJIfStatement("!inverse");
        ifInverse.addToThenPart("runtimeProperty = " + "(" + TumlClassOperations.propertyEnumName(clazz)
                + ".fromQualifiedName(tumlRuntimeProperty.getQualifiedName()))");
        ifInverse.addToElsePart("runtimeProperty = " + "(" + TumlClassOperations.propertyEnumName(clazz)
                + ".fromQualifiedName(tumlRuntimeProperty.getInverseQualifiedName()))");
        initialiseProperty.getBody().addToStatements(ifInverse);

        OJIfStatement ifNotNull = new OJIfStatement("runtimeProperty != null");
        initialiseProperty.getBody().addToStatements(ifNotNull);
        OJSwitchStatement ojSwitchStatement = new OJSwitchStatement();
        ojSwitchStatement.setCondition("runtimeProperty");
        ifNotNull.addToThenPart(ojSwitchStatement);

        for (Property p : TumlClassOperations.getAllOwnedProperties(clazz)) {
            PropertyWrapper pWrap = new PropertyWrapper(p);
            if (!(pWrap.isDerived() || pWrap.isDerivedUnion())) {
                OJSwitchCase ojSwitchCase = new OJSwitchCase();
                ojSwitchCase.setLabel(pWrap.fieldname());
                OJSimpleStatement statement = new OJSimpleStatement("this." + pWrap.fieldname() + " = " + pWrap.javaDefaultInitialisation(clazz));
                statement.setName(pWrap.fieldname());
                ojSwitchCase.getBody().addToStatements(statement);
                annotatedClass.addToImports(pWrap.javaImplTypePath());
                ojSwitchStatement.addToCases(ojSwitchCase);
            }
        }
    }

    private void addGetQualifiers(OJAnnotatedClass annotatedClass, Class clazz) {
        OJAnnotatedOperation getQualifiers = new OJAnnotatedOperation("getQualifiers");
        TinkerGenerationUtil.addOverrideAnnotation(getQualifiers);
        getQualifiers.setComment("getQualifiers is called from the collection in order to update the index used to implement the qualifier");
        getQualifiers.addParam("tumlRuntimeProperty", TinkerGenerationUtil.tumlRuntimePropertyPathName.getCopy());
        getQualifiers.addParam("node", TinkerGenerationUtil.TUML_NODE);
        getQualifiers.addParam("inverse", "boolean");
        getQualifiers.setReturnType(new OJPathName("java.util.List").addToGenerics(TinkerGenerationUtil.TINKER_QUALIFIER_PATHNAME));
        annotatedClass.addToOperations(getQualifiers);

        OJField result = null;
        if (!clazz.getGeneralizations().isEmpty()) {
            result = new OJField(getQualifiers.getBody(), "result", getQualifiers.getReturnType(), "super.getQualifiers(tumlRuntimeProperty, node, inverse)");
        } else {
            result = new OJField(getQualifiers.getBody(), "result", getQualifiers.getReturnType(), "Collections.emptyList()");
        }

        OJField runtimeProperty = new OJField(getQualifiers.getBody(), "runtimeProperty", new OJPathName(TumlClassOperations.propertyEnumName(clazz)));
        OJIfStatement ifInverse = new OJIfStatement("!inverse");
        ifInverse.addToThenPart("runtimeProperty = " + TumlClassOperations.propertyEnumName(clazz)
                + ".fromQualifiedName(tumlRuntimeProperty.getQualifiedName())");
        ifInverse.addToElsePart("runtimeProperty = " + TumlClassOperations.propertyEnumName(clazz)
                + ".fromQualifiedName(tumlRuntimeProperty.getInverseQualifiedName())");
        getQualifiers.getBody().addToStatements(ifInverse);

        OJIfStatement ifRuntimePropertyNotNull = new OJIfStatement(runtimeProperty.getName() + " != null && result.isEmpty()");
        getQualifiers.getBody().addToStatements(ifRuntimePropertyNotNull);

        OJSwitchStatement ojSwitchStatement = new OJSwitchStatement();
        ojSwitchStatement.setCondition("runtimeProperty");
        ifRuntimePropertyNotNull.addToThenPart(ojSwitchStatement);

        for (Property p : TumlClassOperations.getAllOwnedProperties(clazz)) {
            PropertyWrapper pWrap = new PropertyWrapper(p);
            if (pWrap.isQualified()) {
                OJSwitchCase ojSwitchCase = new OJSwitchCase();
                ojSwitchCase.setLabel(pWrap.fieldname());
                OJSimpleStatement statement = new OJSimpleStatement("result = " + pWrap.getQualifiedGetterName() + "((" + pWrap.getType().getName() + ")node)");
                statement.setName(pWrap.fieldname());
                ojSwitchCase.getBody().addToStatements(statement);
                annotatedClass.addToImports(pWrap.javaImplTypePath());
                ojSwitchStatement.addToCases(ojSwitchCase);
            }
        }
        OJSwitchCase ojSwitchCase = new OJSwitchCase();
        ojSwitchCase.getBody().addToStatements("result = Collections.emptyList()");
        ojSwitchStatement.setDefCase(ojSwitchCase);

        getQualifiers.getBody().addToStatements("return " + result.getName());
        annotatedClass.addToImports("java.util.Collections");
    }

    private void addGetSize(OJAnnotatedClass annotatedClass, Class clazz) {
        OJAnnotatedOperation getQualifiers = new OJAnnotatedOperation("getSize");
        TinkerGenerationUtil.addOverrideAnnotation(getQualifiers);
        getQualifiers.setComment("getSize is called from the collection in order to update the index used to implement a sequence's index");
        getQualifiers.addParam("tumlRuntimeProperty", TinkerGenerationUtil.tumlRuntimePropertyPathName.getCopy());
        getQualifiers.setReturnType(new OJPathName("int"));
        annotatedClass.addToOperations(getQualifiers);

        OJField result = null;
        if (!clazz.getGeneralizations().isEmpty()) {
            result = new OJField(getQualifiers.getBody(), "result", getQualifiers.getReturnType(), "super.getSize(tumlRuntimeProperty)");
        } else {
            result = new OJField(getQualifiers.getBody(), "result", getQualifiers.getReturnType(), "0");
        }

        OJField runtimeProperty = new OJField(getQualifiers.getBody(), "runtimeProperty", new OJPathName(TumlClassOperations.propertyEnumName(clazz)));
        runtimeProperty.setInitExp(TumlClassOperations.propertyEnumName(clazz) + ".fromQualifiedName(tumlRuntimeProperty.getQualifiedName())");

        OJIfStatement ifRuntimePropertyNotNull = new OJIfStatement(runtimeProperty.getName() + " != null && result == 0");
        getQualifiers.getBody().addToStatements(ifRuntimePropertyNotNull);

        OJSwitchStatement ojSwitchStatement = new OJSwitchStatement();
        ojSwitchStatement.setCondition("runtimeProperty");
        ifRuntimePropertyNotNull.addToThenPart(ojSwitchStatement);

        for (Property p : TumlClassOperations.getAllOwnedProperties(clazz)) {
            PropertyWrapper pWrap = new PropertyWrapper(p);
            if (!pWrap.isDerived()) {
                OJSwitchCase ojSwitchCase = new OJSwitchCase();
                ojSwitchCase.setLabel(pWrap.fieldname());
                OJSimpleStatement statement = new OJSimpleStatement("result = " + pWrap.fieldname() + ".size()");
                statement.setName(pWrap.fieldname());
                ojSwitchCase.getBody().addToStatements(statement);
                annotatedClass.addToImports(pWrap.javaImplTypePath());
                ojSwitchStatement.addToCases(ojSwitchCase);
            }
        }
        OJSwitchCase ojSwitchCase = new OJSwitchCase();
        ojSwitchCase.getBody().addToStatements("result = 0");
        ojSwitchStatement.setDefCase(ojSwitchCase);

        getQualifiers.getBody().addToStatements("return " + result.getName());
        annotatedClass.addToImports("java.util.Collections");
    }

    private void addGetMetaDataAsJson(OJAnnotatedClass annotatedClass, Class clazz) {
        OJAnnotatedOperation getMetaDataAsJSon = new OJAnnotatedOperation("getMetaDataAsJson", new OJPathName("String"));
        getMetaDataAsJSon.getBody().addToStatements("return " + TumlClassOperations.className(clazz) + "." + TumlClassOperations.propertyEnumName(clazz) + ".asJson()");
        annotatedClass.addToOperations(getMetaDataAsJSon);
        TinkerGenerationUtil.addOverrideAnnotation(getMetaDataAsJSon);
    }

}
