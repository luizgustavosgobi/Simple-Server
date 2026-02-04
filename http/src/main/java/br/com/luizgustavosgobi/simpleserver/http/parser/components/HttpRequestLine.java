package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import lombok.Getter;

@Getter
public class HttpRequestLine extends br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpLine {
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

    @Override
    public String toString() {
        return method + " " + uri + " " + version + "\r\n";
    }
}
