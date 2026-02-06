package br.com.luizgustavosgobi.simpleServer.core.connection;

public interface ConnectionHandler {
    void onAccept(Client client);
    void onRead(Client client, Object data);
    void onWrite(Client client);
    void onClose(Client client);
}
