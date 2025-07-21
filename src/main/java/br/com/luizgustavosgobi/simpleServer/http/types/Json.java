package br.com.luizgustavosgobi.simpleServer.http.types;

import com.fasterxml.jackson.databind.JsonNode;

public record Json(JsonNode json) implements BodyData {

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String makeBody() {
        return json.toString();
    }

    @Override
    public String toString() {
        return json.toPrettyString();
    }
}
