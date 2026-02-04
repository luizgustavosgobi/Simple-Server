package br.com.luizgustavosgobi.simpleServer.core.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketStream {
    private static final int BUFFER_SIZE = 8192;

    private static final ThreadLocal<ByteBuffer> THREAD_LOCAL_BUFFER = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(BUFFER_SIZE));
    private static final ThreadLocal<ExpandableBuffer> THREAD_LOCAL_EXPANDABLE = ThreadLocal.withInitial(() -> new ExpandableBuffer(BUFFER_SIZE));

    public static byte[] read(SocketChannel channel) throws IOException {
        if (!channel.isOpen())
            throw new EOFException("Connection Closed");

        ExpandableBuffer expandableBuffer = THREAD_LOCAL_EXPANDABLE.get();
        expandableBuffer.clear();

        ByteBuffer buffer = THREAD_LOCAL_BUFFER.get();
        buffer.clear();

        int totalRead = 0;
        int read;

        while ((read = channel.read(buffer)) > 0) {
            System.out.println(read);
            buffer.flip();
            expandableBuffer.append(buffer);
            totalRead += read;
            buffer.clear();

            if (channel.socket().getInputStream().available() == 0) {
                break;
            }
        }

        THREAD_LOCAL_BUFFER.remove();
        THREAD_LOCAL_EXPANDABLE.remove();

        if (read == -1)
            throw new EOFException("Connection Closed");

        return totalRead > 0 ? expandableBuffer.toByteArray() : new byte[0];
    }

    public static void write(SocketChannel channel, byte[] data) throws IOException {
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
