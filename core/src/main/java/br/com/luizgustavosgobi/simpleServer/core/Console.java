package br.com.luizgustavosgobi.simpleServer.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class Console {
    public static Console INSTANCE;

    private final HashMap<String, Server> servers = new HashMap<>();

    public Console() {
        INSTANCE = this;
    }

    public void addServer(String name, Server server) {
        String serverName = name + "-" + UUID.randomUUID();
        servers.put(serverName, server);
    }

    public void loop() {
        Scanner sc = new Scanner(System.in);

        while (servers.values().stream().anyMatch(server -> server.getServerChannel().isOpen())) {
            String line = sc.nextLine();

            switch (line) {
                case "close" -> servers.values().forEach(server -> {
                    try {
                        server.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                case "connections" -> servers.forEach((name, server) -> {
                    System.out.println("Server: " + name);
                    System.out.println(server.getConnectionTable().toString());
                });
                case "servers" -> servers.values().forEach(server -> {
                    System.out.println(server.getServerChannel().socket().getInetAddress());
                });
                default -> System.out.println("Unknown command: " + line);
            }

        }

        sc.close();
    }


    public static Console getInstance() {
        if (INSTANCE == null) return new Console();
        return INSTANCE;
    }
}
