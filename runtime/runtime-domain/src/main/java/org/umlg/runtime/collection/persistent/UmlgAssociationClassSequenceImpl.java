package org.umlg.runtime.collection.persistent;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.umlg.runtime.adaptor.UMLG;
import org.umlg.runtime.collection.UmlgCollection;
import org.umlg.runtime.collection.UmlgRuntimeProperty;
import org.umlg.runtime.domain.UmlgMetaNode;
import org.umlg.runtime.domain.UmlgNode;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

/**
 * This set is used for navigating from a class to its related association classes.
 * Date: 2013/06/22
 * Time: 10:08 AM
 */
public class UmlgAssociationClassSequenceImpl<AssociationClassNode> extends UmlgSequenceImpl<AssociationClassNode> {

    /**
     * This gets invoked from the opposite side in addInternal.
     * It is called before the edge is created so the new element will not be loaded by loadFromVertex
     *
     * @param e
     * @return
     */
    @Override
    public boolean inverseAdder(AssociationClassNode e) {
        if (!this.loaded) {
            //Do not call the regular loadFromVertex
            //Association classes are loaded via the edge to the member end.
            //On inverseAdder that edge exist but the association class vertex has not yet been set as we do not want
            //to load the AssociationClass from the db.
            //The point of the inverseAdder is to keep the current object in memory
            associationClassLoadFromVertex();
        }
        return this.internalCollection.add(e);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void associationClassLoadFromVertex() {
        if (!isOnePrimitive() && getDataTypeEnum() == null) {
            for (Iterator<Edge> iter = getEdges(); iter.hasNext(); ) {
                Edge edge = iter.next();
                if (edge.properties().toList().contains(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID)) {
                    AssociationClassNode node;
                    try {
                        Class<?> c = this.getClassToInstantiate(edge);
                        if (c.isEnum()) {
                            Object value = this.getVertexForDirection(edge).value(getPersistentName());
                            node = (AssociationClassNode) Enum.valueOf((Class<? extends Enum>) c, (String) value);
                            putToInternalMap(node, this.getVertexForDirection(edge));
                        } else if (UmlgNode.class.isAssignableFrom(c)) {
                            node = (AssociationClassNode) c.getConstructor(Vertex.class).newInstance(this.getVertexForDirection(edge));
                        } else {
                            Object value = this.getVertexForDirection(edge).value(getPersistentName());
                            node = (AssociationClassNode) value;
                            putToInternalMap(value, this.getVertexForDirection(edge));
                        }
                        this.internalCollection.add(node);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        } else if (getDataTypeEnum() != null && getDataTypeEnum().isDateTime()) {
            String s = this.vertex.value(getLabel());
            if (s != null) {
                AssociationClassNode property = (AssociationClassNode) new DateTime(s);
                this.internalCollection.add(property);
            }
        } else if (getDataTypeEnum() != null && getDataTypeEnum().isDate()) {
            String s = this.vertex.value(getLabel());
            if (s != null) {
                AssociationClassNode property = (AssociationClassNode) new LocalDate(s);
                this.internalCollection.add(property);
            }
        } else if (getDataTypeEnum() != null && getDataTypeEnum().isTime()) {
            String s = this.vertex.value(getLabel());
            if (s != null) {
                AssociationClassNode property = (AssociationClassNode) new LocalTime(s);
                this.internalCollection.add(property);
            }
        } else {
            AssociationClassNode property = this.vertex.value(getLabel());
            if (property != null) {
                this.internalCollection.add(property);
            }
        }
        this.loaded = true;
    }

    public UmlgAssociationClassSequenceImpl(UmlgNode owner, UmlgRuntimeProperty runtimeProperty) {
        super(owner, runtimeProperty);
    }

    @Override
    protected void loadNode(Edge edgeToElement, Vertex vertexToLoad, boolean hyperVertexEdge) {
        AssociationClassNode node;
        try {

            //Get the edges between the vertexToLoad and the owner vertex.
            //Take the first one.
            Set<Edge> edges = UMLG.get().getEdgesBetween(this.owner.getVertex(), vertexToLoad, getLabel());
            //Debug check
            if (edges.size() > 1) {
                throw new IllegalStateException("Only a bag can have multiple edges between vertices!");
            }
            Vertex associationClassVertex = null;
            for (Edge edge : edges) {
                Object value = edge.value(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID);
                associationClassVertex = UMLG.get().V(value).next();
            }

            Class<?> c;
            if (hyperVertexEdge) {
                c = Class.forName((String) associationClassVertex.value("className"));
                //This is a debug check
                //TODO optimize
                Vertex debugVertex = edgeToElement.inV().next();
                if (!debugVertex.equals(vertexToLoad)) {
                    throw new IllegalStateException("Vertexes should be the same, what is going on?");
                }
            } else {
                c = this.getClassToInstantiate(edgeToElement);
            }
            if (c.isEnum()) {
                Object value = vertexToLoad.value(getPersistentName());
                node = (AssociationClassNode) Enum.valueOf((Class<? extends Enum>) c, (String) value);
                putToInternalMap(node, vertexToLoad);
            } else if (UmlgMetaNode.class.isAssignableFrom(c)) {
                Method m = c.getDeclaredMethod("getInstance", new Class[0]);
                node = (AssociationClassNode) m.invoke(null);
            } else if (UmlgNode.class.isAssignableFrom(c)) {
                node = (AssociationClassNode) c.getConstructor(Vertex.class).newInstance(associationClassVertex);
            } else {
                Object value = vertexToLoad.value(getPersistentName());
                node = (AssociationClassNode) value;
                putToInternalMap(value, vertexToLoad);
            }
            this.getInternalList().add(node);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean add(AssociationClassNode e) {
        throw new RuntimeException("The collection to an association class is immutable!");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("The collection to an association class is immutable!");
    }

}
