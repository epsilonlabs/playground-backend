package org.eclipse.epsilon.labs.playground;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLRequest;
import org.eclipse.epsilon.labs.playground.standalone.BackendConfiguration;
import org.eclipse.epsilon.labs.playground.standalone.BackendConfigurationController;
import org.eclipse.epsilon.labs.playground.standalone.ShortURLController;

@Client("/")
public interface StandaloneClient {

  @Post(ShortURLController.PATH)
  ShortURLRequest shortURL(@Body ShortURLRequest request);

  @Get(BackendConfigurationController.PATH)
  BackendConfiguration getBackendConfiguration();

}
