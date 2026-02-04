package br.com.luizgustavosgobi.simpleServer.core;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerRegistry {
    private static final ServerRegistry INSTANCE = new ServerRegistry();
    private final AtomicInteger serverCount = new AtomicInteger(0);
    private final Console console;

    private ServerRegistry() {
        this.console = Console.getInstance();
    }

    public static ServerRegistry getInstance() {
        return INSTANCE;
    }

    public int registerServer(Class<?> mainClass, Server server) {
        int currentIndex = serverCount.getAndIncrement();
        console.addServer(mainClass.getSimpleName(), server);
        return currentIndex;
    }

    public int getNextServerIndex() {
        return serverCount.get();
    }

    public void reset() {
        serverCount.set(0);
    }
}
