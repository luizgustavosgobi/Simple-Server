package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.Console;
import br.com.luizgustavosgobi.simpleServer.core.Server;
import br.com.luizgustavosgobi.simpleServer.core.ServerFactory;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipeline;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        DataPipeline pipeline =  new DataPipeline();
        //pipeline.addLast(new ByteToStringCodec());

        Server server = ServerFactory.create(Main.class, 80, new ConnHandler(), pipeline);
        server.start();

        Console.getInstance().loop();
    }
}