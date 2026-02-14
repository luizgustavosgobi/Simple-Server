package br.com.luizgustavosgobi.simpleServer.http.entities.builders;

import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpResponseLine;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.URI;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

public class HttpResponseBuilder extends HttpBuilder {

    public HttpResponseBuilder(HttpMethod method, URI url, String version, String contentType, long contentLength, Object body, HttpStatus status) {
        super(method, url, version, contentType, contentLength, body, status);
    }

    public HttpResponseBuilder(HttpMethod method, URI url, String contentType, Object body) {
        super(method, url, contentType, body);
    }

    public HttpResponseBuilder(HttpStatus status) {
        super(null, status);
    }

    @Override
    public <T> ResponseEntity<T> body(T body) {
        this.body = body;
        return build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> build() {
        if (status == null)
            throw new IllegalArgumentException("Cannot build an RequestEntity, missing args!");

        HttpResponseLine responseLine = new HttpResponseLine(version, status);
        return new ResponseEntity<>(responseLine, headers, (T) body);
    }
}
