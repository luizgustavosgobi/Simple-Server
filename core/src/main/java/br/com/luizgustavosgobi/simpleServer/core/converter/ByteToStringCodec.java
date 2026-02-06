package br.com.luizgustavosgobi.simpleServer.core.converter;

public class ByteToStringCodec implements ChanelHandler {

    @Override
    public String channelRead(DataPipelineContext ctx, Object msg) throws Exception {
        byte[] rawData = (byte[]) msg;

        return new String(rawData);
    }

    @Override
    public byte[] channelWrite(DataPipelineContext ctx, Object msg) throws Exception {
        String rawData = (String) msg;

        return rawData.getBytes();
    }
}
