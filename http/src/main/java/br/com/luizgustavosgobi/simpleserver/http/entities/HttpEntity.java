package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpLine;

import java.lang.reflect.Type;

public abstract class HttpEntity<T> {
    protected final HttpLine httpLine;
    protected final HttpHeaders headers;
    protected final T body;

    public HttpEntity(HttpLine httpLine, HttpHeaders headers, T body) {
        this.httpLine = httpLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpEntity(HttpLine httpLine, T body) {
        this(httpLine, new HttpHeaders(), body);
    }

    public HttpEntity(HttpLine httpLine, HttpHeaders headers) {
        this(httpLine, headers, null);
    }

    public boolean hasBody() {
        return this.body != null;
    }

    public Type getBodyType() {
        if (this.body == null) return null;

        T body = this.getBody();
        return body.getClass();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && other.getClass() == this.getClass()) {
            HttpEntity<?> otherEntity = (HttpEntity<?>) other;
            return this.headers == otherEntity.headers && this.body == otherEntity.body;
        } else {
            return false;
        }
    }

    public HttpLine getHttpLine() {
        return httpLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }
}
