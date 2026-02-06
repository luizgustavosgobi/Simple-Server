package br.com.luizgustavosgobi.simpleServer.core.connection;

import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipelineContext;
import br.com.luizgustavosgobi.simpleServer.core.io.WriteScheduler;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Client implements AutoCloseable {
    private static final int MAX_PENDING_WRITES = 100;

    protected final SocketChannel channel;
    protected final InetSocketAddress address;
    protected final InetSocketAddress localAddress;

    protected final DataPipelineContext dataPipelineContext;

    private final ConcurrentLinkedQueue<Object> writeQueue;
    private final AtomicInteger pendingWrites;

    private SelectionKey selectionKey;
    private WriteScheduler writeScheduler;

    public Client(SocketChannel channel, WriteScheduler scheduler) throws IOException {
        this.channel = channel;
        this.address = (InetSocketAddress) channel.getRemoteAddress();
        this.localAddress = (InetSocketAddress) channel.getLocalAddress();
        this.writeScheduler = scheduler;

        this.dataPipelineContext = new DataPipelineContext(channel);
        this.writeQueue = new ConcurrentLinkedQueue<>();
        this.pendingWrites = new AtomicInteger(0);
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public boolean write(Object data) {
        if (data == null) return false;

        if (pendingWrites.get() >= MAX_PENDING_WRITES) {
            Logger.Warn(Client.class, "Write queue full for " + address + ", dropping write event");
            return false;
        }

        writeQueue.offer(data);
        pendingWrites.incrementAndGet();

        writeScheduler.scheduleWrite(this);

        return true;
    }

    public Object pollWrite() {
        Object data = writeQueue.poll();

        if (data != null)
            pendingWrites.decrementAndGet();

        return data;
    }

    public boolean hasWrites() {
        return !writeQueue.isEmpty();
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    @Override
    public void close() throws IOException {
        writeQueue.clear();
        pendingWrites.set(0);

        channel.shutdownInput();
        channel.shutdownOutput();
        channel.socket().close();
        channel.close();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public DataPipelineContext getDataPipelineContext() {
        return dataPipelineContext;
    }

    @Override
    public String toString() {
        return "Client{" +
                "channel=" + channel +
                ", address=" + address +
                ", localAddress=" + localAddress +
                '}';
    }
}
