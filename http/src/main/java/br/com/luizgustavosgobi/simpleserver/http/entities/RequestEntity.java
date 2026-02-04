package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpLine;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpRequestLine;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.URI;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.function.Consumer;


public class RequestEntity<T> extends HttpEntity<T> {
    private final Type type;

    public RequestEntity(HttpLine httpLine, HttpHeaders headers, T body, Type type) {
        super(httpLine, headers, body);
        this.type = type;
    }

    public RequestEntity(HttpLine httpLine, HttpHeaders headers, T body) {
        this(httpLine, headers, body, null);
    }

    public RequestEntity(HttpHeaders headers, T body, HttpMethod method, URI url, String version, Type type) {
        this(new HttpRequestLine(method, url, version), headers, body, type);
    }

    public RequestEntity(HttpHeaders headers, T body, HttpMethod method, URI url, String version) {
        this(new HttpRequestLine(method, url, version), headers, body, null);
    }

    //-------------------
    //      Functions
    //-------------------

    public static GenericBuilder method(HttpMethod method, URI url) {
        return new DefaultBuilder(method, url);
    }

    @Override
    public String toString() {
        return httpLine.toString() + headers.toString() + "\r\n" + body.toString();
    }

    //-------------------
    //      Getters
    //-------------------

    public HttpMethod getMethod() {
        return ((HttpRequestLine) this.getHttpLine()).getMethod();
    }
    public URI getUri() {
        return ((HttpRequestLine) this.getHttpLine()).getUri();
    }
    public String getVersion() {
        return this.getHttpLine().getVersion();
    }
    public String getPath() {
        return this.getUri().getPath();
    }
    public String getPathVariable(String key) { return ""; }
    public Type getType() {
        if (this.type == null) {
            T body = this.getBody();
            if (body != null) {
                return body.getClass();
            }
        }

        return this.type;
    }

    @Override
    public HttpRequestLine getHttpLine() {
        return (HttpRequestLine) super.getHttpLine();
    }

    //-------------------------
    //      Builders
    //-------------------------

    private static class DefaultBuilder implements GenericBuilder {
        private final HttpMethod method;
        private final HttpHeaders headers = new HttpHeaders();
        private String version;
        private final URI uri;

        DefaultBuilder(HttpMethod method, URI url, String version) {
            this.method = method;
            this.version = version;
            this.uri = url;
        }

        DefaultBuilder(HttpMethod method, URI url) {
            this(method, url, null);
        }

        DefaultBuilder(HttpMethod method) {
            this(method, null, null);
        }

        public GenericBuilder header(String headerName, String... headerValues) {
            StringBuilder builder = new StringBuilder();
            for(String headerValue : headerValues) {
                builder.append(headerValue).append(", ");
            }

            this.headers.add(headerName, builder.toString());
            return this;
        }

        public GenericBuilder headers(HttpHeaders headers) {
            if (headers != null) {
                this.headers.addAll(headers);
            }

            return this;
        }

        public GenericBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        public GenericBuilder accept(String... acceptableMediaTypes) {
            StringBuilder builder = new StringBuilder();
            for (String acceptableMediaType : acceptableMediaTypes) {
                builder.append(acceptableMediaType).append(", ");
            }
            this.headers.add("Accept", builder.toString());
            return this;
        }

        public GenericBuilder acceptCharset(Charset... acceptableCharsets) {
            StringBuilder builder = new StringBuilder();
            for (Charset acceptableCharset : acceptableCharsets) {
                builder.append(acceptableCharset).append(", ");
            }
            this.headers.add("Accept-Charset", builder.toString());
            return this;
        }

        public GenericBuilder contentLength(long contentLength) {
            this.headers.add("Content-Length", String.valueOf(contentLength));
            return this;
        }

        public GenericBuilder contentType(String contentType) {
            this.headers.add("Content-Type", contentType);
            return this;
        }

        public GenericBuilder ifModifiedSince(ZonedDateTime ifModifiedSince) {
            this.headers.add("If-Modified-Since", ifModifiedSince.toString());

            return this;
        }

        public GenericBuilder ifModifiedSince(Instant ifModifiedSince) {
            this.headers.add("If-Modified-Since", ifModifiedSince.toString());
            return this;
        }

        public GenericBuilder ifModifiedSince(long ifModifiedSince) {
            this.headers.add("Last-Modified", String.valueOf(Instant.ofEpochMilli(ifModifiedSince)));
            return this;
        }

        public GenericBuilder ifNoneMatch(String... ifNoneMatches) {
            StringBuilder builder = new StringBuilder();
            for (String ifNoneMatch : ifNoneMatches) {
                builder.append(ifNoneMatch).append(", ");
            }
            this.headers.add("If-None-Match", builder.toString());
            return this;
        }

        public GenericBuilder version(String version) {
            this.version = version;
            return this;
        }

        public RequestEntity<Void> build() {
            return this.<Void>buildInternal(null, null);
        }

        public <T> RequestEntity<T> body(T body) {
            return this.<T>buildInternal(body, null);
        }

        public <T> RequestEntity<T> body(T body, Type type) {
            return this.<T>buildInternal(body, type);
        }

        private <T> RequestEntity<T> buildInternal(T body, Type type) {
            if (this.uri != null) {
                return new RequestEntity<T>(this.headers, body, this.method, this.uri, version, type);
            } else return null;
        }
    }

    public interface GenericBuilder extends HeaderBuilder<GenericBuilder>, BodyBuilder<GenericBuilder> {}

    private interface BodyBuilder<C extends BodyBuilder<C>> {
        C contentLength(long contentLength);
        C contentType(String contentType);

        <T> RequestEntity<T> body(T body);
        <T> RequestEntity<T> body(T body, Type type);
    }

    private interface HeaderBuilder<C extends HeaderBuilder<C>> {
        C header(String headerName, String... headerValues);

        C headers(HttpHeaders headers);
        C headers(Consumer<HttpHeaders> headersConsumer);

        C accept(String... acceptableMediaTypes);
        C acceptCharset(Charset... acceptableCharsets);

        C ifModifiedSince(ZonedDateTime ifModifiedSince);
        C ifModifiedSince(Instant ifModifiedSince);
        C ifModifiedSince(long ifModifiedSince);

        C ifNoneMatch(String... ifNoneMatches);

        RequestEntity<Void> build();
    }
}
