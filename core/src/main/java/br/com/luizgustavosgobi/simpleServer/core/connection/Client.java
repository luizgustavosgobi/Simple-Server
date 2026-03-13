package br.com.luizgustavosgobi.simpleServer.core.connection;

import br.com.luizgustavosgobi.simpleServer.core.io.WriteQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends WriteQueue implements AutoCloseable {
    protected final SocketChannel channel;
    protected final InetSocketAddress address;
    protected final InetSocketAddress localAddress;

    private SelectionKey selectionKey;

    protected final Map<String, Object> attributes;

    public Client(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.address = (InetSocketAddress) channel.getRemoteAddress();
        this.localAddress = (InetSocketAddress) channel.getLocalAddress();
        this.attributes = new ConcurrentHashMap<>();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public boolean write(Object data) {
        boolean result = super.write(data);
        addKeyInterestIn(SelectionKey.OP_WRITE);

        return result;
    }

    public boolean shouldClose() {
        Object shouldClose = attributes.get("shouldClose");
        if (shouldClose == null) return false;
        return (boolean) shouldClose && !hasWrites();
    }
    public void shouldClose(boolean value) {
        attributes.put("shouldClose", value);
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
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
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }
    public void addKeyInterestIn(int ops) {
        selectionKey.interestOpsOr(ops);
        selectionKey.selector().wakeup();
    }
    public void removeKeyInterestIn(int ops) {
        selectionKey.interestOpsAnd(~ops);
        selectionKey.selector().wakeup();
    }

    @Override
    public String toString() {
        return "Client{" +
                "channel=" + channel +
                ", address=" + address +
                ", localAddress=" + localAddress +
                '}';
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
}
