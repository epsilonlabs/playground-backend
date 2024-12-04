package org.eclipse.epsilon.labs.playground.shorturl.s3;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.eclipse.epsilon.labs.playground.fn.shorturl.IShortURLController;
import org.eclipse.epsilon.labs.playground.fn.shorturl.ShortURLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller(S3ShortURLController.PATH)
public class S3ShortURLController implements IShortURLController {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3ShortURLController.class);

  @Value("${playground.short.s3.bucket:epsilon-playground}")
  private String s3Bucket;

  public static final String PATH = "/shorturl";

  @Inject
  private S3Client s3Client;

  @ExecuteOn(TaskExecutors.BLOCKING)
  @Override
  public ShortURLMessage shorten(@Valid @Body ShortURLMessage request) {
    var response = new ShortURLMessage();

    if (request.getShortened() != null) {
      GetObjectRequest objectRequest = GetObjectRequest.builder()
          .key(request.getShortened())
          .bucket(s3Bucket).build();

      try {
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
        String content = objectBytes.asString(StandardCharsets.UTF_8);
        response.setContent(content);
      } catch (S3Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "S3 object not found");
      }
    } else if (request.getContent() != null) {
      response.setShortened(getShortened(request.getContent()));
      PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(s3Bucket)
          .key(response.getShortened())
          .build();

      try {
        s3Client.putObject(objectRequest, RequestBody.fromString(
            request.getContent(), StandardCharsets.UTF_8));
      } catch (S3Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not put the S3 object");
      }
    } else {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Either shortened or content fields must be set");
    }

    return response;
  }

  protected String getShortened(String content) {
    return UUID.nameUUIDFromBytes(content.getBytes()).toString().substring(0, 8);
  }
}
