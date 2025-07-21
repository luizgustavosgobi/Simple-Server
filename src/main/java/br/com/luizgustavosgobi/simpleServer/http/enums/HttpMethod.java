package br.com.luizgustavosgobi.simpleServer.http.enums;

public enum HttpMethod {
    POST,
    PUT,
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    TRACE,
    PATCH;

    public static HttpMethod parse(String method) {
        try {
            return valueOf(method.toUpperCase());
        }
        catch (Exception e) {
            return null;
        }
    }
}
