package test;

import br.com.luizgustavosgobi.simpleServer.core.server.Server;
import br.com.luizgustavosgobi.simpleServer.http.HttpApplication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = HttpApplication.run(Main.class);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String data = scanner.nextLine();
            if (data.equals("exit")) {
                server.close();
                break;
            } else if (data.equals("connections")) {
                server.getConnectionTable().getConnections().forEach(System.out::println);
            } else {
                System.out.println("Unknown command: " + data);
            }
        }
    }
}