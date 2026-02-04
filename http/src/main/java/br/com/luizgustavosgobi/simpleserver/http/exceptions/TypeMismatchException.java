package br.com.luizgustavosgobi.simpleServer.http.exceptions;

import br.com.luizgustavosgobi.simpleServer.core.exceptions.GenericServerException;

public class TypeMismatchException extends GenericServerException {
    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException() {
        this("Type mismatch");
    }
}
