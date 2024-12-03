package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Epsilon Playground Core",
        version = "0.0",
        description = "Core functions for running Epsilon scripts and rendering models",
        license = @License(name = "Eclipse Public License 2.0", url = "https://www.eclipse.org/legal/epl-2.0/"),
        contact = @Contact(url = "https://github.com/epsilonlabs/playground-backend", name = "Epsilon Developers")
    )
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}
