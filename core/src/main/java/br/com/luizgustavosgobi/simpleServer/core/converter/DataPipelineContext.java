package br.com.luizgustavosgobi.simpleServer.core.converter;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class DataPipelineContext {
    private final SocketChannel channel;
    private final Map<String, Object> attributes;

    public DataPipelineContext(SocketChannel channel) {
        this.channel = channel;
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void next(Object value) {
        setAttribute("nextMsg", value);
    }

    public Object getCurrentData() {
        return attributes.get("nextMsg");
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
