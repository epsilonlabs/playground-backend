package org.eclipse.epsilon.labs.playground.fn.shorturl;

import java.io.FileInputStream;
import java.util.UUID;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/shorturl")
public class ShortURLController {

	@Post("/")
	public ShortURLMessage shorten(@Body ShortURLMessage request) {
		var response = new ShortURLMessage();
		try {
			Storage storage = StorageOptions.newBuilder().setProjectId("epsilon-live-gcp")
					.setCredentials(GoogleCredentials.fromStream(new FileInputStream("epsilon-live-gcp.json"))).build()
					.getService();

			if (request.getContent() != null) {
				String content = request.getContent();
				String shortened = getShortened(content);
				BlobId blobId = BlobId.of("epsilon-live-gcp.appspot.com", shortened);

				Blob blob = storage.get(blobId);
				if (blob == null) {
					storage.create(BlobInfo.newBuilder(blobId).setContentType("text/plain").build(),
							content.getBytes());
				}

				response.setShortened(shortened);
			} else if (request.getShortened() != null) {
				String shortened = request.getShortened();
				BlobId blobId = BlobId.of("epsilon-live-gcp.appspot.com", shortened);

				Blob blob = storage.get(blobId);
				if (blob != null) {
					response.setContent(new String(blob.getContent()));
				}
			}

		} catch (Throwable t) {
			response.setError(t.getMessage());
			response.setOutput(t.getMessage());
		}
		return response;
	}

	protected String getShortened(String content) {
		return UUID.nameUUIDFromBytes(content.getBytes()).toString().substring(0, 8);
	}

}
