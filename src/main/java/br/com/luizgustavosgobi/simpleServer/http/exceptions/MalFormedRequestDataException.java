package br.com.luizgustavosgobi.simpleServer.http.exceptions;

public class MalFormedRequestDataException extends InvalidHttpRequestException {
    public MalFormedRequestDataException(String message) {
        super(message);
    }

    public MalFormedRequestDataException() {
        super("Malformed Request Data");
    }
}
