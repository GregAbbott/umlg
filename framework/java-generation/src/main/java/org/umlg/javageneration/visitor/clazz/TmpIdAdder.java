package org.umlg.javageneration.visitor.clazz;

import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.umlg.framework.VisitSubclasses;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.java.metamodel.OJField;
import org.umlg.java.metamodel.OJIfStatement;
import org.umlg.java.metamodel.OJPathName;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.java.metamodel.annotation.OJAnnotatedOperation;
import org.umlg.java.metamodel.generated.OJVisibilityKindGEN;
import org.umlg.javageneration.util.TinkerGenerationUtil;
import org.umlg.javageneration.visitor.BaseVisitor;

import java.util.List;

/**
 * Date: 2013/04/14
 * Time: 5:11 PM
 */
public class TmpIdAdder extends BaseVisitor implements Visitor<Class> {

    public TmpIdAdder(Workspace workspace) {
        super(workspace);
    }

    @Override
    @VisitSubclasses({Class.class, AssociationClass.class})
    public void visitBefore(Class clazz) {
        OJAnnotatedClass annotatedClass = findOJClass(clazz);
        List<Classifier> generals = clazz.getGenerals();
        if (generals.isEmpty()) {
            addTmpIdField(annotatedClass);
            addTmpIdToFromJson(annotatedClass);
            addTmpIdToToJson(annotatedClass);
        }

    }

    private void addTmpIdToToJson(OJAnnotatedClass annotatedClass) {
        OJAnnotatedOperation toJson = annotatedClass.findOperation("toJson", new OJPathName("Boolean"));
        OJIfStatement ifTmpNull = new OJIfStatement("this.tmpId != null");
        //Insert the line at second line
        ifTmpNull.addToThenPart("sb.append(\"\\\"tmpId\\\": \\\"\" + this.tmpId + \"\\\", \")");
        toJson.getBody().getStatements().add(1, ifTmpNull);

        OJAnnotatedOperation toJsonWithoutCompositeParent = annotatedClass.findOperation("toJsonWithoutCompositeParent", new OJPathName("Boolean"));
        //Insert the line at second line
        toJsonWithoutCompositeParent.getBody().getStatements().add(1, ifTmpNull);
    }

    private void addTmpIdToFromJson(OJAnnotatedClass annotatedClass) {
        OJAnnotatedOperation fromJson = annotatedClass.findOperation("fromJsonDataTypeAndComposite", new OJPathName("java.util.Map"));
        OJIfStatement  ifStatement = new OJIfStatement("propertyMap.containsKey(\"tmpId\")");
        OJIfStatement ifStatement1 = new OJIfStatement("propertyMap.get(\"tmpId\") != null");
        ifStatement.addToThenPart(ifStatement1);
        ifStatement1.addToThenPart("this.tmpId = (String)propertyMap.get(\"tmpId\")");
        ifStatement1.addToThenPart(TinkerGenerationUtil.UmlgTmpIdManager.getLast() + ".INSTANCE.put(this.tmpId, getId())");
        annotatedClass.addToImports(TinkerGenerationUtil.UmlgTmpIdManager);
        ifStatement1.addToElsePart("this.tmpId = null");
        fromJson.getBody().addToStatements(ifStatement);
    }

    //TODO make transient
    private void addTmpIdField(OJAnnotatedClass annotatedClass) {
        OJField tmpId = new OJField("tmpId", "String");
        tmpId.setComment("tmpId is only used the umlg restlet gui. It is never persisted. Its value is generated by the gui.");
        tmpId.setVisibility(OJVisibilityKindGEN.PRIVATE);
        annotatedClass.addToFields(tmpId);
    }

    @Override
    public void visitAfter(Class element) {
    }
}
