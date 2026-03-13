package br.com.luizgustavosgobi.simpleServer.core.converter;

public interface ChannelHandler {
    Object channelRead(ConverterContext ctx, Object msg) throws Exception;
    Object channelWrite(ConverterContext ctx, Object msg) throws Exception;
}
