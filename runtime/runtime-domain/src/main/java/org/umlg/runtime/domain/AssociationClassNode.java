package org.umlg.runtime.domain;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.umlg.runtime.collection.UmlgRuntimeProperty;

/**
 * Date: 2013/06/20
 * Time: 7:13 AM
 */
public interface AssociationClassNode extends PersistentObject {

    UmlgRuntimeProperty internalAdder(UmlgRuntimeProperty umlgRuntimeProperty, boolean inverse, UmlgNode umlgNode);
    void z_internalCopyOnePrimitivePropertiesToEdge(Edge edge);
    Vertex getVertex();

}
