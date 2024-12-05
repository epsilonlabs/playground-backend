package org.eclipse.epsilon.labs.playground;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.function.aws.MicronautRequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.json.JsonMapper;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.objectstorage.ObjectStorageEntry;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLRequest;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionRequestHandler.class);

    @Inject
    JsonMapper objectMapper;

    @Inject
    Validator validator;

    @Inject
    ObjectStorageOperations<?, ?, ?> objectStorage;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        if (!"POST".equals(input.getHttpMethod())) {
            setResponseText(response, "Invalid HTTP method. Valid HTTP methods are: [POST]");
            response.setStatusCode(400);
            return response;
        }
        if (input.getBody() == null) {
            setResponseText(response, "Request body is missing.");
            response.setStatusCode(400);
            return response;
        }

        try {
            var request = objectMapper.readValue(input.getBody(), ShortURLRequest.class);
            Set<ConstraintViolation<ShortURLRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                processInvalidRequest(violations, response);
            } else {
                processValidRequest(request, response);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            response.setStatusCode(500);
        }
        return response;
    }

    private void processInvalidRequest(Set<ConstraintViolation<ShortURLRequest>> violations, APIGatewayProxyResponseEvent response) throws IOException {
        var responseBody = new ShortURLResponse();
        responseBody.setError("Invalid request: " + String.join(",",
            violations.stream().map(e -> e.getPropertyPath() + " " + e.getMessage()
            ).collect(Collectors.toList())));
        response.setStatusCode(400);
        setResponseJSON(response, responseBody);
    }

    protected static void setResponseText(APIGatewayProxyResponseEvent response, String body) {
        response.setBody(body);
        response.setHeaders(Collections.singletonMap(
            HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN));
    }

    protected void setResponseJSON(APIGatewayProxyResponseEvent response, ShortURLResponse responseBody) throws IOException {
        response.setBody(objectMapper.writeValueAsString(responseBody));
        response.setHeaders(Collections.singletonMap(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    protected void processValidRequest(ShortURLRequest request, APIGatewayProxyResponseEvent response) throws IOException {
        var responseMsg = new ShortURLResponse();
        if (request.getShortened() != null) {
            processDownloadRequest(request, response, responseMsg);
        } else if (request.getContent() != null) {
            processUploadRequest(request, response, responseMsg);
        } else {
            setResponseText(response, "Exactly one of shortened or content must be specified");
            response.setStatusCode(400);
        }
    }

    protected void processUploadRequest(ShortURLRequest request, APIGatewayProxyResponseEvent response, ShortURLResponse responseMsg) throws IOException {
        String key = getShortened(request.getContent());
        UploadRequest uploadRequest = UploadRequest.fromBytes(
            request.getContent().getBytes(StandardCharsets.UTF_8), key);

        @NonNull UploadResponse<?> uploadResponse = objectStorage.upload(uploadRequest);
        responseMsg.setShortened(uploadResponse.getKey());
        setResponseJSON(response, responseMsg);
        response.setStatusCode(200);
    }

    protected void processDownloadRequest(ShortURLRequest request, APIGatewayProxyResponseEvent response, ShortURLResponse responseMsg) throws IOException {
        Optional<ObjectStorageEntry<?>> content = objectStorage.retrieve(request.getShortened());
        if (content.isPresent()) {
            try (
                InputStream is = content.get().getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ) {
                byte[] buffer = new byte[1024];
                for (int length; (length = is.read(buffer)) != -1; ) {
                    baos.write(buffer, 0, length);
                }
                responseMsg.setContent(baos.toString(StandardCharsets.UTF_8));
                setResponseJSON(response, responseMsg);
                response.setStatusCode(200);
            }
        } else {
            response.setStatusCode(404);
        }
    }

    protected String getShortened(String content) {
        return UUID.nameUUIDFromBytes(content.getBytes()).toString().substring(0, 8);
    }

}
