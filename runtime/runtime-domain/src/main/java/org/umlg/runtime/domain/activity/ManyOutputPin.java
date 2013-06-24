package org.umlg.runtime.domain.activity;

import com.tinkerpop.blueprints.Vertex;
import org.umlg.runtime.domain.activity.interf.IManyOutputPin;

import java.util.Collections;
import java.util.List;

public abstract class ManyOutputPin<O> extends OutputPin<O, CollectionObjectToken<O>> implements IManyOutputPin<O> {

	public ManyOutputPin() {
		super();
	}

	public ManyOutputPin(boolean persist, String name) {
		super(persist, name);
	}

	public ManyOutputPin(Vertex vertex) {
		super(vertex);
	}
	
	@Override
	public abstract List<ManyObjectFlowKnown<O>> getIncoming();

	@Override
	public List<ManyObjectFlowKnown<O>> getOutgoing() {
		return Collections.emptyList();
	}
	
	@Override
	protected int countNumberOfElementsOnTokens() {
		int size = 0;
		List<CollectionObjectToken<O>> tokens = getOutTokens();
		for (CollectionObjectToken<O> collectionObjectToken : tokens) {
			size += collectionObjectToken.getElements().size();
		}
		return size;
	}	

}