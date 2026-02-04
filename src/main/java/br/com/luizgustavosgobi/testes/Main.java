package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.Console;
import br.com.luizgustavosgobi.simpleServer.core.Server;
import br.com.luizgustavosgobi.simpleServer.core.ServerFactory;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Server server = ServerFactory.create(Main.class, 80, new ConnHandler());
        server.start();

        Console.getInstance().loop();
    }
}