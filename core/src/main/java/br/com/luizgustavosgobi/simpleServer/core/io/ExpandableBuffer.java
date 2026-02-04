package br.com.luizgustavosgobi.simpleServer.core.io;

import java.nio.ByteBuffer;

public class ExpandableBuffer {
    private static final int MAX_BUFFER_SIZE = 65536;

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