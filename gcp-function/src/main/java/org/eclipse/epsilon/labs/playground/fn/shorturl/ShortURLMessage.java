package org.eclipse.epsilon.labs.playground.fn.shorturl;

import org.eclipse.epsilon.labs.playground.fn.AbstractPlaygroundResponse;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ShortURLMessage extends AbstractPlaygroundResponse {

    private String content;
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
