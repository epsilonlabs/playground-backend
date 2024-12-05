package org.eclipse.epsilon.labs.playground.fn.shorturl;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Serdeable
public class ShortURLRequest {

    public static final int MAX_CONTENT_LENGTH = 100_000;
    public static final String SHORTENED_REGEX = "[a-f0-9]{8}";

    @Size(max=MAX_CONTENT_LENGTH)
    private String content;

    @Pattern(regexp=SHORTENED_REGEX)
    private String shortened;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortened() {
        return shortened;
    }

    public void setShortened(String shortened) {
        this.shortened = shortened;
    }
}
