package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.nio.channels.SocketChannel;

public class ConnHandler implements ConnectionHandlerPort {

    @Override
    public void onAccept(SocketChannel client) {
        Logger.Info("Aceito! " + client.socket().getRemoteSocketAddress());
    }

    @Override
    public void onRead(SocketChannel client, byte[] data) {
        Logger.Info("Lido! " + client.socket().getRemoteSocketAddress());
    }

    @Override
    public void onClose(SocketChannel client) {
        Logger.Info("Desconectado! " + client.socket().getRemoteSocketAddress());
    }
}
