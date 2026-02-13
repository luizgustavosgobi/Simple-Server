package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
        add("Host", "SimpleServer");
    }

    public HttpHeaders add(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public void addAll(HttpHeaders headers) {
        this.headers.putAll(headers.getHeaders());
    }

    public void addIfAbsent(String key, String value) {
        headers.putIfAbsent(key, value);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpHeaders setContentType(String contentType) {
        headers.put("Content-Type", contentType);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }
}
