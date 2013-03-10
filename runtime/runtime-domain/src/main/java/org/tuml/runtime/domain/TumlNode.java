package org.tuml.runtime.domain;

import com.tinkerpop.blueprints.Vertex;
import org.tuml.runtime.collection.Qualifier;
import org.tuml.runtime.collection.TinkerSet;
import org.tuml.runtime.collection.TumlRuntimeProperty;
import org.tuml.runtime.domain.ocl.OclAny;
import org.tuml.runtime.validation.TumlConstraintViolation;

import java.util.List;

public interface TumlNode extends TumlEnum, OclAny, PersistentObject {
    public static final String ALLINSTANCES_EDGE_LABEL = "allinstances";
	Vertex getVertex();
	boolean isTinkerRoot();
	void initialiseProperties();
	void initialiseProperty(TumlRuntimeProperty tumlRuntimeProperty, boolean inverse);
	List<Qualifier> getQualifiers(TumlRuntimeProperty tumlRuntimeProperty, TumlNode node, boolean inverse);
	void delete();
	int getSize(TumlRuntimeProperty tumlRuntimeProperty);
	<E> TinkerSet<E> asSet();
	List<TumlConstraintViolation> validateMultiplicities();
    List<TumlConstraintViolation> checkClassConstraints();
	TumlNode getOwningObject();
	<T extends TumlNode> List<T> getPathToCompositionalRoot();
    void addEdgeToMetaNode();
    TumlMetaNode getMetaNode();
}
