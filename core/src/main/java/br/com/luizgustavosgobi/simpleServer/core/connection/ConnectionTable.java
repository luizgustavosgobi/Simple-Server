package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectionTable implements ConnectionTablePort {
    private final HashSet<SocketChannel> connections;

    public ConnectionTable() {
        this.connections = new HashSet<>();
    }

    @Override
    public void add(SocketChannel s) {
        connections.add(s);
    }

    @Override
    public void remove(SocketChannel s) {
        connections.remove(s);
    }

    @Override
    public void disconnect(SocketChannel s) throws IOException {
        s.close();
        connections.remove(s);
    }

    @Override
    public Set<SocketChannel> getConnections() {
        return connections;
    }

    @Override
    public String toString() {
        List<SocketChannel> snapshot = new ArrayList<>(connections);

        List<String[]> rows = new ArrayList<>();
        int idx = 1;
        int idxWidth = Math.max(5, String.valueOf(snapshot.size()).length() + 1);
        int remoteWidth = "Remote Address".length();
        int localWidth = "Local Address".length();
        int statusWidth = "State".length();

        for (SocketChannel sc : snapshot) {
            String remote = "-";
            String local = "-";
            String state = (sc != null && sc.isOpen()) ? "OPEN" : "CLOSED";

            if (sc != null) {
                try {
                    if (sc.getRemoteAddress() != null) remote = sc.getRemoteAddress().toString();
                } catch (IOException e) {
                    remote = "?";
                }
                try {
                    if (sc.getLocalAddress() != null) local = sc.getLocalAddress().toString();
                } catch (IOException e) {
                    local = "?";
                }
            }

            String indexStr = String.valueOf(idx++);
            rows.add(new String[]{indexStr, remote, local, state});

            if (remote.length() > remoteWidth) remoteWidth = remote.length();
            if (local.length() > localWidth) localWidth = local.length();
            if (state.length() > statusWidth) statusWidth = state.length();
            if (indexStr.length() > idxWidth) idxWidth = indexStr.length();
        }

        String fmt = String.format(" %%-%ds | %%-%ds | %%-%ds | %%-%ds\n", idxWidth, remoteWidth, localWidth, statusWidth);
        String sep = new String(new char[idxWidth+1]).replace('\0', '-') + "-+-" +
                new String(new char[remoteWidth]).replace('\0', '-') + "-+-" +
                new String(new char[localWidth]).replace('\0', '-') + "-+-" +
                new String(new char[statusWidth]).replace('\0', '-');

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(fmt, "#", "Remote Address", "Local Address", "State"));
        sb.append(sep).append('\n');

        for (String[] r : rows) {
            sb.append(String.format(fmt, r[0], r[1], r[2], r[3]));
        }

        if (rows.isEmpty()) {
            return "(no active connections)";
        }

        return sb.toString();
    }
}