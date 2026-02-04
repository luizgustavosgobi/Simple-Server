package br.com.luizgustavosgobi.simpleServer.http.exceptions;

public class InvalidJsonException extends br.com.luizgustavosgobi.simpleServer.http.exceptions.InvalidHttpRequestException {
    public InvalidJsonException(String message) {
        super(message);
    }

    public InvalidJsonException() {
        super("Invalid JSON");
    }
}
