# Epsilon Playground microservices

This project provides an alternative [Micronaut](https://micronaut.io/)-based implementation of the microservices needed for the [Playground](https://github.com/epsilonlabs/playground) of the [Eclipse Epsilon](https://eclipse.org/epsilon) project.

This project publishes [Docker images](https://github.com/epsilonlabs/playground-backend/pkgs/container/playground-backend) to the Github Container Registry from its `main` branch.

## Dependencies

To build this project, you will need:

- [Git](https://git-scm.com/downloads) command-line client
- [Java 17](https://adoptium.net/) (for Micronaut 4.x)
- [Node.js 16](https://nodejs.org/en) (for packing the JavaScript files in `ep-tool-server`)
- [`rsync`](https://rsync.samba.org/) command-line tool

To run this project, you will also need:

- [Graphviz](https://graphviz.org/)

Alternatively, you can open this project in [VS Code](https://code.visualstudio.com/) and use the provided [development container](https://code.visualstudio.com/docs/devcontainers/containers), which includes the dependencies.

## Structure

The project is divided into:

* [`core`](./core) is a library that contains most of the implementations of the microservices.
* [`ep-tool-server`](./ep-tool-server) exposes the microservices as an [MDENet Education Platform tool service](https://github.com/mdenet/educationplatform/wiki/Adding-a-Tool#tool-service), which can be distributed as an uber-JAR or as a Docker image. See [below](#running-the-education-platform-tool-server) for instructions of how to use it.
* [`backend-server`](./backend-server) exposes the microservices on their own, without any additions and without the Swagger UI. Mostly to be used when serving a frontend separately (e.g. via a CDN).
* [`standalone-server`](./standalone-server) combines the microservices with a local copy of the [Epsilon Playground](https://github.com/eclipse-epsilon/epsilon-website) frontend, for easy local use of the playground (e.g. for teaching). More instructions on how to use it are [below](#running-the-standalone-playground-server).
* [`gcp-function`](./gcp-function) exposes the microservices as a Google Cloud Function, and adds an endpoint for communicating with the Google Cloud Storage API.

The [`buildSrc`](./buildSrc) directory includes common Gradle build logic (see [Gradle documentation on this](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html#sec:using_buildsrc)).

## Endpoints

The endpoints are [CORS](https://fetch.spec.whatwg.org/)-aware: they allow requests from any origin with any headers and a `Max-Age` set to 1 hour, but only with the methods listed below.

### Core endpoints

* `POST /emfatic2plantuml`: transforms a metamodel written in [Emfatic](https://eclipse.dev/emfatic/) to a [PlantUML class diagram](https://plantuml.com/class-diagram).
* `POST /flexmi2plantuml`: transforms a model written in [Flexmi](https://eclipse.dev/epsilon/doc/flexmi/) that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /epsilon`: runs an Epsilon script against a given set of metamodels (written in Emfatic) and models (written in Flexmi or XMI). The first model can alternatively be a JSON document.

### Additional endpoints for the EP tool server

* `GET /tools`: returns a JSON document according to the [MDENet Education Platform tool specification](https://github.com/mdenet/educationplatform/wiki/Adding-a-Tool).
* `GET /swagger/epsilon-playground-core-0.0.yml`: returns the OpenAPI specification for the core endpoints.
* `GET /swagger/epsilon-playground-http-0.0.yml`: returns the OpenAPI specification for the EP tool server-only endpoints.

The server includes static assets for providing [Ace](https://ace.c9.io/)-based syntax highlighting and icons.

The server also includes a copy of the Swagger UI at `/swagger-ui`, pointing at the core endpoints by default.

### Additional endpoints for the standalone Playground tool server

* `POST /shorturl`: allows for storing work locally and retrieving it later.
* `GET /swagger/epsilon-playground-core-0.0.yml`: returns the OpenAPI specification for the core endpoints.

The server includes a local copy of the static assets of the Epsilon Playground.

The server also includes a copy of the Swagger UI at `/swagger-ui`, pointing at the core endpoints by default.

### Additional endpoints for the Google Cloud Function

* `POST /shorturl`: allows for storing work in Google Cloud Storage and retrieving it later.

## Configuration options

Besides the default [Micronaut options](https://docs.micronaut.io/latest/guide/index.html), all servers can be configured through these environment variables:

* `PLAYGROUND_TIMEOUT_MILLIS`: timeout in milliseconds for any Epsilon scripts being executed by the playground. Default is `60000` (60s).

### Options for the standalone Playground server

The standalone Playground server supports these additional environment variables:

* `PLAYGROUND_EPSILON_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation.
* `PLAYGROUND_FLEXMI2PLANTUML_URL`: URL to the service running the Flexmi to PlantUML transformation. The default is to use the server's own implementation.
* `PLAYGROUND_EMFATIC2PLANTUML_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation.
* `PLAYGROUND_SHORT_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation, which uses a local folder. When using this default, the following environment variable applies:
  * `PLAYGROUND_SHORT_FOLDER`: absolute path to the folder that should store the contents to be shared. This is the `shorturl` subfolder of the current working directory by default when running directly via Gradle or from the uber-JAR. For the Docker image specifics, see [below](#standalone-playground-from-the-docker-image).

## Building the project

Run this command to build all modules and run the tests on the core endpoints:

```bash
./gradlew build
```

This will also build uber-JAR distributions of the servers and the Google Cloud Functions, in the respective `build/libs` directories of the modules.

After the project has been built, you can build the various Docker images with:

```bash
./gradlew dockerBuild
```

## Running the Education Platform tool server

### EP tool server from Gradle

To run the Education Platform tool server locally, run:

```bash
./gradlew ep-tool-server:run
```

You can then try out the endpoints through the Swagger UI available here:

http://localhost:8080/swagger-ui

To customise the port, you can use the `MICRONAUT_SERVER_PORT` environment variable:

```bash
MICRONAUT_SERVER_PORT=8010 ./gradlew run
```

### EP tool server from the uber JAR

If you have Java installed, you can run the tool server from its uber-JAR.
For instance, if you built it yourself:

```bash
java -jar ep-tool-server/build/libs/ep-tool-server-0.1-SNAPSHOT-all.jar
```

You can download the latest `-all.jar` directly from [Github Packages](https://github.com/epsilonlabs/playground-backend/packages/2332989).

### EP tool server from the Docker image

To run the tool service image while exposing its endpoints on the 8010 port (e.g., to avoid the 8080 port used by the MDENet Education Platform), run:

```bash
docker run --rm -p 8010:8080 ghcr.io/epsilonlabs/playground-backend:ep-tool-server
```

## Running the standalone Playground server

### Standalone Playground from Gradle

To run the standalone Playground server locally, run:

```bash
./gradlew standalone-server:run
```

You can then try out the Playground through:

http://localhost:8080/

The Swagger UI is also available here:

http://localhost:8080/swagger-ui

### Standalone Playground from the uber JAR

If you have Java installed, you can run the tool server from its uber-JAR.
For instance, if you built it yourself:

```bash
java -jar standalone-server/build/libs/standalone-server-0.1-SNAPSHOT-all.jar
```

You can download the latest `-all.jar` directly from [Github Packages](https://github.com/epsilonlabs/playground-backend/packages/2333178).

### Standalone Playground from the Docker image

To run the standalone Playground server directly from Docker, run:

```bash
docker run --rm -p 8080:8080 ghcr.io/epsilonlabs/playground-backend:standalone-server
```

Note that the default implementation of "Share" in this image will use the `/var/share/shorturl` folder within the Docker container.
This means that unless you use a [bind mount](https://docs.docker.com/engine/storage/bind-mounts/) or a [volume](https://docs.docker.com/engine/storage/volumes/), you would lose all shared work once the container was destroyed.

For example, you could run this command to bind the `shorturl` folder in your host system to `/var/share/shorturl` within the container:

```bash
docker run --rm -v ./shorturl:/var/share/shorturl -p 8080:8080 ghcr.io/epsilonlabs/playground-backend:standalone-server
```

## Deploying to Google Cloud Functions

Please check the [`README` of the `gcp-function` module](./gcp-function/README.md) for details.
