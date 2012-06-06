package org.tuml.runtime.collection;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.runtime.adaptor.TransactionThreadEntityVar;
import org.tuml.runtime.adaptor.TransactionThreadVar;
import org.tuml.runtime.domain.CompositionNode;
import org.tuml.runtime.domain.TinkerAuditableNode;
import org.tuml.runtime.domain.TinkerNode;
import org.tuml.runtime.util.TinkerFormatter;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;

public abstract class BaseCollection<E> implements Collection<E>, TumlRuntimeProperty {

	protected Collection<E> internalCollection;
	// protected boolean composite;
	// On a compositional association inverse is true for the set children
	// protected boolean controllingSide;
	// protected boolean manyToMany;
	protected boolean loaded = false;
	protected TinkerNode owner;
	// This is the vertex of the owner of the collection
	protected Vertex vertex;
	protected Class<?> parentClass;
	protected Map<Object, Vertex> internalVertexMap = new HashMap<Object, Vertex>();
	protected TumlRuntimeProperty tumlRuntimeProperty;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void loadFromVertex() {
		for (Edge edge : getEdges()) {
			E node = null;
			try {
				Class<?> c = this.getClassToInstantiate(edge);
				if (c.isEnum()) {
					Object value = this.getVertexForDirection(edge).getProperty("value");
					node = (E) Enum.valueOf((Class<? extends Enum>) c, (String) value);
					this.internalVertexMap.put(value, this.getVertexForDirection(edge));
				} else if (TinkerNode.class.isAssignableFrom(c)) {
					node = (E) c.getConstructor(Vertex.class).newInstance(this.getVertexForDirection(edge));
				} else {
					Object value = this.getVertexForDirection(edge).getProperty("value");
					node = (E) value;
					this.internalVertexMap.put(value, this.getVertexForDirection(edge));
				}
				this.internalCollection.add(node);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		this.loaded = true;
	}

	protected Iterable<Edge> getEdges() {
		if (this.isControllingSide()) {
			return this.vertex.getOutEdges(this.getLabel());
		} else {
			return this.vertex.getInEdges(this.getLabel());
		}
	}

	protected Iterable<Edge> getEdges(Vertex v) {
		if (!this.isControllingSide()) {
			return v.getOutEdges(this.getLabel());
		} else {
			return v.getInEdges(this.getLabel());
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		maybeLoad();
		boolean result = true;
		for (E e : c) {
			if (!this.add(e)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public boolean add(E e) {
		//validateMultiplicityForAdditionalElement calls size() which loads the collection
		validateMultiplicityForAdditionalElement();
		maybeCallInit(e);
		boolean result = this.internalCollection.add(e);
		if (result) {
			addInternal(e);
		}
		return result;
	}

	private void validateMultiplicityForAdditionalElement() {
		if (!isValid(size() + 1)) {
			throw new IllegalStateException(String.format("The collection's multiplicity is (lower = %s, upper = %s). Current size = %s. It can not accept another element.", getLower(), getUpper(), size()));
		}
	}

	@Override
	public boolean remove(Object o) {
		maybeLoad();
		boolean result = this.internalCollection.remove(o);
		if (result) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			Vertex v;
			if (o instanceof TinkerNode) {
				TinkerNode node = (TinkerNode) o;
				v = node.getVertex();
				
				if (node instanceof CompositionNode) {
					TransactionThreadEntityVar.setNewEntity((CompositionNode) node);
				}
				
				Set<Edge> edges = GraphDb.getDb().getEdgesBetween(this.vertex, v, this.getLabel());
				for (Edge edge : edges) {
					if (o instanceof TinkerAuditableNode) {
						createAudit(e, v, true);
					}
					GraphDb.getDb().removeEdge(edge);
					break;
				}
			} else if (o.getClass().isEnum()) {
				v = this.internalVertexMap.get(((Enum<?>) o).name());
				GraphDb.getDb().removeVertex(v);
			} else {
				v = this.internalVertexMap.get(o);
				if (this.owner instanceof TinkerAuditableNode) {
					createAudit(e, v, true);
				}
				GraphDb.getDb().removeVertex(v);
			}
		}
		return result;
	}

	protected Edge addInternal(E e) {
		Vertex v = null;
		if (e instanceof TinkerNode) {
			TinkerNode node = (TinkerNode) e;
			if (e instanceof CompositionNode) {
				TransactionThreadEntityVar.setNewEntity((CompositionNode) node);
			}
			v = node.getVertex();
			if (this.isOneToMany() || this.isOneToOne()) {
				// Remove the existing one from the element if it exist
				Iterator<Edge> iteratorToOne = getEdges(v).iterator();
				if (iteratorToOne.hasNext()) {
					GraphDb.getDb().removeEdge(iteratorToOne.next());
					node.initialiseProperty(this.tumlRuntimeProperty);
				}
			}
		} else if (e.getClass().isEnum()) {
			v = GraphDb.getDb().addVertex(null);
			v.setProperty("value", ((Enum<?>) e).name());
			this.internalVertexMap.put(((Enum<?>) e).name(), v);
		} else if (isOnePrimitive(e)) {
			this.vertex.setProperty(tumlRuntimeProperty.getLabel(), e);
		} else {
			v = GraphDb.getDb().addVertex(null);
			v.setProperty("value", e);
			this.internalVertexMap.put(e, v);
		}
		if (v !=null) {
			Edge edge = null;
			// See if edge already added, this can only happen with a manyToMany
			if (this.isManyToMany()) {
				edge = addCorrelationForManyToMany(v, edge);
			}
			boolean createdEdge = false;
			if (edge == null) {
				createdEdge = true;
				edge = createEdge(e, v);
			}
			if (createdEdge && this.owner instanceof TinkerAuditableNode) {
				createAudit(e, v, false);
			}
			return edge;
		} else {
			return null;
		}
		
	}

	protected boolean isOnePrimitive(E e) {
		//primitive properties on a classifier is manytoone as the otherside is null it defaults to many
		return this.isManyToOne() && (e.getClass() == String.class || e.getClass() == Integer.class || e.getClass() == Boolean.class || e.getClass().isPrimitive());
	}

	private Edge addCorrelationForManyToMany(Vertex v, Edge edge) {
		Set<Edge> edgesBetween = GraphDb.getDb().getEdgesBetween(this.vertex, v, this.getLabel());
		// Only a sequence can have duplicates
		if (this instanceof TinkerSequence || this instanceof TinkerBag) {
			for (Edge edgeBetween : edgesBetween) {

				if (edgeBetween.getProperty("manyToManyCorrelationInverseTRUE") != null && edgeBetween.getProperty("manyToManyCorrelationInverseFALSE") != null) {
					// A new edge must be created as this is a duplicate
				} else if (edgeBetween.getProperty("manyToManyCorrelationInverseTRUE") == null && edgeBetween.getProperty("manyToManyCorrelationInverseFALSE") == null) {
					throw new IllegalStateException();
				} else if (edgeBetween.getProperty("manyToManyCorrelationInverseTRUE") == null && edgeBetween.getProperty("manyToManyCorrelationInverseFALSE") != null) {
					edge = edgeBetween;
					if (!this.isControllingSide()) {
						throw new IllegalStateException();
					}
					edge.setProperty("manyToManyCorrelationInverseTRUE", "SETTED");
					break;
				} else if (edgeBetween.getProperty("manyToManyCorrelationInverseTRUE") != null && edgeBetween.getProperty("manyToManyCorrelationInverseFALSE") == null) {
					edge = edgeBetween;
					if (this.isControllingSide()) {
						throw new IllegalStateException();
					}
					edge.setProperty("manyToManyCorrelationInverseFALSE", "SETTED");
					break;
				}

			}
		} else {
			if (!edgesBetween.isEmpty()) {
				if (edgesBetween.size() != 1) {
					throw new IllegalStateException("A set can only have one edge between the two ends");
				}
				edge = edgesBetween.iterator().next();
			}
		}
		return edge;
	}

	private Edge createEdge(E e, Vertex v) {
		Edge edge;
		if (this.isControllingSide()) {
			edge = GraphDb.getDb().addEdge(null, this.vertex, v, this.getLabel());
			edge.setProperty("outClass", this.parentClass.getName());
			edge.setProperty("inClass", e.getClass().getName());
			if (this.isManyToMany()) {
				edge.setProperty("manyToManyCorrelationInverseTRUE", "SETTED");
			}
		} else {
			edge = GraphDb.getDb().addEdge(null, v, this.vertex, this.getLabel());
			edge.setProperty("outClass", e.getClass().getName());
			edge.setProperty("inClass", this.parentClass.getName());
			edge.setProperty("manyToManyCorrelationInverseFALSE", "SETTED");
		}
		return edge;
	}

	protected void createAudit(E e, Vertex v, boolean deletion) {
		if (!(owner instanceof TinkerAuditableNode)) {
			throw new IllegalStateException("if the collection member is an TinkerAuditableNode, then the owner must be a TinkerAuditableNode!");
		}
		TinkerAuditableNode auditOwner = (TinkerAuditableNode) owner;
		if (TransactionThreadVar.hasNoAuditEntry(owner.getClass().getName() + owner.getUid())) {
			auditOwner.createAuditVertex(false);
		}
		if (e instanceof TinkerAuditableNode) {
			TinkerAuditableNode node = (TinkerAuditableNode) e;
			if (TransactionThreadVar.hasNoAuditEntry(node.getClass().getName() + node.getUid())) {
				node.createAuditVertex(false);
			}
			Edge auditEdge = GraphDb.getDb().addEdge(null, auditOwner.getAuditVertex(), node.getAuditVertex(), this.getLabel());
			auditEdge.setProperty("outClass", auditOwner.getClass().getName() + "Audit");
			auditEdge.setProperty("inClass", node.getClass().getName() + "Audit");
			if (deletion) {
				auditEdge.setProperty("deletedOn", TinkerFormatter.format(new Date()));
			}
		} else if (e.getClass().isEnum()) {

		} else {
			if (TransactionThreadVar.hasNoAuditEntry(owner.getClass().getName() + e.getClass().getName() + e.toString())) {
				Vertex auditVertex = GraphDb.getDb().addVertex(null);
				auditVertex.setProperty("value", e);
				TransactionThreadVar.putAuditVertexFalse(owner.getClass().getName() + e.getClass().getName() + e.toString(), auditVertex);
				auditVertex.setProperty("transactionNo", GraphDb.getDb().getTransactionCount());
				Edge auditEdge = GraphDb.getDb().addEdge(null, auditOwner.getAuditVertex(), auditVertex, this.getLabel());
				auditEdge.setProperty("transactionNo", GraphDb.getDb().getTransactionCount());
				auditEdge.setProperty("outClass", this.parentClass.getName());
				auditEdge.setProperty("inClass", e.getClass().getName() + "Audit");
				if (deletion) {
					auditEdge.setProperty("deletedOn", TinkerFormatter.format(new Date()));
				}
			}
		}
	}

	protected void maybeCallInit(E e) {
		if (this.isComposite() && e instanceof CompositionNode && !((CompositionNode) e).hasInitBeenCalled()) {
			((CompositionNode) e).init(this.owner);
		}
	}

	protected void maybeLoad() {
		if (!this.loaded) {
			loadFromVertex();
		}
	}

	protected Vertex getVertexForDirection(Edge edge) {
		if (this.isControllingSide()) {
			return edge.getInVertex();
		} else {
			return edge.getOutVertex();
		}
	}

	protected Class<?> getClassToInstantiate(Edge edge) {
		try {
			if (this.isControllingSide()) {
				return Class.forName((String) edge.getProperty("inClass"));
			} else {
				return Class.forName((String) edge.getProperty("outClass"));
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int size() {
		maybeLoad();
		return this.internalCollection.size();
	}

	@Override
	public boolean isEmpty() {
		maybeLoad();
		return this.internalCollection.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		maybeLoad();
		return this.internalCollection.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		maybeLoad();
		return this.internalCollection.iterator();
	}

	@Override
	public Object[] toArray() {
		maybeLoad();
		return this.internalCollection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		maybeLoad();
		return this.internalCollection.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		maybeLoad();
		return this.internalCollection.containsAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (!this.loaded) {
			loadFromVertex();
		}
		boolean result = true;
		for (E e : this.internalCollection) {
			if (!c.contains(e)) {
				if (!this.remove(e)) {
					result = false;
				}
			}
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		maybeLoad();
		boolean result = true;
		for (Object object : c) {
			if (!this.remove(object)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public String getLabel() {
		return this.tumlRuntimeProperty.getLabel();
	}

	@Override
	public boolean isOneToOne() {
		return this.tumlRuntimeProperty.isOneToOne();
	}

	@Override
	public boolean isOneToMany() {
		return this.tumlRuntimeProperty.isOneToMany();
	}

	@Override
	public boolean isManyToOne() {
		return this.tumlRuntimeProperty.isManyToOne();
	}

	@Override
	public boolean isManyToMany() {
		return this.tumlRuntimeProperty.isManyToMany();
	}

	@Override
	public int getUpper() {
		return this.tumlRuntimeProperty.getUpper();
	}

	@Override
	public int getLower() {
		return this.tumlRuntimeProperty.getLower();
	}

	@Override
	public boolean isControllingSide() {
		return this.tumlRuntimeProperty.isControllingSide();
	}

	@Override
	public boolean isComposite() {
		return this.tumlRuntimeProperty.isComposite();
	}

	@Override
	public boolean isValid(int elementCount) {
		return this.tumlRuntimeProperty.isValid(elementCount);
	}
}