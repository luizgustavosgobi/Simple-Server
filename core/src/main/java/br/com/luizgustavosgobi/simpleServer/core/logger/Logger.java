package br.com.luizgustavosgobi.simpleServer.core.logger;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.logger.enums.Colors;
import br.com.luizgustavosgobi.simpleServer.core.logger.enums.LogTypes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe responsável por realizar todos os logs relacionados ao SimpleServer.
 * Esta classe implementa um Logger que pode ser usado de forma estática ou por instância.
 * Ele suporta diferentes níveis de log (erro, aviso, informação, etc.) e permite associar
 * logs a classes específicas. Além disso, utiliza um padrão Singleton contextual para
 * gerenciar instâncias por ClassLoader.
 */
public class Logger {
    private static boolean hasPrintedLogo = false;

    private final ConfigurationManager configurationManager = ConfigurationManager.getOrCreate();
    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private final String applicationName;

    //
    // Constructors
    //

    public Logger(String applicationName) {
        this.applicationName = applicationName;
        if (!hasPrintedLogo) printLogo();
    }

    public Logger(Class<?> clazz) {
        this(clazz.getName());
    }

    public Logger() {
        this("Main");
    }

    //
    // Utils functions
    //

    public void printLogo() {
        String logo = """
                ######################################################################################\s
                ##        _____ _                 _         _____                                   ##\s
                ##       / ____(_)               | |       / ____|                                  ##\s
                ##      | (___  _ _ __ ___  _ __ | | ___  | (___   ___ _ ____   _____ _ __          ##\s
                ##       \\___ \\| | '_ ` _ \\| '_ \\| |/ _ \\  \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|         ##\s
                ##       ____) | | | | | | | |_) | |  __/  ____) |  __/ |   \\ V /  __/ |            ##\s
                ##      |_____/|_|_| |_| |_| .__/|_|\\___| |_____/ \\___|_|    \\_/ \\___|_|            ##\s
                ##                         | |                                                      ##\s
                ##                         |_|                                                      ##\s
                ######################################################################################\s
                
                :: Simple Server :: (v1.0)
                """;

        System.out.println(Colors.GREEN_BOLD_BRIGHT + logo + Colors.RESET);
        hasPrintedLogo = true;
    }

    //
    // Logger functions
    //

    /**
     * Método auxiliar para exibir mensagens no console.
     *
     * @param type Tipo de mensagem (e.g., ERROR, INFO).
     * @param clazz Classe associada à mensagem.
     * @param message Mensagem que será exibida.
     */
    private void log(LogTypes type, Class<?> clazz, String message) {
        if (configurationManager.getBoolean("logger.logging", true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            System.out.println("[" + now.format(formatter) + "] " + type + " -- " + "[" + applicationName + "] " + clazz.getSimpleName() + " : " + message);
        }
    }

    //
    // Static Methods
    //

    public static void Error(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.ERROR, caller, message);
    }
    public static void Error(Object caller, String message) {
        Error(caller.getClass(), message);
    }
    public static void Error(String message) {
        Error(walker.getCallerClass(), message);
    }
    public void error(String message) {
        Error(walker.getCallerClass(), message);
    }

    public static void Info(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.INFO, caller, message);
    }
    public static void Info(Object caller, String message) {
        Info(caller.getClass(), message);
    }
    public static void Info(String message) {
        Info(walker.getCallerClass(), message);
    }
    public void info(String message) {
        Info(walker.getCallerClass(), message);
    }

    public static void Warn(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.WARN, caller, message);
    }
    public static void Warn(Object caller, String message) {
        Warn(caller.getClass(), message);
    }
    public static void Warn(String message) {
        Warn(walker.getCallerClass(), message);
    }
    public void warn(String message) {
        Warn(walker.getCallerClass(), message);
    }

    public static void Fatal(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.FATAL, caller, message);
    }
    public static void Fatal(Object caller, String message) {
        Fatal(caller.getClass(), message);
    }
    public static void Fatal(String message) {
        Fatal(walker.getCallerClass(), message);
    }
    public void fatal(String message) {
        Fatal(walker.getCallerClass(), message);
    }

    public static void Good(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.GOOD, caller, message);
    }
    public static void Good(Object caller, String message) {
        Good(caller.getClass(), message);
    }
    public static void Good(String message) {
        Good(walker.getCallerClass(), message);
    }
    public void good(String message) {
        Good(walker.getCallerClass(), message);
    }

    public static void Bad(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.BAD, caller, message);
    }
    public static void Bad(Object caller, String message) {
        Bad(caller.getClass(), message);
    }
    public static void Bad(String message) {
        Bad(walker.getCallerClass(), message);
    }
    public void bad(String message) {
        Bad(walker.getCallerClass(), message);
    }

    public static void Debug(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.DEBUG, caller, message);
    }
    public static void Debug(Object caller, String message) {
        Debug(caller.getClass(), message);
    }
    public static void Debug(String message) {
        Debug(walker.getCallerClass(), message);
    }
    public void debug(String message) {
        Debug(walker.getCallerClass(), message);
    }


    public static class LoggerHolder {
        private static final InheritableThreadLocal<Logger> INSTANCE = new InheritableThreadLocal<>();

        public static void setInstance(Logger logger) {
            INSTANCE.set(logger);
        }

        public static void removeInstance() {
            INSTANCE.remove();
        }

        public static Logger getOrCreate() {
            Logger logger = INSTANCE.get();
            if (logger == null) {
                logger = new Logger();
                setInstance(logger);
            }

            return logger;
        }
    }

}
