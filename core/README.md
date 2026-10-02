# Core microservices for the Playground

This is a Micronaut library project which implements the core microservices used by the Playground to run Epsilon scripts and visualise models and metamodels.

## Endpoints

* `POST /emfatic2plantuml`: transforms a metamodel written in [Emfatic](https://eclipse.dev/emfatic/) to a [PlantUML class diagram](https://plantuml.com/class-diagram).
* `POST /flexmi2plantuml`: transforms a model written in [Flexmi](https://eclipse.dev/epsilon/doc/flexmi/) that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /xmi2plantuml`: transforms a model written in XMI that conforms to a metamodel written in Emfatic to a PlantUML class diagram.
* `POST /emfatic2graph`: transforms a metamodel written in Emfatic to a JSON graph of its class diagram (see below).
* `POST /flexmi2graph`: transforms a model written in Flexmi that conforms to a metamodel written in Emfatic to a JSON graph of its object diagram, or of the diagram defined by the metamodel's [graphical syntax annotations](https://eclipse.dev/epsilon/doc/articles/playground/graphical-syntax-annotations/).
* `POST /epsilon`: runs an Epsilon script against a given set of metamodels (written in Emfatic) and models (written in Flexmi or XMI). The first model can alternatively be a JSON document. Model diagrams in the response are PlantUML-rendered SVGs by default: set `"diagramFormat": "graph"` in the request to get JSON graphs instead (in `modelGraph`: the graph of the target model for ETL, EMG, EML and Flock, of the validated model for EVL, or of the pattern-matched model for EPL).

## JSON graphs

The `*2graph` endpoints (and `/epsilon` with `"diagramFormat": "graph"`) describe diagrams as graphs that clients lay out and render themselves (e.g. with [React Flow](https://reactflow.dev/)).
A graph is an object with a layout `direction` (`DOWN` or `RIGHT`), and lists of `nodes` and `edges`:

* Every node has an `id` and a `kind`: `object` (object diagram), `class` or `enum` (class diagram), `shape` (graphical syntax annotations), `note` (validation results and pattern matches), or `junction` (a point in the middle of an edge that a note is attached to). Nodes may also have a `label`, a `type` (the name of the EClass of the model element), a `shape` (the `@node` shape), an `image` URL, a `parent` node id (for nodes nested in other nodes), `compartments` (lists of lines of text, such as attribute values or features), an `abstract` flag, and a `style` object (`backgroundColor` and `borderColor` as CSS colors, plus any style options of the `@node` annotation).
* Every edge has an `id`, a `source` and a `target` node id, and a `sourceDecoration` and `targetDecoration` (`none`, `arrow`, `arrow.empty`, `diamond` or `diamond.empty`). Edges may also have a `label`, a `direction` hint (`up`, `down`, `left` or `right`, as in PlantUML), a `hidden` flag (edges that should affect the layout but not be drawn), and a `style` object (`color`, `pattern` and `thickness`).

## Caching

The `*2plantuml` and `*2graph` endpoints use in-memory caches to avoid rendering the same diagram multiple times.
These caches are limited in size by default: for further configuration, consult the [Micronaut Cache](https://micronaut-projects.github.io/micronaut-cache/latest/guide/) documentation.

## Configuration options

Besides the default [Micronaut options](https://docs.micronaut.io/latest/guide/index.html), the endpoints can be configured through these environment variables:

* `PLAYGROUND_TIMEOUT_MILLIS`: timeout in milliseconds for any Epsilon scripts being executed by the playground. Default is `60000` (60s).

## Request options

The endpoints are [CORS](https://fetch.spec.whatwg.org/)-aware: by default, they allow requests from any origin with any headers and a `Max-Age` set to 1 hour, but only with the methods listed above.

Requests are limited to a maximum of 100kB by default.

