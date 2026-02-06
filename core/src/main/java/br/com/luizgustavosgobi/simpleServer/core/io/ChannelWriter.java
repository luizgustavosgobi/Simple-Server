package br.com.luizgustavosgobi.simpleServer.core.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelWriter {

    public void write(SocketChannel channel, byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
