package org.eclipse.epsilon.labs.playground.fn.run;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

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

    
}
