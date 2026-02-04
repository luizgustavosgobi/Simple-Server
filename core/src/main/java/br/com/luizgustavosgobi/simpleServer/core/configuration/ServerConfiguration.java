package br.com.luizgustavosgobi.simpleServer.core.configuration;

public class ServerConfiguration {
    private final int port;
    private final boolean blocking;
    private final Class<?> mainClass;

    private ServerConfiguration(int port, boolean blocking, Class<?> mainClass) {
        this.port = port;
        this.blocking = blocking;
        this.mainClass = mainClass;
    }

    public static ServerConfiguration resolve(Class<?> mainClass, Integer port, int serverIndex, ConfigurationManager configManager) {
        int resolvedPort = port != null
            ? port
            : configManager.getInt(
                "server." + (serverIndex == 0 ? "" : (serverIndex + 1) + ".") + "port",
                3000
            );

        boolean blocking = configManager.getBoolean("server.blocking", false);

        return new ServerConfiguration(resolvedPort, blocking, mainClass);
    }

    public static ServerConfiguration of(Class<?> mainClass, int port, boolean blocking) {
        return new ServerConfiguration(port, blocking, mainClass);
    }

    public int getPort() {
        return port;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "port=" + port +
                ", blocking=" + blocking +
                ", mainClass=" + mainClass.getSimpleName() +
                '}';
    }
}
