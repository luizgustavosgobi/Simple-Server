package br.com.luizgustavosgobi.simpleServer.core.converter;

public abstract class ChanelOutboundHandler implements ChanelHandler {
    @Override
    public Object channelRead(DataPipelineContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public abstract Object channelWrite(DataPipelineContext ctx, Object msg) throws Exception;
}
