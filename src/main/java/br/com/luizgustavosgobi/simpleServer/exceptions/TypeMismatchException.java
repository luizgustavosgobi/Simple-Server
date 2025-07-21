package br.com.luizgustavosgobi.simpleServer.exceptions;

public class TypeMismatchException extends GenericServerException {
    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException() {
        this("Type mismatch");
    }
}
