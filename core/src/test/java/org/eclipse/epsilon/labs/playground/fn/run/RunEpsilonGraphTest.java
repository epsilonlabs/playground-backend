package org.eclipse.epsilon.labs.playground.fn.run;

import static org.eclipse.epsilon.labs.playground.fn.GraphAssertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.eclipse.epsilon.labs.playground.fn.PlaygroundTest;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Tests for running Epsilon programs with <code>"diagramFormat": "graph"</code>.
 */
@MicronautTest
public class RunEpsilonGraphTest extends PlaygroundTest {

    static final String TREE_EMFATIC = "package tree; class Tree { attr String name; val Tree[*] children; }";
    static final String TREE_FLEXMI = "<?nsuri tree?><tree name=\"t1\"><tree name=\"t2\"><tree name=\"t3\"/></tree></tree>";

    static final String ANNOTATED_TREE_EMFATIC = "package tree; @node(label=\"name\") class Tree { attr String name; @edge val Tree[*] children; }";

    @Inject
    RunEpsilonClient client;

    @Test
    public void evl() throws Exception {
        var req = request("evl", "context Tree { constraint NotT3 { check: self.name <> 't3' message: 'Tree named t3' } }");
        var response = client.execute(req);
        assertNull(response.getError());
        assertNull(response.getValidatedModelDiagram());

        Map<String, Object> graph = response.getModelGraph();
        save(graph, "evl");
        assertWellFormed(graph);

        var notes = nodesOfKind(graph, "note");
        assertEquals(1, notes.size());
        assertEquals("ⓧ Tree named t3", notes.get(0).get("label"));
        assertTrue(edges(graph).stream().anyMatch(e -> e.get("source").equals(notes.get(0).get("id")) && e.get("target").equals("N2")));
    }

    @Test
    public void evlValidModel() throws Exception {
        var req = request("evl", "context Tree { constraint HasChildren { check: true } }");
        var response = client.execute(req);
        assertNull(response.getError());

        Map<String, Object> graph = response.getModelGraph();
        assertWellFormed(graph);
        assertEquals("☑ The model is valid.", nodesOfKind(graph, "note").get(0).get("label"));
    }

    @Test
    public void evlAnnotatedEdge() throws Exception {
        var req = request("evl", "context Tree { critique Leaf { check: self.children.notEmpty() message: 'Leaf' } }");
        req.setEmfatic("package tree; @node(label=\"name\") class Tree { attr String name; ref Tree[*] children; } " +
            "@edge(source=\"from\", target=\"to\") class Link { ref Tree from; ref Tree to; }");
        req.setFlexmi("<?nsuri tree?><_><tree name=\"a\" children=\"b\"/><tree name=\"b\"/><link from=\"a\" to=\"b\"/></_>");
        var response = client.execute(req);
        assertNull(response.getError());

        Map<String, Object> graph = response.getModelGraph();
        save(graph, "evl-annotated");
        assertWellFormed(graph);
        assertEquals(2, nodesOfKind(graph, "shape").size());
        assertEquals(1, nodesOfKind(graph, "note").size());
    }

    @Test
    public void epl() throws Exception {
        var req = request("epl", "pattern Parent p : Tree, c : Tree from: p.children { }");
        var response = client.execute(req);
        assertNull(response.getError());
        assertNull(response.getPatternMatchedModelDiagram());

        Map<String, Object> graph = response.getModelGraph();
        save(graph, "epl");
        assertWellFormed(graph);

        var notes = nodesOfKind(graph, "note");
        assertEquals(2, notes.size());
        assertEquals("Parent", notes.get(0).get("label"));
        assertTrue(edges(graph).stream().anyMatch(e -> "p".equals(e.get("label"))));
        assertTrue(edges(graph).stream().anyMatch(e -> "c".equals(e.get("label"))));
    }

    @Test
    public void eplAnnotated() throws Exception {
        var req = request("epl", "pattern Parent p : Tree, c : Tree from: p.children { }");
        req.setEmfatic(ANNOTATED_TREE_EMFATIC);
        var response = client.execute(req);
        assertNull(response.getError());

        Map<String, Object> graph = response.getModelGraph();
        save(graph, "epl-annotated");
        assertWellFormed(graph);
        assertEquals(2, nodesOfKind(graph, "note").size());
    }

    @Test
    public void etl() throws Exception {
        var req = request("etl", "rule T2N transform t : Source!Tree to n : Target!Node { n.label = t.name; }");
        req.setSecondEmfatic("package graph; @node(label=\"label\", shape=\"hexagon\") class Node { attr String label; }");
        var response = client.execute(req);
        assertNull(response.getError());
        assertNull(response.getTargetModelDiagram());

        Map<String, Object> graph = response.getModelGraph();
        save(graph, "etl");
        assertWellFormed(graph);
        assertEquals(3, nodesOfKind(graph, "shape").size());
        assertEquals("hexagon", nodeWithLabel(graph, "t1").get("shape"));
    }

    @Test
    public void emg() throws Exception {
        var req = new RunEpsilonRequest();
        req.setLanguage("emg");
        req.setProgram(getResourceAsString("/emg/program.emg"));
        req.setEmfatic(getResourceAsString("/emg/petrinet.emf"));
        req.setDiagramFormat(RunEpsilonRequest.DIAGRAM_FORMAT_GRAPH);

        var response = client.execute(req);
        assertNull(response.getError());
        assertWellFormed(response.getModelGraph());
    }

    @Test
    public void plantUmlIsTheDefault() throws Exception {
        var req = request("evl", "context Tree { constraint HasName { check: self.name.isDefined() } }");
        req.setDiagramFormat(null);
        var response = client.execute(req);
        assertNull(response.getError());
        assertNotNull(response.getValidatedModelDiagram());
        assertNotNull(response.getValidatedModelDiagramSource());
        assertNull(response.getModelGraph());
    }

    protected RunEpsilonRequest request(String language, String program) {
        var req = new RunEpsilonRequest();
        req.setLanguage(language);
        req.setProgram(program);
        req.setEmfatic(TREE_EMFATIC);
        req.setFlexmi(TREE_FLEXMI);
        req.setDiagramFormat(RunEpsilonRequest.DIAGRAM_FORMAT_GRAPH);
        return req;
    }
}
