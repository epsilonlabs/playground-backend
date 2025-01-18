# Epsilon Playground backend

This project provides [Micronaut](https://micronaut.io/)-based implementations of the microservices needed for the [Playground](https://eclipse.dev/epsilon/playground) of the [Eclipse Epsilon](https://eclipse.org/epsilon) project.

## Running the Playground locally

This project publishes [Docker images](https://github.com/epsilonlabs/playground-backend/pkgs/container/playground-backend) to the Github Container Registry from its `main` branch.

To try the Playground on your own computer (with Docker installed), you can run:

```shell
docker run --rm -p 8080:8080 ghcr.io/epsilonlabs/playground-backend/standalone-server
```

## Using and administering the Playground

The available endpoints are described here:

* [Core microservices](core/README.md)
* [ShortURL API spec](shorturl-api/README.md)

There are three different distributions for the core microservices:

* [MDENet Education Platform tool server](ep-tool-server/README.md)
* [Backend-only server](backend-server/README.md)
* [Standalone (frontend + backend) server](standalone-server/README.md)

There is a standalone implementation of the ShortURL API:

* [Serverless ShortURL on top of AWS Lambda and S3 object storage](shorturl-s3-lambda/README.md)

## Building the project and contributing to it

The [`CONTRIBUTING.md`](./CONTRIBUTING.md) document has instructions on how to build the project and contribute to it.
