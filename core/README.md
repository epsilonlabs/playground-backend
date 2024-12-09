# Core microservices for the Playground

This is a Micronaut library project which implements the core microservices used by the Playground to run Epsilon scripts and visualise models and metamodels.

## Endpoints

* `POST /emfatic2plantuml`: transforms a metamodel written in [Emfatic](https://eclipse.dev/emfatic/) to a [PlantUML class diagram](https://plantuml.com/class-diagram).
* `POST /flexmi2plantuml`: transforms a model written in [Flexmi](https://eclipse.dev/epsilon/doc/flexmi/) that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /xmi2plantuml`: transforms a model written in XMI that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /epsilon`: runs an Epsilon script against a given set of metamodels (written in Emfatic) and models (written in Flexmi or XMI). The first model can alternatively be a JSON document.

The `*2plantuml` endpoints use in-memory caches to avoid rendering the same diagram multiple times.
These caches are limited in size by default: for further configuration, consult the [Micronaut Cache](https://micronaut-projects.github.io/micronaut-cache/latest/guide/) documentation.

## Configuration options

Besides the default [Micronaut options](https://docs.micronaut.io/latest/guide/index.html), the endpoints can be configured through these environment variables:

* `PLAYGROUND_TIMEOUT_MILLIS`: timeout in milliseconds for any Epsilon scripts being executed by the playground. Default is `60000` (60s).

## Request options

The endpoints are [CORS](https://fetch.spec.whatwg.org/)-aware: by default, they allow requests from any origin with any headers and a `Max-Age` set to 1 hour, but only with the methods listed above.

Requests are limited to a maximum of 100kB by default.

