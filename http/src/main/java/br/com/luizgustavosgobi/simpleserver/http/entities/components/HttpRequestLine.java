package br.com.luizgustavosgobi.simpleServer.http.entities.components;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;

public class HttpRequestLine extends HttpLine {
    private final HttpMethod method;
    private final URI uri;

    public HttpRequestLine(HttpMethod method, URI uri, String version) {
        super(version);
        this.method = method;
        this.uri = uri;
    }

    public HttpRequestLine(HttpMethod method, URI uri) {
        this(method, uri, "HTTP/1.1");
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return method + " " + uri + " " + version + "\r\n";
    }
}
