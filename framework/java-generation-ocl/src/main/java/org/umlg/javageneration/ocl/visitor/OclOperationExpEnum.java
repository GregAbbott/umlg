package org.umlg.javageneration.ocl.visitor;

import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.utilities.PredefinedType;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.javageneration.ocl.visitor.tojava.*;

import java.util.List;
import java.util.logging.Logger;

public enum OclOperationExpEnum implements HandleOperationExp {

    ALL_INSTANCES(new OclAllInstancesExprToJava()), UNION(new OclUnionExprToJava()), IS_EMPTY(new OclIsEmptyExprToJava()), OCL_IS_UNDEFINED(new OclOclIsUndefinedExpToJava()), OCL_IS_INVALID(
            new OclOclIsInvalidExpToJava()), INCLUDING(new OclIncludingExprToJava()), TO_STRING(new OclToStringExprToJava()), FIRST(new OclFirstExprToJava()), LAST(new OclLastExprToJava()),
    AT(new OclAtExprToJava()),
    MINUS(new OclMinusExprToJava()), EQUAL(new OclEqualExprToJava()), SIZE(new OclSizeExprToJava()), NOT_EQUAL(new OclNotEqualExprToJava()), AS_SET(
            new OclAsSetExprToJava()), AS_SEQUENCE(new OclAsSequenceExprToJava()), AS_ORDERED_SET(new OclAsOrderedSetExprToJava()), AS_BAG(
            new OclAsBagExprToJava()), FLATTEN(new OclFlattenExprToJava()), CONCAT(new OclConcatExprToJava()), INCLUDES(new OclIncludesExpToJava()), DEFAULT(new OclDefaultToStringExprToJava()),
            INDEX_OF(new OclIndexOfExprToJava());
    private static Logger logger = Logger.getLogger(OclOperationExpEnum.class.getPackage().getName());
    private HandleOperationExp implementor;
    private OJAnnotatedClass ojClass;

    private OclOperationExpEnum(HandleOperationExp implementor) {
        this.implementor = implementor;
    }

    public static OclOperationExpEnum from(String name) {
        if (name.equals(PredefinedType.EQUAL_NAME)) {
            return EQUAL;
        } else if (name.equals(PredefinedType.ALL_INSTANCES_NAME)) {
            return ALL_INSTANCES;
        } else if (name.equals(PredefinedType.NOT_EQUAL_NAME)) {
            return NOT_EQUAL;
        } else if (name.equals(PredefinedType.AS_SET_NAME)) {
            return AS_SET;
        } else if (name.equals(PredefinedType.AS_SEQUENCE_NAME)) {
            return AS_SEQUENCE;
        } else if (name.equals(PredefinedType.AS_ORDERED_SET_NAME)) {
            return AS_ORDERED_SET;
        } else if (name.equals(PredefinedType.AS_BAG_NAME)) {
            return AS_BAG;
        } else if (name.equals(PredefinedType.FLATTEN_NAME)) {
            return FLATTEN;
        } else if (name.equals(PredefinedType.CONCAT_NAME)) {
            return CONCAT;
        } else if (name.equals(PredefinedType.MINUS_NAME)) {
            return MINUS;
        } else if (name.equals(PredefinedType.FIRST_NAME)) {
            return FIRST;
        } else if (name.equals(PredefinedType.LAST_NAME)) {
            return LAST;
        } else if (name.equals(PredefinedType.AT_NAME)) {
            return AT;
            // } else if (name.equals(PredefinedType.TO_STRING_NAME)) {
            // return TO_STRING;
        } else if (name.equals(PredefinedType.INCLUDING_NAME)) {
            return INCLUDING;
        } else if (name.equals(PredefinedType.OCL_IS_INVALID_NAME)) {
            return OCL_IS_INVALID;
        } else if (name.equals(PredefinedType.OCL_IS_UNDEFINED_NAME)) {
            return OCL_IS_UNDEFINED;
        } else if (name.equals(PredefinedType.IS_EMPTY_NAME)) {
            return IS_EMPTY;
        } else if (name.equals(PredefinedType.UNION_NAME)) {
            return UNION;
        } else if (name.equals(PredefinedType.SIZE_NAME)) {
            return SIZE;
        } else if (name.equals(PredefinedType.INDEX_OF_NAME)) {
            return INDEX_OF;
        } else if (name.equals(PredefinedType.INCLUDES_NAME)) {
            return INCLUDES;
        } else {
            logger.warning(String.format("Not yet implemented, '%s'", name));
            return DEFAULT;
        }
    }

    @Override
    public String handleOperationExp(OperationCallExp<Classifier, Operation> oc, String sourceResult, List<String> argumentResults) {
        return implementor.setOJClass(ojClass).handleOperationExp(oc, sourceResult, argumentResults);
    }

    @Override
    public HandleOperationExp setOJClass(OJAnnotatedClass ojClass) {
        this.ojClass = ojClass;
        return this;
    }
}