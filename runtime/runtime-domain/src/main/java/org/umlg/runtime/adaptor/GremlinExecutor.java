package org.umlg.runtime.adaptor;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.readonly.ReadOnlyGraph;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.Pipeline;
import com.tinkerpop.pipes.util.iterators.SingleIterator;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang.time.StopWatch;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * Date: 2013/06/09
 * Time: 8:34 PM
 */
public class GremlinExecutor {

    public static String executeGremlinQuery(Long contextId, String gremlin) {
        StringBuilder result = new StringBuilder();
        Pipe pipe = Gremlin.compile("_()." + gremlin);
        GremlinToStringPipe<String> toStringPipe = new GremlinToStringPipe(new SingleIterator<Object>(pipe));
        return toStringPipe.toString();
    }

    /**
     * Executes a gremlin query. If the contextId is null then it is ignored.
     * If it is not null then all instances of the keywork "this" will be replaced with "g.v(contextId)"
     * @param contextId
     * @param gremlin
     * @return
     */
    public static String executeGremlinViaGroovy(Long contextId, String gremlin) {
        if (contextId != null) {
            gremlin = gremlin.replace("this", "g.v(" + contextId + ")");
        }
        Graph graph = new ReadOnlyGraph(GraphDb.getDb());
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass("org.umlg.runtime.adaptor.GremlinExecutorBaseClass");
        Binding binding = new Binding();
        binding.setVariable("g", graph);
        GroovyShell shell = new GroovyShell(binding, compilerConfiguration);
        Object pipe = shell.evaluate("return " + gremlin + ";");
        GremlinToStringPipe<String> toStringPipe = new GremlinToStringPipe(new SingleIterator<Object>(pipe));
        return toStringPipe.toString();
    }

}
