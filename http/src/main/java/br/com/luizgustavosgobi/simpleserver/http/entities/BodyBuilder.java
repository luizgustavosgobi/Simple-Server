package br.com.luizgustavosgobi.simpleServer.http.entities;

public interface BodyBuilder <C extends BodyBuilder<C>>{

    C contentLength(long contentLength);

    C contentType(String contentType);

    <T> HttpEntity<T> body(T body);
}
