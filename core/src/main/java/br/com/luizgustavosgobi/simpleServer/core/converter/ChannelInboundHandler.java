package br.com.luizgustavosgobi.simpleServer.core.converter;

public abstract class ChannelInboundHandler implements ChannelHandler {

    @Override
    public abstract Object channelRead(ConverterContext ctx, Object msg) throws Exception;

    @Override
    public Object channelWrite(ConverterContext ctx, Object msg) throws Exception {
        return msg;
    }
}
