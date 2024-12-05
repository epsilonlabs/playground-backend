package org.eclipse.epsilon.labs.playground.fn.shorturl;

import java.io.FileInputStream;
import java.util.UUID;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/shorturl")
public class ShortURLController implements IShortURLController {

	@Value("${playground.gcp.projectId:`epsilon-live-gcp`")
	private String projectId;

	@Value("${playground.gcp.bucket:`epsilon-live-gcp.appspot.com`")
	private String bucket;

	@Value("${playground.gcp.credentials:`epsilon-live-gcp.json`")
	private String credentialsPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShortURLController.class);

	@Override
	@Post
	public ShortURLResponse shorten(@Body ShortURLRequest request) {
		var response = new ShortURLResponse();
		try {
			Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
					.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath))).build()
					.getService();

			if (request.getContent() != null) {
				String content = request.getContent();
				String shortened = getShortened(content);
				BlobId blobId = BlobId.of(bucket, shortened);

				Blob blob = storage.get(blobId);
				if (blob == null) {
					storage.create(BlobInfo.newBuilder(blobId).setContentType("text/plain").build(),
							content.getBytes());
				}

				response.setShortened(shortened);
			} else if (request.getShortened() != null) {
				String shortened = request.getShortened();
				BlobId blobId = BlobId.of(bucket, shortened);

				Blob blob = storage.get(blobId);
				if (blob != null) {
					response.setContent(new String(blob.getContent()));
				}
			}

		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store the example");
		}
		return response;
	}

	protected String getShortened(String content) {
		return UUID.nameUUIDFromBytes(content.getBytes()).toString().substring(0, 8);
	}

}
