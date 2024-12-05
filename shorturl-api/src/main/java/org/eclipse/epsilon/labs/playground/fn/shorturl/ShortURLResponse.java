package org.eclipse.epsilon.labs.playground.fn.shorturl;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ShortURLResponse extends ShortURLRequest {
  private String error;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
