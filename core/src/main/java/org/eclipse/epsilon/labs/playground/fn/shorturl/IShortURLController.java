package org.eclipse.epsilon.labs.playground.fn.shorturl;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;

/**
 * Generic interface for {@code /shorturl} controllers.
 *
 * Servers would need to either implement their own variant of
 * this service, or leave it unimplemented for other servers to
 * provide this service separately.
 */
public interface IShortURLController {
  @Post
  ShortURLMessage shorten(@Valid @Body ShortURLMessage request);
}
