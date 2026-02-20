package org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.epsilon.labs.playground.fn.ModelDiagramResponse;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class Flexmi2PlantUMLTest {

    @Inject
    Flexmi2PlantUMLClient client;

    @Client(Flexmi2PlantUMLController.PATH)
    @Inject
    HttpClient rawClient;

    @Test
    public void corsPreflight() {
        var request = HttpRequest
                .OPTIONS("/")
                .header(HttpHeaders.ORIGIN, "http://localhost:1234")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST");

        var response = rawClient.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void singlePackage() {
        var req = new Flexmi2PlantUMLRequest();
        req.setFlexmi("<?nsuri http://www.eclipse.org/emf/2002/Ecore?>\n<package name=\"p1\"/>");
        req.setEmfatic("");

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

    @Test
    public void annotated() {
        var req = new Flexmi2PlantUMLRequest();

        req.setEmfatic("""
                  package filesystem;
                
                  class Filesystem {
                      val Drive[*] drives;
                      val Sync[*] syncs;
                  }
                
                  class Drive extends Folder {}
                
                  @node(contents="contents")
                  class Folder extends File {
                      val File[*] contents;
                  }
                
                  @node(shape="node")
                  class Shortcut extends File {
                      @edge(color="red", label="eol:'sync'")
                      ref File target;
                  }
                
                  @edge(source="source", target="target", color="green")
                  class Sync {
                      ref File source;
                      ref File target;
                  }
                
                  @node(label = "name", color = "azure")
                  class File {
                      attr String name;
                  }
                """);

        req.setFlexmi("""
                <?nsuri filesystem?>
                <filesystem>
                    <drive name="C">
                        <folder name="My Documents">
                            <file name="image.bmp"/>
                        </folder>
                        <shortcut name="image.lnk" target="image.bmp"/>
                        <file name="synced.bmp"/>
                    </drive>
                    <sync source="synced.bmp" target="image.bmp"/>
                </filesystem>
                """);

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

    @Test
    public void eglTemplate() {
        var req = new Flexmi2PlantUMLRequest();
        req.setFlexmi("<_><foo/><:template name=\"foo\"><content language=\"EGL\"></content></:template></_>");
        req.setEmfatic("");

        ModelDiagramResponse result = client.convert(req);
        assertNull(result.getError());
        assertNull(result.getOutput());
        assertNotNull(result.getModelDiagram());
    }

}
