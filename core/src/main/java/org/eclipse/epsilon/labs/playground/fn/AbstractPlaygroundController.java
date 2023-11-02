package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Options;

public class AbstractPlaygroundController {

    @Options("/")
    public HttpResponse<Void> options() {
        return HttpResponse.<Void>noContent()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type")
                .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
    }
    
}
