package org.eclipse.epsilon.labs.playground.fn.run;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.matchers.JUnitMatchers;

import com.google.common.io.CharStreams;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class RunEpsilonTest {
    
    @Inject
    RunEpsilonClient client;

    @Test
    public void egx() {
        var req = new RunEpsilonRequest();
        req.setLanguage("egx");
        req.setProgram("rule T2T transform t : Tree { template : 'foo.egl' target: t.name + '.txt'}");
        req.setSecondProgram("Tree [%=t.name%]");
        req.setFlexmi("<?nsuri tree?><_><tree name=\"t1\"/><tree name=\"t2\"/></_>");
        req.setEmfatic("package tree; class Tree { attr String name; }");

        var response = client.execute(req);
        assertNull(response.getError());
        assertNotNull(response.getGeneratedFiles());
        assertEquals(2, response.getGeneratedFiles().size());
    }

    @Test
    public void egxInputXmi() {
        var req = new RunEpsilonRequest();
        req.setLanguage("egx");
        req.setProgram("rule T2T transform t : Tree { template : 'foo.egl' target: t.name + '.txt'}");
        req.setSecondProgram("Tree [%=t.name%]");
        req.setXmi("<?xml version=\"1.0\" encoding=\"ASCII\"?>\n"
        		+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:tree=\"tree\">\n"
        		+ "  <tree:Tree xmi:id=\"_DPJo4MmUEe6q89ioEkXZsQ\" name=\"t1\"/>\n"
        		+ "  <tree:Tree xmi:id=\"_DPJo4cmUEe6q89ioEkXZsQ\" name=\"t2\"/>\n"
        		+ "</xmi:XMI>\n"
        		+ "");
        req.setEmfatic("package tree; class Tree { attr String name; }");

        var response = client.execute(req);
        assertNull(response.getError());
        assertNotNull(response.getGeneratedFiles());
        assertEquals(2, response.getGeneratedFiles().size());
    }

    @Test
    public void emlSecondInputXmi() throws Exception {
        var req = new RunEpsilonRequest();
        req.setLanguage("eml");
        req.setProgram(getResourceAsString("/eml/tree.eml"));
        req.setSecondProgram(getResourceAsString("/eml/tree.ecl"));
        req.setXmi(getResourceAsString("/eml/left.xmi"));
        req.setThirdXmi(getResourceAsString("/eml/right.xmi"));

        String emfaticSource = getResourceAsString("/eml/tree.emf");
        req.setEmfatic(emfaticSource);
        req.setSecondEmfatic(emfaticSource);
        req.setThirdEmfatic(emfaticSource);

        var response = client.execute(req);
        assertNull(response.getError());
        assertThat(response.getTargetModelDiagram(), JUnitMatchers.containsString("A"));
        assertThat(response.getTargetModelDiagram(), JUnitMatchers.containsString("B"));
    }

    private String getResourceAsString(String resource) throws IOException {
        var inputStream = getClass().getResourceAsStream(resource);
        return CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }
    
}
