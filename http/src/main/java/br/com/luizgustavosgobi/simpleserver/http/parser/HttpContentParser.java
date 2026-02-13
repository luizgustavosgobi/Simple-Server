package br.com.luizgustavosgobi.simpleServer.http.parser;

import br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.InvalidJsonException;
import br.com.luizgustavosgobi.simpleServer.http.parser.handlers.MultipartFormDataHandler;
import br.com.luizgustavosgobi.simpleServer.http.types.Json;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum HttpContentParser {
    JSON ((body, args) -> {
        JsonNode node = null;

        try {
            node = getObjectMapper().readTree(body);
        } catch (JacksonException e) {
            throw new InvalidJsonException();
        }

        return new Json(node);
    }),

    FORM_DATA ((body, args) -> MultipartFormDataHandler.parse(body, args[0]));

    @FunctionalInterface
    private interface ContentParser {
        Object parse(String body, String ...args) throws HttpException;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final ContentParser parseFunction;

    HttpContentParser(ContentParser parseFunction) {
        this.parseFunction = parseFunction;
    }

    public Object parse(String body, String ...args) throws HttpException {
        return parseFunction.parse(body, args);
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null)
            objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
