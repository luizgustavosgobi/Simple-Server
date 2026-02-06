package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.net.InetSocketAddress;
import java.util.*;

public class ClientConnectionTable implements ConnectionTable {
    private final Map<InetSocketAddress, Client> connections;

    public ClientConnectionTable() {
        this.connections = new HashMap<>();
    }

    @Override
    public void add(Client client) {
        if (client == null) return;
        connections.put(client.getAddress(), client);
    }

    @Override
    public Client get(InetSocketAddress address) {
        if (connections.containsKey(address))
            return connections.get(address);

        return null;
    }

    @Override
    public void remove(Client client) {
        if (client == null) return;
        connections.remove(client.getAddress());
    }

    @Override
    public Set<Client> getConnections() {
        return Set.copyOf(connections.values());
    }

    @Override
    public String toString() {
        List<Client> snapshot = List.copyOf(connections.values());

        List<String[]> rows = new ArrayList<>();
        int idx = 1;
        int idxWidth = Math.max(5, String.valueOf(snapshot.size()).length() + 1);
        int remoteWidth = "Remote Address".length();
        int localWidth = "Local Address".length();
        int statusWidth = "State".length();

        for (Client sc : snapshot) {
            String remote = "-";
            String local = "-";
            String state = (sc != null && sc.isConnected()) ? "OPEN" : "CLOSED";

            if (sc != null) {
                if (sc.getAddress() != null) remote = sc.getAddress().toString();
                if (sc.getLocalAddress() != null) local = sc.getLocalAddress().toString();
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