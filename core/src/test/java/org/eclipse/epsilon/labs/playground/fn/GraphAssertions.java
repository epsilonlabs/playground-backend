package org.eclipse.epsilon.labs.playground.fn;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.micronaut.serde.ObjectMapper;

/**
 * Helper methods for checking the JSON graphs produced by the *2graph endpoints.
 */
public class GraphAssertions {

    private GraphAssertions() {}

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> nodes(Map<String, Object> graph) {
        return (List<Map<String, Object>>) graph.get("nodes");
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> edges(Map<String, Object> graph) {
        return (List<Map<String, Object>>) graph.get("edges");
    }

    public static List<Map<String, Object>> nodesOfKind(Map<String, Object> graph, String kind) {
        return nodes(graph).stream().filter(n -> kind.equals(n.get("kind"))).collect(Collectors.toList());
    }

    public static Map<String, Object> nodeWithLabel(Map<String, Object> graph, String label) {
        return nodes(graph).stream().filter(n -> label.equals(n.get("label"))).findFirst()
            .orElseThrow(() -> new AssertionError("No node with label " + label));
    }

    /**
     * Checks that node ids are unique, and that all the edges and nested nodes refer to existing nodes.
     */
    public static void assertWellFormed(Map<String, Object> graph) {
        assertNotNull(graph);
        assertTrue(Set.of("DOWN", "RIGHT").contains(graph.get("direction")));

        Set<Object> ids = new HashSet<>();
        for (Map<String, Object> node : nodes(graph)) {
            assertNotNull(node.get("kind"));
            assertTrue(ids.add(node.get("id")), "Duplicate node id " + node.get("id"));
        }
        for (Map<String, Object> node : nodes(graph)) {
            if (node.containsKey("parent")) {
                assertTrue(ids.contains(node.get("parent")), "Unknown parent " + node.get("parent"));
            }
        }

        Set<Object> edgeIds = new HashSet<>();
        for (Map<String, Object> edge : edges(graph)) {
            assertTrue(edgeIds.add(edge.get("id")), "Duplicate edge id " + edge.get("id"));
            assertTrue(ids.contains(edge.get("source")), "Unknown edge source " + edge.get("source"));
            assertTrue(ids.contains(edge.get("target")), "Unknown edge target " + edge.get("target"));
            assertNotNull(edge.get("sourceDecoration"));
            assertNotNull(edge.get("targetDecoration"));
        }

        assertFalse(nodes(graph).isEmpty());
    }

    /**
     * Saves a graph under build/graphs, for inspection.
     */
    public static void save(Map<String, Object> graph, String name) throws IOException {
        Path path = Path.of("build/graphs/" + name + ".json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, ObjectMapper.getDefault().writeValueAsString(graph), StandardCharsets.UTF_8);
    }
}
