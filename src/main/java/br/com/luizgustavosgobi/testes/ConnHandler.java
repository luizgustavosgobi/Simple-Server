package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

public class ConnHandler implements ConnectionHandler {

    @Override
    public void onAccept(Client client) {
        Logger.Info("Aceito! " + client.getAddress());

        client.write("Bem-vindo ao servidor!\n");
    }

    @Override
    public void onRead(Client client, Object data) {
        if (data instanceof String s) {
            Logger.Info("Lido de " + client.getAddress() + " " + "string" + ": " + s);
            client.write("Echo: " + s);

        } else if (data instanceof byte[] bytes) {
            Logger.Info("Lido de " + client.getAddress() + " " + "byte" + ": " + new String(bytes).strip());
            client.write("Echo (bytes): " + new String(bytes));

        } else {
            Logger.Info("Lido de " + client.getAddress() + ": ?");
        }
    }

    @Override
    public void onWrite(Client client) {
        Logger.Info("Escrito em " + client.getAddress());
    }

    @Override
    public void onClose(Client client) {
        Logger.Info("Desconectado! " + client.getAddress());
    }
}
