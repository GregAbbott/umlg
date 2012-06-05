package org.tuml.runtime.collection;

import org.tuml.runtime.domain.TinkerNode;

import com.google.common.collect.HashMultiset;

public class TinkerBagImpl<E> extends BaseBag<E> implements TinkerBag<E> {

	public TinkerBagImpl(TinkerNode owner, TumlRuntimeProperty multiplicity) {
		super();
		this.internalCollection = HashMultiset.create();
		this.owner = owner;
		this.vertex = owner.getVertex();
		this.parentClass = owner.getClass();
		this.tumlRuntimeProperty = multiplicity;
	}

}
