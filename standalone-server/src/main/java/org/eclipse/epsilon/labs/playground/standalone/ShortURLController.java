package org.eclipse.epsilon.labs.playground.standalone;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.validation.Valid;
import org.eclipse.epsilon.labs.playground.fn.shorturl.IShortURLController;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Controller(ShortURLController.PATH)
public class ShortURLController implements IShortURLController {
  public static final String PATH = "/shorturl";
  private static final Logger LOGGER = LoggerFactory.getLogger(ShortURLController.class);

  @Value("${playground.short.folder:shorturl}")
  private String storagePath;

  private File storageFolder;

  @EventListener
  public void onStartup(StartupEvent event) {
    storageFolder = new File(storagePath);
    if (!storageFolder.exists()) {
      LOGGER.warn("Storage folder {} does not exist: trying to create it", storagePath);
      if (!storageFolder.mkdirs()) {
        LOGGER.error("Failed to create storage folder: {}", storagePath);
      }
    }
  }

  @ExecuteOn(TaskExecutors.IO)
  @Override
  public ShortURLMessage shorten(@Valid @Body ShortURLMessage request) {
    var response = new ShortURLMessage();
    if (request.getContent() != null) {
      String content = request.getContent();
      String shortened = getShortened(content);
      try {
        Files.writeString(new File(storageFolder, shortened).toPath(), content);
        response.setShortened(shortened);
      } catch (IOException e) {
        LOGGER.error(e.getMessage(), e);
        throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save resource");
      }
    } else if (request.getShortened() != null) {
      // Note - we intentionally only use the last segment (remove any path segments)
      File contentFile = new File(storageFolder, new File(request.getShortened()).getName());
      if (contentFile.isFile() && contentFile.exists()) {
        try {
          response.setContent(Files.readString(contentFile.toPath()));
        } catch (IOException e) {
          throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read resource");
        }
      } else {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Resource not found");
      }
    } else {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Either content or shortened must be provided");
    }

    return response;
  }

  protected String getShortened(String content) {
    return UUID.nameUUIDFromBytes(content.getBytes()).toString().substring(0, 8);
  }

}
