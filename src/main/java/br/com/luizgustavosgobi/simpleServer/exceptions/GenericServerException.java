package br.com.luizgustavosgobi.simpleServer.exceptions;

public abstract class GenericServerException extends RuntimeException {
  public GenericServerException(String message) {
    super(message);
  }
}
