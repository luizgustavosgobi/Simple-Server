package br.com.luizgustavosgobi.simpleServer.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DataUtils {
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 65536;

    private static final ThreadLocal<ByteBuffer> THREAD_LOCAL_BUFFER = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(BUFFER_SIZE));
    private static final ThreadLocal<ExpandableBuffer> THREAD_LOCAL_EXPANDABLE = ThreadLocal.withInitial(() -> new ExpandableBuffer(BUFFER_SIZE));

    public static byte[] readData(SocketChannel channel) throws IOException {
        if (!channel.isOpen()) {
            return null;
        }

        ExpandableBuffer expandableBuffer = THREAD_LOCAL_EXPANDABLE.get();
        expandableBuffer.clear();

        ByteBuffer buffer = THREAD_LOCAL_BUFFER.get();
        buffer.clear();

        int totalRead = 0;
        int read;

        while ((read = channel.read(buffer)) > 0) {
            buffer.flip();
            expandableBuffer.append(buffer);
            totalRead += read;
            buffer.clear();

            if (channel.socket().getInputStream().available() == 0) {
                break;
            }
        }

        if (read == -1) {
            channel.close();
            return totalRead > 0 ? expandableBuffer.toByteArray() : null;
        }

        return totalRead > 0 ? expandableBuffer.toByteArray() : new byte[0];
    }

    public static void writeData(SocketChannel channel, byte[] data) throws IOException {
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

    private static class ExpandableBuffer {
        private ByteBuffer buffer;
        private int position = 0;

        ExpandableBuffer(int initialSize) {
            this.buffer = ByteBuffer.allocateDirect(initialSize);
        }

        void clear() {
            position = 0;
            buffer.clear();
        }

        void append(ByteBuffer data) {
            ensureCapacity(data.remaining());

            // Copiar dados eficientemente
            int oldLimit = buffer.limit();
            buffer.position(position);
            buffer.limit(position + data.remaining());
            buffer.put(data);
            position = buffer.position();
            buffer.limit(oldLimit);
        }

        private void ensureCapacity(int additionalBytes) {
            if (position + additionalBytes > buffer.capacity()) {
                int newCapacity = Math.min(
                        Math.max(buffer.capacity() * 2, position + additionalBytes),
                        MAX_BUFFER_SIZE
                );

                ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity);
                buffer.flip();
                buffer.limit(position);
                newBuffer.put(buffer);
                buffer = newBuffer;
            }
        }

        byte[] toByteArray() {
            byte[] result = new byte[position];
            buffer.flip();
            buffer.limit(position);
            buffer.get(result);
            return result;
        }
    }
}
