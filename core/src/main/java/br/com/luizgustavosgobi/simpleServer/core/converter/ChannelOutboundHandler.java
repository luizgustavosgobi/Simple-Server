package br.com.luizgustavosgobi.simpleServer.core.converter;

public abstract class ChannelOutboundHandler implements ChannelHandler {
    @Override
    public Object channelRead(DataPipelineContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public abstract Object channelWrite(DataPipelineContext ctx, Object msg) throws Exception;
}
