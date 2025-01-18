# Education Platform tool server for the Epsilon languages

This Micronaut application project bundles the core microservices with the additional endpoints and assets needed to implement an MDENet Education Platform tool server for the Eclipse Epsilon languages.

The server includes static assets for providing [Ace](https://ace.c9.io/)-based syntax highlighting and icons.

## Additional endpoints

* `GET /tools`: returns a JSON document according to the [MDENet Education Platform tool specification](https://github.com/mdenet/educationplatform/wiki/Adding-a-Tool).
* `GET /swagger/epsilon-playground-core-0.0.yml`: returns the OpenAPI specification for the core endpoints.
* `GET /swagger/epsilon-playground-http-0.0.yml`: returns the OpenAPI specification for the EP tool server-only endpoints.

The server also includes a copy of the Swagger UI at `/swagger-ui`, pointing at the core endpoints by default.

## Running from Gradle

To run the Education Platform tool server locally, run:

```bash
../gradlew run
```

You can then try out the endpoints through the Swagger UI available here:

http://localhost:8080/swagger-ui

To customise the port, you can use the `MICRONAUT_SERVER_PORT` environment variable:

```bash
MICRONAUT_SERVER_PORT=8010 ../gradlew run
```

## Running from the uber JAR

If you have Java installed, you can run the tool server from its uber-JAR.
For instance, if you built it yourself:

```bash
java -jar build/libs/ep-tool-server-0.1-SNAPSHOT-all.jar
```

You can download the latest `-all.jar` directly from [Github Packages](https://github.com/epsilonlabs/playground-backend/packages/2332989).

## Running from the Docker image

To run the tool service image while exposing its endpoints on the 8010 port (e.g., to avoid the 8080 port used by the MDENet Education Platform), run:

```bash
docker run --rm -p 8010:8080 ghcr.io/epsilonlabs/playground-backend/ep-tool-server
```

Besides the `latest` tag, other tags are supported.
See [the full list](https://github.com/epsilonlabs/playground-backend/pkgs/container/playground-backend%2Fep-tool-server).