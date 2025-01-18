# Instructions for contributors

## Dependencies

To build this project, you will need:

- [Git](https://git-scm.com/downloads) command-line client
- [Java 17](https://adoptium.net/) (for Micronaut 4.x)
- [Node.js 16](https://nodejs.org/en) (for packing the JavaScript files in `ep-tool-server`)
- [`rsync`](https://rsync.samba.org/) command-line tool

If you would like to build the native version of the `shorturl-s3-lambda`, you will also need:

- [GraalVM 17 or newer](https://www.graalvm.org/release-notes/JDK_17/)

To run this project, you will also need:

- [Graphviz](https://graphviz.org/)

Alternatively, you can open this project in [VS Code](https://code.visualstudio.com/) and use the provided [development container](https://code.visualstudio.com/docs/devcontainers/containers), which includes the dependencies.

## Structure

The project starts from two main subprojects:

* [`core`](./core) is a library that implements the main microservices, responsible for executing Epsilon scripts and visualising models.
* [`shorturl-api`](./shorturl-api) is a library that defines the interface of the URL shortening service which implements the "Share" button in the Playground.

There are different distributions of the core microservices, depending on your needs:

* [`ep-tool-server`](./ep-tool-server) exposes the microservices as an [MDENet Education Platform tool service](https://github.com/mdenet/educationplatform/wiki/Adding-a-Tool#tool-service), which can be distributed as an uber-JAR or as a Docker image.
* [`backend-server`](./backend-server) exposes the microservices on their own, without any additions and without the Swagger UI. Mostly to be used when serving a frontend separately (e.g. via a CDN).
* [`standalone-server`](./standalone-server) combines the microservices with a local copy of the [Epsilon Playground](https://github.com/eclipse-epsilon/epsilon-website) frontend, for easy local use of the playground (e.g. for teaching).

There is an implementation of the `shorturl-api` as a serverless function:

* [`shorturl-s3-lambda`](./shorturl-s3-lambda) is an implementation based on AWS Lambda, using an S3 bucket for storage.

The [`buildSrc`](./buildSrc) directory includes common Gradle build logic (see [Gradle documentation on this](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html#sec:using_buildsrc)).
Specifically, it contains conventions for all Java projects in this repository, and for all server projects (which inherit the conventions of the Java projects).

## Building the project

The project uses a multi-project Gradle build system.
To compile, test, and package the project, run:

```shell
./gradlew build
```

If you would like to build the Docker images as well, run:

```shell
./gradlew dockerBuild
```

## Managing releases

The version number for the entire project is in the `gradle.properties` file, as the `projectVersion`.
If at some point we want to produce a stable release, the process would be as follows:

1. Create a tag with the commit that will be the source for this release.
1. Bump the `projectVersion` to the next version, so interim images for the next release will start to be produced.
1. Update the [`dockerFromTags` workflow](.github/workflows/dockerFromTags.yml) so it will periodically rebuild the images of the release. Specifically, new entries will need to be added to the `include` list of its matrix strategy.
