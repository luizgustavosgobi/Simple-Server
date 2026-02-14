package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.entities.builders.HttpRequestBuilder;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpLine;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpRequestLine;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.URI;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;


public class RequestEntity<T> extends HttpEntity<T> {

    public RequestEntity(HttpLine httpLine, HttpHeaders headers, T body) {
        super(httpLine, headers, body);
    }

    public static HttpRequestBuilder method(HttpMethod method, URI url) {
        return new HttpRequestBuilder(method, url);
    }

    public HttpMethod getMethod() {
        return this.getHttpLine().getMethod();
    }

    public URI getUri() {
        return this.getHttpLine().getUri();
    }

    public String getVersion() {
        return this.getHttpLine().getVersion();
    }

    public String getPath() {
        return this.getUri().getPath();
    }

    public String getPathVariable(String key) {
        return this.getUri().getQueryParam(key);
    }

    @Override
    public HttpRequestLine getHttpLine() {
        return (HttpRequestLine) super.getHttpLine();
    }

    @Override
    public String toString() {
        return httpLine.toString() + headers.toString() + "\r\n" + body.toString();
    }
}
