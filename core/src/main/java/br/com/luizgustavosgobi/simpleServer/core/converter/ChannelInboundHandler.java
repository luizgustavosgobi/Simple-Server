package br.com.luizgustavosgobi.simpleServer.core.converter;

public abstract class ChannelInboundHandler implements ChannelHandler {

    @Override
    public abstract Object channelRead(DataPipelineContext ctx, Object msg) throws Exception;

    @Override
    public Object channelWrite(DataPipelineContext ctx, Object msg) throws Exception {
        return msg;
    }
}
