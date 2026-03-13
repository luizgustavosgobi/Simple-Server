package br.com.luizgustavosgobi.simpleServer.core.converter;

public abstract class ChannelOutboundHandler implements ChannelHandler {
    @Override
    public Object channelRead(ConverterContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public abstract Object channelWrite(ConverterContext ctx, Object msg) throws Exception;
}
