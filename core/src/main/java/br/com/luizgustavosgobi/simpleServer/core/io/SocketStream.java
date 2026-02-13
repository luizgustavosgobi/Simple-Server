package br.com.luizgustavosgobi.simpleServer.core.io;

import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipeline;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipelineContext;
import com.sun.jdi.InvalidTypeException;

import java.nio.channels.SocketChannel;

public class SocketStream {
    private final DataPipeline dataPipeline;
    private final ChannelReader channelReader;
    private final ChannelWriter channelWriter;

    public SocketStream(DataPipeline dataPipeline) {
        this.dataPipeline = dataPipeline;
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
    }

    public Object read(SocketChannel channel, DataPipelineContext context) throws Exception {
        byte[] rawData = channelReader.read(channel);
        return dataPipeline.fireChannelRead(rawData, context);
    }

    public void write(SocketChannel channel, DataPipelineContext context, Object data) throws Exception {
        if (data == null) return;

        Object convertedData = dataPipeline.fireChannelWrite(data, context);

        if (convertedData instanceof byte[] byteArray) channelWriter.write(channel, byteArray);
        else if (convertedData instanceof String s) channelWriter.write(channel, s.getBytes());
        else throw new InvalidTypeException("Cannot write data to an socket with the given type! only byte[] and String accepted");
    }
}
