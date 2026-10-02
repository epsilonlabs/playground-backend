package org.eclipse.epsilon.labs.playground.fn.emfatic2graph;

import static org.eclipse.epsilon.labs.playground.fn.GraphAssertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.epsilon.labs.playground.fn.PlaygroundTest;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Emfatic2GraphTest extends PlaygroundTest {
    @Inject
    Emfatic2GraphClient client;

    @Test
    public void classDiagram() throws Exception {
        var req = new Emfatic2GraphRequest();
        req.setEmfatic(String.join("\n",
            "package example;",
            "abstract class Named { attr String name; }",
            "class Library extends Named { val Book[*] books; attr Kind kind; }",
            "class Book extends Named { ref Book[0..1] sequel; }",
            "enum Kind { public; private; }"));

        var response = client.render(req);
        assertNull(response.getError());

        Map<String, Object> graph = response.getMetamodelGraph();
        save(graph, "example-metamodel");
        assertWellFormed(graph);

        assertEquals(3, nodesOfKind(graph, "class").size());
        assertEquals(1, nodesOfKind(graph, "enum").size());
        assertEquals(true, nodeWithLabel(graph, "Named").get("abstract"));
        assertEquals(List.of(List.of("books : Book[*]", "kind : Kind")), nodeWithLabel(graph, "Library").get("compartments"));
        assertEquals(List.of(List.of("public", "private")), nodeWithLabel(graph, "Kind").get("compartments"));

        Map<String, Object> inheritance = edge(graph, "Library", "Named");
        assertEquals("arrow.empty", inheritance.get("targetDecoration"));
        assertEquals("up", inheritance.get("direction"));

        Map<String, Object> containment = edge(graph, "Library", "Book");
        assertEquals("diamond", containment.get("sourceDecoration"));
        assertEquals("arrow", containment.get("targetDecoration"));

        Map<String, Object> enumAttribute = edge(graph, "Library", "Kind");
        assertEquals("none", enumAttribute.get("targetDecoration"));

        Map<String, Object> selfReference = edge(graph, "Book", "Book");
        assertEquals("right", selfReference.get("direction"));
    }

    @Test
    public void externalSupertype() throws Exception {
        var req = new Emfatic2GraphRequest();
        req.setEmfatic("package example; import \"http://www.eclipse.org/emf/2002/Ecore\"; class Foo extends ecore.EObject {}");

        var response = client.render(req);
        assertNull(response.getError());

        // A node is added for the supertype, so that the inheritance edge does not dangle
        Map<String, Object> graph = response.getMetamodelGraph();
        assertWellFormed(graph);
        assertNotNull(nodeWithLabel(graph, "EObject"));
    }

    @Test
    public void psl() throws Exception {
        var req = new Emfatic2GraphRequest();
        req.setEmfatic(getResourceAsString("/plantuml/psl.emf"));

        var response = client.render(req);
        assertNull(response.getError());

        Map<String, Object> graph = response.getMetamodelGraph();
        save(graph, "psl-metamodel");
        assertWellFormed(graph);
        assertTrue(nodesOfKind(graph, "class").size() > 1);
    }

    protected Map<String, Object> edge(Map<String, Object> graph, String source, String target) {
        return edges(graph).stream()
            .filter(e -> source.equals(e.get("source")) && target.equals(e.get("target")))
            .findFirst().orElseThrow(() -> new AssertionError("No edge from " + source + " to " + target));
    }
}
