package br.com.luizgustavosgobi.simpleServer.http.entities.builders;

import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpRequestLine;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.URI;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;

public class HttpRequestBuilder extends HttpBuilder {

    public HttpRequestBuilder(HttpMethod method, URI url, String contentType, long contentLength, Object body) {
        super(method, url, contentType, contentLength, body);
    }

    public HttpRequestBuilder(HttpMethod method, URI url, String contentType, Object body) {
        super(method, url, contentType, body);
    }

    public HttpRequestBuilder(HttpMethod method, URI url) {
        super(method, url);
    }

    @Override
    public <T> RequestEntity<T> body(T body) {
        this.body = body;
        return build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RequestEntity<T> build() {
        if (method == null || uri == null)
            throw new IllegalArgumentException("Cannot build an RequestEntity, missing args!");

        HttpRequestLine requestLine = new HttpRequestLine(method, uri, version);
        return new RequestEntity<>(requestLine, headers, (T) body);
    }

}
