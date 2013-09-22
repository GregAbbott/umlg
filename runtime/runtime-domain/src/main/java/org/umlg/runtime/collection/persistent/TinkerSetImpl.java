package org.umlg.runtime.collection.persistent;

import org.umlg.runtime.collection.TinkerSet;
import org.umlg.runtime.collection.TumlRuntimeProperty;
import org.umlg.runtime.domain.UmlgNode;

import java.util.Set;

public class TinkerSetImpl<E> extends BaseSet<E> implements TinkerSet<E> {

	public TinkerSetImpl(UmlgNode owner, TumlRuntimeProperty runtimeProperty) {
		super(owner, runtimeProperty);
	}
	
	public Set<E> getInternalSet() {
		return (Set<E>) this.internalCollection;
	}

}
