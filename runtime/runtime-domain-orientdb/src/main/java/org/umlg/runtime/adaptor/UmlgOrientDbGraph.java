package org.umlg.runtime.adaptor;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.umlg.runtime.collection.memory.UmlgLazyList;
import org.umlg.runtime.domain.PersistentObject;
import org.umlg.runtime.domain.UmlgApplicationNode;
import org.umlg.runtime.domain.UmlgNode;
import org.umlg.runtime.util.UmlgProperties;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Date: 2013/01/09
 * Time: 7:25 PM
 */
public class UmlgOrientDbGraph extends OrientGraph implements UmlgGraph {

    private static final Logger logger = Logger.getLogger(UmlgOrientDbGraph.class.getPackage().getName());
    private static final String VERTEX_ID_COUNT = "vertexIdCount";
    private UmlgTransactionEventHandler transactionEventHandler;
    private Class<UmlgApplicationNode> umlgApplicationNodeClass;

    public UmlgOrientDbGraph(Configuration configuration) {
        super(configuration);
        this.transactionEventHandler = new UmlgTransactionEventHandlerImpl();
    }

    /** Generic for all graphs start */
    @Override
    public void incrementTransactionCount() {
        this.getRoot().setProperty("transactionCount", (Integer) this.getRoot().getProperty("transactionCount") + 1);
    }

    @Override
    public long getTransactionCount() {
        return this.getRoot().getProperty("transactionCount");
    }

    @Override
    public void addRoot() {
        Vertex root = addVertex(ROOT_VERTEX);
        root.setProperty("transactionCount", 1);
        root.setProperty("className", ROOT_CLASS_NAME);
    }

    @Override
    public void commit() {
        try {
            this.transactionEventHandler.beforeCommit();
            super.commit();
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

    @Override
    public void rollback() {
        try {
            super.rollback();
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

//    If using orientdb class schema
//    @Override
//    public Vertex addVertex(String className) {
//        className = className.replace(".", "_");
//        Vertex v;
//        if (className != null) {
//            v = super.addVertex("class:" + className);
//            v.setProperty("className", className);
//        } else {
//            v = super.addVertex(null);
//        }
//        return v;
//    }

    @Override
    public Vertex addVertex(String className) {
        Vertex v = super.addVertex(null);
        if (className != null) {
            v.setProperty("className", className);
        }
        return v;
    }

    @Override
    public void addDeletionNode() {
        Vertex v = addVertex(DELETION_VERTEX);
        addEdge(null, getRoot(), v, DELETED_VERTEX_EDGE);
    }

    private Vertex getDeletionVertex() {
        if (getRoot() != null && getRoot().getEdges(Direction.OUT, DELETED_VERTEX_EDGE).iterator().hasNext()) {
            return getRoot().getEdges(Direction.OUT, DELETED_VERTEX_EDGE).iterator().next().getVertex(Direction.IN);
        } else {
            return null;
        }
    }

    @Override
    public <T> T instantiateClassifier(Object id) {
        try {
            Vertex v = this.getVertex(id);
            if (v == null) {
                throw new RuntimeException(String.format("No vertex found for id %d", new Object[]{id}));
            }
            return instantiateClassifier(v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T instantiateClassifier(Vertex v) {
        try {
            // TODO reimplement schemaHelper
            String className = v.getProperty("className");
            Class<?> c = Class.forName(className);
            return (T) c.getConstructor(Vertex.class).newInstance(v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PersistentObject getFromUniqueIndex(String indexKey, Object indexValue) {
        Iterator<Vertex> iterator = query().has(indexKey, indexValue).vertices().iterator();
        if ( iterator.hasNext() ) {
            return instantiateClassifier(iterator.next());
        } else {
            return null;
        }
    }

    @Override
    public List<PersistentObject> getFromIndex(String indexKey, Object indexValue) {
        final Iterator<Vertex> iterator = query().has(indexKey, indexValue).vertices().iterator();
        List<PersistentObject> lazy = new UmlgLazyList(iterator);
        return lazy;
    }

    @Override
    public UmlgApplicationNode getUmlgApplicationNode() {
        try {
            if (this.umlgApplicationNodeClass == null) {
                this.umlgApplicationNodeClass = (Class<UmlgApplicationNode>) Thread.currentThread().getContextClassLoader().loadClass(UmlgProperties.INSTANCE.getModelJavaName());
            }
            return (UmlgApplicationNode) this.umlgApplicationNodeClass.getField("INSTANCE").get(null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Generic for all graphs end */

    @Override
    public String executeQueryToString(UmlgQueryEnum umlgQueryEnum, Object contextId, String query) {

        switch (umlgQueryEnum) {
            case OCL:
                try {
                    Class<?> umlgOclExecutor= Class.forName("org.umlg.ocl.UmlgOclExecutor");
                    Method method = umlgOclExecutor.getMethod("executeOclQueryAsJson", UmlgNode.class, String.class);
                    UmlgNode context = (UmlgNode) UMLG.get().instantiateClassifier(contextId);
                    String json = (String) method.invoke(null, context, query);
                    return json;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("UmlgOclExecutor is not on the class path.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case GROOVY:
                String result;
                if (contextId != null) {
                    result = GroovyExecutor.INSTANCE.executeGroovyAsString(contextId, query);
                } else {
                    result = GroovyExecutor.INSTANCE.executeGroovyAsString(null, query);
                }
                return result;
            case NATIVE:
                throw new RuntimeException("Not yet implemented!");
            default:
                throw new RuntimeException("Unknown query enum");
        }
    }

    @Override
    public Object executeQuery(UmlgQueryEnum umlgQueryEnum, Object contextId, String query) {

        switch (umlgQueryEnum) {
            case OCL:
                try {
                    Class<?> umlgOclExecutor= Class.forName("org.umlg.ocl.UmlgOclExecutor");
                    Method method = umlgOclExecutor.getMethod("executeOclQuery", UmlgNode.class, String.class);
                    UmlgNode context = (UmlgNode) UMLG.get().instantiateClassifier(contextId);
                    Object json = method.invoke(null, context, query);
                    return json;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("UmlgOclExecutor is not on the class path.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case GROOVY:
                Object result;
                if (contextId != null) {
                    result = GroovyExecutor.INSTANCE.executeGroovy(contextId, query);
                } else {
                    result = GroovyExecutor.INSTANCE.executeGroovy(null, query);
                }
                return result;
            case NATIVE:
                throw new RuntimeException("Not yet implemented!");
            default:
                throw new RuntimeException("Unknown query enum");
        }
    }


//        @Override
//    public Vertex getRoot() {
//        OClass cls = this.getRawGraph().getMetadata().getSchema().getClass(ROOT_VERTEX);
//        if (cls != null) {
//            int[] ids = cls.getClusterIds();
//            StringBuilder sb = new StringBuilder("#");
//            sb.append(String.valueOf(ids[0]));
//            sb.append(":0");
//            return getVertex(sb.toString());
//        } else {
//            return null;
//        }
//    }

    @Override
    public Vertex getRoot() {
        return getVertex("#9:0");
    }

    @Override
    public void removeVertex(final Vertex vertex) {
        this.autoStartTransaction();
        Iterable<Edge> edges = vertex.getEdges(Direction.BOTH);
        for (final Edge edge : edges) {
            edge.remove();
        }
        getDeletionVertex().addEdge(DELETED_VERTEX_EDGE, vertex);
        vertex.setProperty("deleted", true);
    }

    @Override
    public Set<Edge> getEdgesBetween(Vertex v1, Vertex v2, String... labels) {
        Set<Edge> result = new HashSet<Edge>();
        Iterable<Edge> edges = v1.getEdges(Direction.BOTH, labels);
        if (!v1.equals(v2)) {
            for (Edge edge : edges) {
                if (edge.getVertex(Direction.IN).equals(v2) || edge.getVertex(Direction.OUT).equals(v2)) {
                    result.add(edge);
                }
            }
        } else {
            for (Edge edge : edges) {
                if (edge.getVertex(Direction.IN).equals(v2) && edge.getVertex(Direction.OUT).equals(v2)) {
                    result.add(edge);
                }
            }
        }
        return result;
    }

    @Override
    public long countVertices() {
        int countDeletedNodes = 0;
        if (getDeletionVertex() != null) {
            for (Edge edge : getDeletionVertex().getEdges(com.tinkerpop.blueprints.Direction.OUT, DELETED_VERTEX_EDGE)) {
                countDeletedNodes++;
            }
        }
        return super.countVertices() - 2 - countDeletedNodes;
    }

    @Override
    public long countEdges() {
        int countDeletedNodes = 0;
        if (getDeletionVertex() != null) {
            for (Edge edge : getDeletionVertex().getEdges(com.tinkerpop.blueprints.Direction.OUT, DELETED_VERTEX_EDGE)) {
                countDeletedNodes++;
            }
        }
        return super.countEdges() - 1 - countDeletedNodes;
    }

    @Override
    public boolean hasEdgeBeenDeleted(Edge edge) {
        //This method is only a problem for neo4j indexes
        return false;
    }

    /**
     * This method does not work for orientdb's threading model.
     * After commit/rollback orientdb immediately starts a transaction.
     * the graph needs to be shutdown at the end of the thread workload.
     * @return
     */
    @Override
    public boolean isTransactionActive() {
        return false;
    }

    @Override
    public void shutdown() {
        try {
            this.commit();
        } catch (Exception e) {
            this.rollback();
        }
        super.shutdown();
    }

    @Override
    public void drop() {
        this.getRawGraph().drop();
        UmlgGraphManager.INSTANCE.deleteGraph();
    }

    @Override
    public void afterThreadContext() {
//        if (TransactionThreadEntityVar.get()!=null && TransactionThreadEntityVar.get().size()>0) {
//            throw new RuntimeException("wtf");
//        }
//        if (TransactionThreadMetaNodeVar.get()!=null && TransactionThreadMetaNodeVar.get().size()>0) {
//            throw new RuntimeException("wtf");
//        }
        TransactionThreadEntityVar.remove();
        TransactionThreadMetaNodeVar.remove();
        UmlgAssociationClassManager.remove();
        UMLG.get().shutdown();
        UMLG.remove();
    }

}
