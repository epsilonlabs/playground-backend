package org.eclipse.epsilon.labs.playground;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Epsilon Playground HTTP",
        version = "0.0",
        description = "Additional functions only needed by Playground HTTP server",
        license = @License(name = "Eclipse Public License 2.0", url = "https://www.eclipse.org/legal/epl-2.0/"),
        contact = @Contact(url = "https://github.com/epsilonlabs/playground-micronaut", name = "Epsilon Developers")
    )
)
public class Application {

    @ContextConfigurer
    public static class DefaultEnvironmentConfigurer implements ApplicationContextConfigurer {
        @Override
        public void configure(@NonNull ApplicationContextBuilder builder) {
            builder.defaultEnvironments("", "http");
        }
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
