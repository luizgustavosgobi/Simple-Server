package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.URI;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;

public abstract class HttpBuilder implements HeaderBuilder<HttpBuilder>, BodyBuilder<HttpBuilder> {
    protected HttpMethod method;
    protected HttpHeaders headers;
    protected String version;
    protected URI uri;

    protected Object body;
    protected String contentType;
    protected long contentLength;

    protected HttpStatus status;

    HttpBuilder(HttpMethod method, URI url, String version, String contentType, long contentLength, Object body, HttpStatus status) {
        this.method = method;
        this.version = version;
        this.uri = url;
        this.headers = new HttpHeaders();

        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;

        this.status = status;
    }

    HttpBuilder(HttpMethod method, URI url, String contentType, long contentLength, Object body) {
        this(method, url, "HTTP/1.1", contentType, contentLength, body, HttpStatus.OK);
    }

    HttpBuilder(HttpMethod method, URI url, String contentType, Object body) {
        this(method, url, "HTTP/1.1", contentType, 0, body, HttpStatus.OK);
    }

    HttpBuilder(HttpMethod method, URI url) {
        this(method, url, "HTTP/1.1", null, 0, null, HttpStatus.OK);
    }

    HttpBuilder(String version, String contentType, long contentLength, Object body, HttpStatus status) {
        this(null, null, version, contentType, contentLength, body, status);
    }

    HttpBuilder(String contentType, long contentLength, Object body, HttpStatus status) {
        this("HTTP/1.1", contentType, contentLength, body, status);
    }

    HttpBuilder(Object body, HttpStatus status) {
        this("HTTP/1.1", null, 0, body, status);
    }

    public HttpBuilder header(String headerName, String... headerValues) {
        StringBuilder builder = new StringBuilder();

        for(String headerValue : headerValues) {
            builder.append(headerValue).append(", ");
        }

        this.headers.add(headerName, builder.toString());
        return this;
    }

    public HttpBuilder headers(HttpHeaders headers) {
        if (headers != null) {
            this.headers.addAll(headers);
        }

        return this;
    }

    public HttpBuilder accept(String... acceptableMediaTypes) {
        StringBuilder builder = new StringBuilder();

        for (String acceptableMediaType : acceptableMediaTypes) {
            builder.append(acceptableMediaType).append(", ");
        }

        this.headers.add("Accept", builder.toString());
        return this;
    }

    public HttpBuilder acceptCharset(Charset... acceptableCharsets) {
        StringBuilder builder = new StringBuilder();

        for (Charset acceptableCharset : acceptableCharsets) {
            builder.append(acceptableCharset).append(", ");
        }

        this.headers.add("Accept-Charset", builder.toString());
        return this;
    }

    public HttpBuilder contentLength(long contentLength) {
        this.headers.add("Content-Length", String.valueOf(contentLength));
        return this;
    }

    public HttpBuilder contentType(String contentType) {
        this.headers.add("Content-Type", contentType);
        return this;
    }

    public HttpBuilder ifModifiedSince(ZonedDateTime ifModifiedSince) {
        this.headers.add("If-Modified-Since", ifModifiedSince.toString());

        return this;
    }

    public HttpBuilder ifModifiedSince(Instant ifModifiedSince) {
        this.headers.add("If-Modified-Since", ifModifiedSince.toString());
        return this;
    }

    public HttpBuilder ifModifiedSince(long ifModifiedSince) {
        this.headers.add("Last-Modified", String.valueOf(Instant.ofEpochMilli(ifModifiedSince)));
        return this;
    }

    public HttpBuilder ifNoneMatch(String... ifNoneMatches) {
        StringBuilder builder = new StringBuilder();

        for (String ifNoneMatch : ifNoneMatches) {
            builder.append(ifNoneMatch).append(", ");
        }

        this.headers.add("If-None-Match", builder.toString());
        return this;
    }

    public HttpBuilder version(String version) {
        this.version = version;
        return this;
    }

    public HttpBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public <T> HttpEntity<T> body(T body) {
        this.body = body;
        return this.build();
    }

    public abstract  <T> HttpEntity<T> build();
}
