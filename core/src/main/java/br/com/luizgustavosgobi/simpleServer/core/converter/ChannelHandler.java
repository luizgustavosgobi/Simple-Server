package br.com.luizgustavosgobi.simpleServer.core.converter;

public interface ChannelHandler {
    Object channelRead(DataPipelineContext ctx, Object msg) throws Exception;
    Object channelWrite(DataPipelineContext ctx, Object msg) throws Exception;
}
