# ShortURL service specification

This is a Micronaut library project that provides a shared definition of the request and response messages of the `/shorturl` service.

This service is what powers the Playground's "Share" button, which should use the HTTP POST method as it results in side effects (the storage of the state of the Playground panels).

## Storing Playground state

With this request, we store the state of the Playground's various panes:

```json
{"content": "base64-encoded-state"}
```

This should respond with an ID that we can use later to retrieve this state:

```json
{"shortened": "<identifier>"}
```

## Retrieving Playground state

To retrieve the state of the Playground, we send a request like this one:

```json
{"shortened": "<identifier>"}
```

The service should then respond with the content:

```json
{"content": "base64-encoded-state"}
```

## Validation rules

The request and response beans include Jakarta Validation annotations.
Specifically, they check that the identifier follows the expected pattern, and that the content is of a certain maximum length.