package org.tuml.runtime.domain.neo4j;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.runtime.adaptor.TransactionThreadEntityVar;
import org.tuml.runtime.adaptor.TransactionThreadVar;
import org.tuml.runtime.domain.BaseTinkerAuditable;
import org.tuml.runtime.domain.CompositionNode;
import org.tuml.runtime.domain.TinkerNode;

public class NakedTransactionEventHandler<T> implements TransactionEventHandler<T> {

	Validator validator;
	
	public NakedTransactionEventHandler(Validator validator) {
		super();
		this.validator = validator;
	}

	@Override
	public T beforeCommit(TransactionData data) throws Exception {
		if (!isEmpty(data)) {
			Set<ConstraintViolation<TinkerNode>> constraintViolations = new HashSet<ConstraintViolation<TinkerNode>>();
			TransactionThreadVar.clear();
			GraphDb.incrementTransactionCount();
			List<CompositionNode> entities = TransactionThreadEntityVar.get();
			for (CompositionNode entity : entities) {
				constraintViolations.addAll(validator.validate((TinkerNode)entity));
				if (!entity.isTinkerRoot() && entity.getOwningObject() == null) {

					if (entity instanceof BaseTinkerAuditable && ((BaseTinkerAuditable) entity).getDeletedOn().before(new Date())) {
						return null;
					}
					TransactionThreadEntityVar.clear();
					throw new IllegalStateException(String.format("Entity %s %s does not have a composite owner", entity.getClass().getSimpleName(), entity.getId()));

				}
			}
			TransactionThreadEntityVar.clear();
			if (!constraintViolations.isEmpty()) {
				throw new IllegalStateException("Constraint violations, need to pass violations along//TODO");
			}
		}
		return null;
	}

	private boolean isEmpty(TransactionData data) {
		return !data.assignedNodeProperties().iterator().hasNext() && !data.assignedRelationshipProperties().iterator().hasNext()
				&& !data.createdNodes().iterator().hasNext() && !data.createdRelationships().iterator().hasNext() && !data.deletedNodes().iterator().hasNext()
				&& !data.deletedRelationships().iterator().hasNext() && !data.removedNodeProperties().iterator().hasNext()
				&& !data.removedRelationshipProperties().iterator().hasNext();
	}

	@Override
	public void afterCommit(TransactionData data, T state) {

	}

	@Override
	public void afterRollback(TransactionData data, T state) {

	}

}
