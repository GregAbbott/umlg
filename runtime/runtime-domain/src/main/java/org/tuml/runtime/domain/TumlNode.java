package org.tuml.runtime.domain;

import java.util.List;

import org.tuml.runtime.collection.Qualifier;
import org.tuml.runtime.collection.TumlRuntimeProperty;
import org.tuml.runtime.domain.ocl.OclAny;

import com.tinkerpop.blueprints.Vertex;

public interface TumlNode extends OclAny, PersistentObject {
	Vertex getVertex();
	boolean isTinkerRoot();
	void initialiseProperties();
	void initialiseProperty(TumlRuntimeProperty tumlRuntimeProperty);
	List<Qualifier> getQualifiers(TumlRuntimeProperty tumlRuntimeProperty, TumlNode node);
	void delete();
	int getSize(TumlRuntimeProperty tumlRuntimeProperty);
}