package br.com.luizgustavosgobi.simpleServer.core.converter;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;

import java.util.HashMap;
import java.util.Map;

public class ConverterContext {
    private final Client client;
    private final Map<String, Object> attributes;

    public ConverterContext(Client client) {
        this.client = client;
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public Client getClient() {
        return client;
    }

    public void next(Object value) {
        setAttribute("nextMsg", value);
    }
    public void shouldClose(boolean value) {
        setAttribute("shouldClose", value);
    }
    public void closeAfterWrite(boolean value) {
        setAttribute("closeAfterWrite", value);
    }
}
