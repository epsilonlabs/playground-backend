package org.eclipse.epsilon.labs.playground.standalone;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

@Serdeable
public class ShortURLMessage extends AbstractPlaygroundResponse {

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
