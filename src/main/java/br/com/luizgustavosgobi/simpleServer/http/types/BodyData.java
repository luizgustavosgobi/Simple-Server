package br.com.luizgustavosgobi.simpleServer.http.types;

import com.fasterxml.jackson.databind.JsonNode;

public interface BodyData {
    String getContentType();
    String makeBody();
    JsonNode json();
}
