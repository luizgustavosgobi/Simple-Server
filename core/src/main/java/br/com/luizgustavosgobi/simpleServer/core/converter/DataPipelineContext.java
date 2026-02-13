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
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public SocketChannel getChannel() {
        return channel;
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
