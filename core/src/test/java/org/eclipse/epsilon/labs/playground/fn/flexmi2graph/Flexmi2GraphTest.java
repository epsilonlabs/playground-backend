package org.eclipse.epsilon.labs.playground.fn.flexmi2graph;

import static org.eclipse.epsilon.labs.playground.fn.GraphAssertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.eclipse.epsilon.labs.playground.fn.PlaygroundTest;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Flexmi2GraphTest extends PlaygroundTest {

    @Inject
    Flexmi2GraphClient client;

    @Test
    public void ccl() throws Exception {
        process("ccl.emf", "ccl.flexmi");
    }

    @Test
    public void dml() throws Exception {
        process("dml.emf", "dml.flexmi");
    }

    @Test
    public void stm() throws Exception {
        process("stm.emf", "stm.flexmi");
    }

    @Test
    public void filesystem() throws Exception {
        process("filesystem.emf", "filesystem.flexmi");
    }

    @Test
    public void psl() throws Exception {
        process("psl.emf", "psl.flexmi");
    }

    @Test
    public void psl2() throws Exception {
        process("psl2.emf", "psl2.flexmi");
    }

    @Test
    public void psl3() throws Exception {
        process("psl3.emf", "psl3.flexmi");
    }

    @Test
    public void languages() throws Exception {
        process("languages.emf", "languages.flexmi");
    }

    @Test
    public void layers() throws Exception {
        process("layers.emf", "layers.flexmi");
    }

    @Test
    public void callcentre() throws Exception {
        process("callcentre.emf", "callcentre.flexmi");
    }

    @Test
    public void callcentreWithDanglingTransitions() throws Exception {
        process("callcentre.emf", "callcentre-dangling-transitions.flexmi");
    }

    @Test
    public void unannotatedObjectDiagram() throws Exception {
        var req = new Flexmi2GraphRequest();
        req.setEmfatic("package tree; class Tree { attr String name; val Tree[*]#parent children; ref Tree#children parent; }");
        req.setFlexmi("<?nsuri tree?><tree name=\"t1\"><tree name=\"t2\"/><tree name=\"t3\"/></tree>");

        ModelGraphResponse result = client.convert(req);
        assertNull(result.getError());

        Map<String, Object> graph = result.getModelGraph();
        assertWellFormed(graph);
        assertEquals(3, nodesOfKind(graph, "object").size());
        assertEquals(":Tree", nodes(graph).get(0).get("label"));
        assertEquals(Map.of("backgroundColor", "#F0FFFF", "borderColor", "#00C6C6"), nodes(graph).get(0).get("style"));
        assertEquals("name = t1", ((java.util.List<?>) ((java.util.List<?>) nodes(graph).get(0).get("compartments")).get(0)).get(0));

        // Only one of the two opposite references (children/parent) is shown
        assertEquals(2, edges(graph).size());
    }

    @Test
    public void graphicalSyntaxAnnotationValidation() throws Exception {
        var req = new Flexmi2GraphRequest();
        req.setEmfatic(getResourceAsString("/plantuml/graph.emf"));
        req.setFlexmi("<_/>");

        ModelGraphResponse result = client.convert(req);
        assertEquals(getResourceAsString("/plantuml/graph-failed-constraints.txt"), result.getError());
        assertNull(result.getModelGraph());
    }

    protected Map<String, Object> process(String emfatic, String flexmi) throws Exception {
        var req = new Flexmi2GraphRequest();
        req.setEmfatic(getResourceAsString("/plantuml/" + emfatic));
        req.setFlexmi(getResourceAsString("/plantuml/" + flexmi));

        ModelGraphResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());

        Map<String, Object> graph = result.getModelGraph();
        save(graph, flexmi.replace(".flexmi", ""));
        assertWellFormed(graph);
        return graph;
    }
}
