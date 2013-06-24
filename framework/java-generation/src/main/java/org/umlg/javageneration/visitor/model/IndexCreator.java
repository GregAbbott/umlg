package org.umlg.javageneration.visitor.model;

import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.umlg.framework.ModelLoader;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.java.metamodel.OJField;
import org.umlg.java.metamodel.OJIfStatement;
import org.umlg.java.metamodel.OJPackage;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.java.metamodel.annotation.OJAnnotatedOperation;
import org.umlg.javageneration.util.PropertyWrapper;
import org.umlg.javageneration.util.TinkerGenerationUtil;
import org.umlg.javageneration.visitor.BaseVisitor;

import java.util.List;

/**
 * Date: 2013/03/24
 * Time: 9:21 AM
 */
public class IndexCreator extends BaseVisitor implements Visitor<Model> {

    public IndexCreator(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void visitBefore(Model element) {
        OJAnnotatedClass indexCreator = new OJAnnotatedClass("IndexCreator");
        OJPackage ojPackage = new OJPackage(TinkerGenerationUtil.TumlRootPackage.toJavaString());
        indexCreator.setMyPackage(ojPackage);
        indexCreator.addToImplementedInterfaces(TinkerGenerationUtil.TumlIndexManager);
        addToSource(indexCreator);

        OJAnnotatedOperation createIndexes = new OJAnnotatedOperation("createIndexes");
        indexCreator.addToOperations(createIndexes);

        List<Property> qualifiers = ModelLoader.INSTANCE.getAllQualifiers();
        for (Property q : qualifiers) {
            PropertyWrapper ownerWrap = new PropertyWrapper((Property) q.getOwner());

            OJField index = new OJField("index", TinkerGenerationUtil.tinkerIndexPathName);
            createIndexes.getBody().addToLocals(index);
            createIndexes.getBody().addToStatements("index  = " + TinkerGenerationUtil.graphDbAccess + ".getIndex(\"" + ownerWrap.getQualifiedName() + "\", " + TinkerGenerationUtil.edgePathName.getLast() + ".class)");
            OJIfStatement ifIndexNull = new OJIfStatement("index == null");
            ifIndexNull.addToThenPart(TinkerGenerationUtil.graphDbAccess + ".createIndex(\"" + ownerWrap.getQualifiedName() + "\", " + TinkerGenerationUtil.edgePathName.getLast() + ".class)");
            createIndexes.getBody().addToStatements(ifIndexNull);

            indexCreator.addToImports(TinkerGenerationUtil.tinkerIndexPathName);
            indexCreator.addToImports(TinkerGenerationUtil.edgePathName);
            indexCreator.addToImports(TinkerGenerationUtil.graphDbPathName);
        }

    }

    @Override
    public void visitAfter(Model element) {
    }
}