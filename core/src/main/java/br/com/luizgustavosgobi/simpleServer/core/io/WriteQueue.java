package br.com.luizgustavosgobi.simpleServer.core.io;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WriteQueue {
    protected static final int MAX_PENDING_WRITES = 100;

    protected final ConcurrentLinkedQueue<Object> writeQueue;
    protected final AtomicInteger pendingWrites;

    public WriteQueue() {
        this.writeQueue = new ConcurrentLinkedQueue<>();
        this.pendingWrites = new AtomicInteger(0);
    }

    public boolean hasWrites() {
        return !writeQueue.isEmpty();
    }

    public Object pollWrite() {
        Object data = writeQueue.poll();

        if (data != null)
            pendingWrites.decrementAndGet();

        return data;
    }

    public boolean write(Object data) {
        if (data == null) return false;

        if (pendingWrites.get() >= MAX_PENDING_WRITES) {
            Logger.Warn(Client.class, "Write queue full, dropping write event");
            return false;
        }

        writeQueue.offer(data);
        pendingWrites.incrementAndGet();

        return true;
    }
}
