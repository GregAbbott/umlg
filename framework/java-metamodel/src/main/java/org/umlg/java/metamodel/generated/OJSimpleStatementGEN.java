/*
 * File generated by Grasland Grammar Generator on Dec 23, 2006 7:26:03 PM
 */
package org.umlg.java.metamodel.generated;

import java.util.ArrayList;
import java.util.List;

import org.umlg.java.metamodel.OJElement;
import org.umlg.java.metamodel.OJSimpleStatement;
import org.umlg.java.metamodel.OJStatement;
import org.umlg.java.metamodel.utilities.InvariantError;


/** Class ...
 */
abstract public class OJSimpleStatementGEN extends OJStatement {
	private String f_expression = "";
	static protected boolean usesAllInstances = false;
	static protected List<OJSimpleStatement> allInstances = new ArrayList<OJSimpleStatement>();

	/** Constructor for OJSimpleStatementGEN
	 * 
	 * @param name 
	 * @param comment 
	 * @param expression 
	 */
	protected OJSimpleStatementGEN(String name, String comment, String expression) {
		super();
		super.setName(name);
		super.setComment(comment);
		this.setExpression(expression);
		if ( usesAllInstances ) {
			allInstances.add(((OJSimpleStatement)this));
		}
	}
	
	/** Default constructor for OJSimpleStatement
	 */
	protected OJSimpleStatementGEN() {
		super();
		if ( usesAllInstances ) {
			allInstances.add(((OJSimpleStatement)this));
		}
	}

	/** Implements the getter for feature '+ expression : String'
	 */
	public String getExpression() {
		return f_expression;
	}
	
	/** Implements the setter for feature '+ expression : String'
	 * 
	 * @param element 
	 */
	public void setExpression(String element) {
		if ( f_expression != element ) {
			f_expression = element;
		}
	}
	
	public void addToExpression(String s) {
		setExpression(getExpression() + s);
	}
	
	/** Checks all invariants of this object and returns a list of messages about broken invariants
	 */
	public List<InvariantError> checkAllInvariants() {
		List<InvariantError> result = new ArrayList<InvariantError>();
		return result;
	}
	
	/** Implements a check on the multiplicities of all attributes and association ends
	 */
	public List<InvariantError> checkMultiplicities() {
		List<InvariantError> result = new ArrayList<InvariantError>();
		return result;
	}
	
	/** Default toString implementation for OJSimpleStatement
	 */
	public String toString() {
		String result = "";
		result = super.toString();
		if ( this.getExpression() != null ) {
			result = result + " expression:" + this.getExpression();
		}
		return result;
	}
	
	/** Returns the default identifier for OJSimpleStatement
	 */
	public String getIdString() {
		String result = "";
		if ( this.getExpression() != null ) {
			result = result + this.getExpression();
		}
		return result;
	}
	
	/** Implements the OCL allInstances operation
	 */
	static public List allInstances() {
		if ( !usesAllInstances ) {
			throw new RuntimeException("allInstances is not implemented for ((OJSimpleStatement)this) class. Set usesAllInstances to true, if you want allInstances() implemented.");
		}
		return allInstances;
	}
	
	/** Returns a copy of this instance. True parts, i.e. associations marked
			'aggregate' or 'composite', and attributes, are copied as well. References to
			other objects, i.e. associations not marked 'aggregate' or 'composite', will not
			be copied. The returned copy will refer to the same objects as the original (this)
			instance.
	 */
	public OJElement getCopy() {
		OJSimpleStatement result = new OJSimpleStatement();
		this.copyInfoInto(result);
		return result;
	}
	
	/** Copies all attributes and associations of this instance into 'copy'.
			True parts, i.e. associations marked 'aggregate' or 'composite', and attributes, 
			are copied as well. References to other objects, i.e. associations not marked 
			'aggregate' or 'composite', will not be copied. The 'copy' will refer 
			to the same objects as the original (this) instance.
	 * 
	 * @param copy 
	 */
	public void copyInfoInto(OJSimpleStatement copy) {
		super.copyInfoInto(copy);
		copy.setExpression(getExpression());
	}

}