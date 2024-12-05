package org.eclipse.epsilon.labs.playground;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLRequest;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class FunctionRequestHandlerTest {

    @Inject
    ObjectMapper objectMapper;

    private static FunctionRequestHandler handler;

    @BeforeAll
    public static void setupServer() {
        handler = new FunctionRequestHandler();
    }

    @AfterAll
    public static void stopServer() {
        if (handler != null) {
            handler.getApplicationContext().close();
        }
    }

    @Test
    public void testNotFound() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/");

        ShortURLRequest message = new ShortURLRequest();
        message.setShortened("abcdef01");
        request.setBody(objectMapper.writeValueAsString(message));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCode().intValue());
    }

    @Test
    public void testBadKey() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/");

        ShortURLRequest message = new ShortURLRequest();
        message.setShortened("abcdef012");
        request.setBody(objectMapper.writeValueAsString(message));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(400, response.getStatusCode().intValue());
        var responseMessage = objectMapper.readValue(response.getBody(), ShortURLResponse.class);
        assertTrue(responseMessage.getError().contains("shortened"));
    }

    @Test
    public void testTooLongContent() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/");

        ShortURLRequest message = new ShortURLRequest();
        message.setContent(new String(new char[ShortURLRequest.MAX_CONTENT_LENGTH + 1]).replace('\0', 'a'));
        request.setBody(objectMapper.writeValueAsString(message));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(400, response.getStatusCode().intValue());
        var responseMessage = objectMapper.readValue(response.getBody(), ShortURLResponse.class);
        assertTrue(responseMessage.getError().contains("content"));
    }

    @Test
    public void testOneOfThemMustBeProvided() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/");

        ShortURLRequest message = new ShortURLRequest();
        request.setBody(objectMapper.writeValueAsString(message));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(400, response.getStatusCode().intValue());
        assertTrue(response.getBody().toLowerCase().contains("one of"));
    }

    @Test
    public void testBadMethod() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/");

        ShortURLRequest message = new ShortURLRequest();
        request.setBody(objectMapper.writeValueAsString(message));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(400, response.getStatusCode().intValue());
        assertTrue(response.getBody().toLowerCase().contains("post"));
    }

    @Test
    public void testUploadThenDownload() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/");

        ShortURLRequest uploadRequest = new ShortURLRequest();
        uploadRequest.setContent("Hello world!");
        request.setBody(objectMapper.writeValueAsString(uploadRequest));

        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(200, response.getStatusCode().intValue());
        var uploadResponse = objectMapper.readValue(response.getBody(), ShortURLResponse.class);
        assertNotNull(uploadResponse.getShortened());

        ShortURLRequest downloadRequest = new ShortURLRequest();
        downloadRequest.setShortened(uploadResponse.getShortened());
        request.setBody(objectMapper.writeValueAsString(downloadRequest));
        response = handler.execute(request);
        assertEquals(200, response.getStatusCode().intValue());

        var downloadResponse = objectMapper.readValue(response.getBody(), ShortURLResponse.class);
        assertEquals(uploadRequest.getContent(), downloadResponse.getContent());
    }
}
