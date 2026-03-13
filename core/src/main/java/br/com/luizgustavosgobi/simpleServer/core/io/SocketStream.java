package br.com.luizgustavosgobi.simpleServer.core.io;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.converter.ConverterPipelineProxy;
import com.sun.jdi.InvalidTypeException;

public class SocketStream {
    private final ConverterPipelineProxy converterPipelineProxy;
    private final ChannelReader channelReader;
    private final ChannelWriter channelWriter;

    public SocketStream(ConverterPipelineProxy converterPipeline) {
        this.converterPipelineProxy = converterPipeline;
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
    }

    public Object read(Client client) throws Exception {
        byte[] rawData = channelReader.read(client.getChannel());

        if (converterPipelineProxy == null) return rawData;
        else return converterPipelineProxy.doDecode(client, rawData);
    }

    public void write(Client client, Object data) throws Exception {
        if (data == null) return;

        Object convertedData = data;
        if (converterPipelineProxy != null) convertedData = converterPipelineProxy.doEncode(client, data);

        if (convertedData instanceof byte[] byteArray) channelWriter.write(client.getChannel(), byteArray);
        else if (convertedData instanceof String s) channelWriter.write(client.getChannel(), s.getBytes());
        else throw new InvalidTypeException("Cannot write data to an socket with the given type! only byte[] and String accepted");
    }
}
