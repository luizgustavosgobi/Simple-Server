package br.com.luizgustavosgobi.simpleServer.core.converter;

public class ByteToStringCodec implements ChannelHandler {

    @Override
    public String channelRead(ConverterContext ctx, Object msg) throws Exception {
        byte[] rawData = (byte[]) msg;

        return new String(rawData);
    }

    @Override
    public byte[] channelWrite(ConverterContext ctx, Object msg) throws Exception {
        String rawData = (String) msg;

        return rawData.getBytes();
    }
}
