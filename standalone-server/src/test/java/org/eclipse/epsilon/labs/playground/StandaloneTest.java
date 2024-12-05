package org.eclipse.epsilon.labs.playground;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class StandaloneTest {
  @Inject
  private StandaloneClient client;

  @Test
  public void getConfig() {
    assertNotNull(client.getBackendConfiguration());
  }

  @Test
  public void shortenContent() {
    var storeRequest = new ShortURLRequest();
    storeRequest.setContent("example");

    var storeResponse = client.shortURL(storeRequest);
    assertNotNull(storeResponse);
    assertTrue(storeResponse.getShortened().matches(ShortURLRequest.SHORTENED_REGEX));

    var fetchRequest = new ShortURLRequest();
    fetchRequest.setShortened(storeResponse.getShortened());
    var fetchResponse = client.shortURL(fetchRequest);
    assertNotNull(fetchResponse);
    assertEquals(fetchResponse.getContent(), storeRequest.getContent(),
        "The fetched content should be the same");
  }

  @Test
  public void shortenedNotValid() {
    var fetchRequest = new ShortURLRequest();
    fetchRequest.setShortened("../give/me/another/path");
    try {
      client.shortURL(fetchRequest);
      fail("Should have thrown an exception");
    } catch (HttpClientResponseException ex) {
      assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
  }

  @Test
  public void contentTooLong() {
    var storeRequest = new ShortURLRequest();
    String longString = new String(new char[ShortURLRequest.MAX_CONTENT_LENGTH + 1]).replace('\0', 'a');
    storeRequest.setContent(longString);
    try {
      client.shortURL(storeRequest);
      fail("Should have thrown an exception");
    } catch (HttpClientResponseException ex) {
      assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
  }
}
