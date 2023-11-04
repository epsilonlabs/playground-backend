# Epsilon Playground microservices

This project provides an alternative [Micronaut](https://micronaut.io/)-based implementation of the microservices needed for the [Playground](https://github.com/epsilonlabs/playground) of the [Eclipse Epsilon](https://eclipse.org/epsilon) project.

## Dependencies

To build this project, you will need:

- [Java 17](https://adoptium.net/) (for Micronaut 4.x)
- [Node.js 16](https://nodejs.org/en) (for packing the JavaScript files in `http-server`)

## Structure

The project is divided into three modules:

* [`core`](./core) is a library that contains most of the implementations of the microservices.
* [`http-server`](./http-server) exposes the microservices as an HTTP server, which can be distributed as an uber-JAR or as a Docker image.
* [`gcp-function`](./gcp-function) exposes the microservices as a Google Cloud Function, and adds an endpoint for communicating with the Google Cloud Storage API.

## Endpoints

The endpoints are [CORS](https://fetch.spec.whatwg.org/)-aware: they allow requests from any origin with any headers and a `Max-Age` set to 1 hour, but only with the methods listed below.

### Core endpoints

* `POST /emfatic2plantuml`: transforms a metamodel written in [Emfatic](https://eclipse.dev/emfatic/) to a [PlantUML class diagram](https://plantuml.com/class-diagram).
* `POST /flexmi2plantuml`: transforms a model written in [Flexmi](https://eclipse.dev/epsilon/doc/flexmi/) that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /epsilon`: runs an Epsilon script against a given set of metamodels (written in Emfatic) and models (written in Flexmi).

### Additional endpoints for the HTTP server

* `GET /tools`: returns a JSON document according to the [MDENet Education Platform tool specification](https://github.com/mdenet/educationplatform/wiki/Adding-a-Tool).

The HTTP server includes static assets for providing [ACE](https://ace.c9.io/)-based syntax highlighting and icons.

### Additional endpoints for the Google Cloud Function

* `POST /shorturl`: allows for storing work in Google Cloud Storage and retrieving it later.

## Building the project

Run this command to build all modules and run the tests on the core endpoints:

```bash
./gradlew build
```

This will also build uber-JAR distributions of the HTTP server and the Google Cloud Functions, in the respective `build/libs` directories of the modules.

After the project has been built, you can build a Docker image for the HTTP server as well:

```bash
cd http-server
../gradlew dockerBuild
```

## Running the project

To run the HTTP server locally, run:

```bash
./gradlew run
```

## Deploying to Google Cloud Functions

Please check the [`README` of the `gcp-function` module](./gcp-function/README.md) for details.