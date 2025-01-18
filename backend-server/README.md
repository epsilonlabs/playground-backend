# Backend-only distribution

This Micronaut application project provides a plain distribution of the core services, with nothing else added.

This is meant to be used from a separately deployed frontend (e.g. via a content distribution network).

## Endpoints

These are the same as in the [core](../core/README.md) project.

## Running from Gradle

To run the Education Platform tool server locally, run:

```bash
../gradlew run
```

## Running from the uber JAR

If you have Java installed, you can run the tool server from its uber-JAR.
For instance, if you built it yourself:

```bash
java -jar build/libs/backend-server-0.1-SNAPSHOT-all.jar
```

You can download the latest `-all.jar` directly from [Github Packages](https://github.com/epsilonlabs/playground-backend/packages/2333060).

## Running from the Docker image

To run the tool service image, run:

```bash
docker run --rm -p 8080:8080 ghcr.io/epsilonlabs/playground-backend/backend-server
```

The endpoints will be available from:

http://localhost:8080/

Besides the `latest` tag, other tags are supported.
See [the full list](https://github.com/epsilonlabs/playground-backend/pkgs/container/playground-backend%2Fbackend-server).