package br.com.luizgustavosgobi.simpleServer.core.filters;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;

import java.util.HashMap;
import java.util.Map;

public class FilterContext {
    protected final Object request;
    protected Object response;

    protected Client client;

    protected final Map<String, Object> attributes;

    protected boolean chainStopped;

    public FilterContext(Client client, Object request) {
        this.request = request;
        this.attributes = new HashMap<>();
        this.chainStopped = false;
        this.client = client;
    }

    public Object getRequest() {
        return request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }


    public Client getClient() {
        return client;
    }


    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }



    public void stopChain() {
        this.chainStopped = true;
    }

    public boolean isChainStopped() {
        return chainStopped;
    }
}

