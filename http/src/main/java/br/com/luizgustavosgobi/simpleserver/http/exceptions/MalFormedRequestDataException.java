package br.com.luizgustavosgobi.simpleServer.http.exceptions;

public class MalFormedRequestDataException extends br.com.luizgustavosgobi.simpleServer.http.exceptions.InvalidHttpRequestException {
    public MalFormedRequestDataException(String message) {
        super(message);
    }

    public MalFormedRequestDataException() {
        super("Malformed Request Data");
    }
}
