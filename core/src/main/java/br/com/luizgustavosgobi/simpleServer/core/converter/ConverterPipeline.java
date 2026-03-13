package br.com.luizgustavosgobi.simpleServer.core.converter;

import java.util.ArrayList;
import java.util.List;

public class ConverterPipeline {
    private final List<ChannelHandler> handlers;

    public ConverterPipeline() {
        this.handlers = new ArrayList<>();
    }

    public ConverterPipeline addLast(ChannelHandler handler) {
        handlers.addLast(handler);
        return this;
    }

    /**
     * Processo de transformar o dado vindo do client para um outro dado!
     * ex: byte[] -> String
     */
    public Object decode(Object msg, ConverterContext context) throws Exception {
        Object currentMsg = msg;

        for (ChannelHandler handler : handlers) {
            currentMsg = handler.channelRead(context, currentMsg);
            if (currentMsg == null) break;
        }

        return currentMsg;
    }

    /**
     * Processo de transformar o dado que irá ser mandado para o client!
     * ex: String -> byte[]
     */
    public Object encode(Object msg, ConverterContext context) throws Exception {
        Object currentMsg = msg;

        for (int i = handlers.size() - 1; i >= 0; i--) {
            currentMsg = handlers.get(i).channelWrite(context, currentMsg);
            if (currentMsg == null) break;
        }

        return currentMsg;
    }
}
