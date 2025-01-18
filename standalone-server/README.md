# Standalone server for the Epsilon Playground

This Micronaut application project provides a unified distribution of the Epsilon Playground that includes both its frontend and its backend.
This makes it especially convenient for deploying at a small scale (e.g. within an educational institution).

## Additional endpoints

* `POST /shorturl`: allows for storing work locally and retrieving it later.
* `GET /swagger/epsilon-playground-core-0.0.yml`: returns the OpenAPI specification for the core endpoints.

The server also includes a copy of the Swagger UI at `/swagger-ui`, pointing at the core endpoints by default.

## Additional options

* `PLAYGROUND_EPSILON_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation.
* `PLAYGROUND_FLEXMI2PLANTUML_URL`: URL to the service running the Flexmi to PlantUML transformation. The default is to use the server's own implementation.
* `PLAYGROUND_EMFATIC2PLANTUML_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation.
* `PLAYGROUND_SHORT_URL`: URL to the service running Epsilon scripts. The default is to use the server's own implementation, which uses a local folder. When using this default, the following environment variable applies:
    * `PLAYGROUND_SHORT_FOLDER`: absolute path to the folder that should store the contents to be shared. This is the `shorturl` subfolder of the current working directory by default when running directly via Gradle or from the uber-JAR.

## Running from Gradle

To run the standalone Playground server locally, run:

```bash
../gradlew run
```

You can then try out the Playground through:

http://localhost:8080/

The Swagger UI is also available here:

http://localhost:8080/swagger-ui

## Running from the uber JAR

If you have Java installed, you can run the tool server from its uber-JAR.
For instance, if you built it yourself:

```bash
java -jar build/libs/standalone-server-0.1-SNAPSHOT-all.jar
```

You can download the latest `-all.jar` directly from [Github Packages](https://github.com/epsilonlabs/playground-backend/packages/2333178).

## Running from the Docker image

To run the standalone Playground server directly from Docker, run:

```bash
docker run --rm -p 8080:8080 ghcr.io/epsilonlabs/playground-backend/standalone-server
```

Note that the default implementation of "Share" in this image will use the `/var/share/shorturl` folder within the Docker container.
This means that unless you use a [bind mount](https://docs.docker.com/engine/storage/bind-mounts/) or a [volume](https://docs.docker.com/engine/storage/volumes/), you would lose all shared work once the container was destroyed.

For example, you could run this command to bind the `shorturl` folder in your host system to `/var/share/shorturl` within the container:

```bash
docker run --rm -v ./shorturl:/var/share/shorturl -p 8080:8080 ghcr.io/epsilonlabs/playground-backend/standalone-server
```

Besides the `latest` tag, other tags are supported.
See [the full list](https://github.com/epsilonlabs/playground-backend/pkgs/container/playground-backend%2Fstandalone-server).