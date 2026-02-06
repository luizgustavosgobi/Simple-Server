package br.com.luizgustavosgobi.simpleServer.core.converter;

import java.util.ArrayList;
import java.util.List;

public class DataPipeline {
    private final List<ChanelHandler> handlers;

    public DataPipeline() {
        this.handlers = new ArrayList<>();
    }

    public DataPipeline addLast(ChanelHandler handler) {
        handlers.addLast(handler);
        return this;
    }

    public Object fireChannelRead(Object msg, DataPipelineContext context) throws Exception {
        Object currentMsg = msg;

        for (ChanelHandler handler : handlers) {
            currentMsg = handler.channelRead(context, currentMsg);
            if (currentMsg == null) break;
        }

        return currentMsg;
    }

    public Object fireChannelWrite(Object msg, DataPipelineContext context) throws Exception {
        Object currentMsg = msg;

        for (int i = handlers.size() - 1; i >= 0; i--) {
            currentMsg = handlers.get(i).channelWrite(context, currentMsg);
            if (currentMsg == null) break;
        }

        return currentMsg;
    }
}
