package br.com.luizgustavosgobi.simpleServer.logger;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.logger.enums.Colors;
import br.com.luizgustavosgobi.simpleServer.logger.enums.LogTypes;

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

    private final ConfigurationManager config = ConfigurationManager.getInstance();
    private final String applicationName;

    public Logger(Class<?> clazz) {
        this.applicationName = clazz.getName();
    }

    public Logger(String applicationName) {
        this.applicationName = applicationName;

        printLogo();
    }

    public Logger() {
        this("Main");
    }

    //
    // Utils functions
    //

    /**
     * Printa no console o logo do framework.
     * Este método exibe uma arte ASCII representando o SimpleServer.
     */
    public void printLogo() {
        if (hasPrintedLogo) {return;}
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
        if (config.getBoolean("logger.logging", true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            System.out.println("[" + now.format(formatter) + "] " + type + " -- " + "[" + applicationName + "] " + clazz.getSimpleName() + " : " + message);
        }
    }

    //
    // Static Methods
    //

    /**
     * Exibe uma mensagem de erro no console de forma estática.
     *
     * @param caller Classe associadao ao erro.
     * @param message Mensagem que será exibida.
     */
    public static void Error(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.ERROR, caller, message);
    }
    public static void Error(Object caller, String message) {
        Error(caller.getClass(), message);
    }
    public static void Error(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Error(callerClass, message);
    }

    /**
     * Exibe uma mensagem informativa no console de forma estática.
     *
     * @param caller Classe associado à informação.
     * @param message Mensagem que será exibida.
     */
    public static void Info(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.INFO, caller, message);
    }
    public static void Info(Object caller, String message) {
        Info(caller.getClass(), message);
    }
    public static void Info(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Info(callerClass, message);
    }

    /**
     * Exibe uma mensagem de aviso no console de forma estática.
     *
     * @param caller Classe associado ao aviso.
     * @param message Mensagem que será exibida.
     */
    public static void Warn(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.WARN, caller, message);
    }
    public static void Warn(Object caller, String message) {
        Warn(caller.getClass(), message);
    }
    public static void Warn(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Warn(callerClass, message);
    }

    /**
     * Exibe uma mensagem de erro que é comprometedor ao sistema.
     *
     * @param caller Classe associado ao erro.
     * @param message Mensagem que será exibida.
     */
    public static void Fatal(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.FATAL, caller, message);
    }
    public static void Fatal(Object caller, String message) {
        Fatal(caller.getClass(), message);
    }
    public static void Fatal(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Fatal(callerClass, message);
    }

    /**
     * Exibe uma mensagem de sucesso no console.
     *
     * @param caller Classe em que ocorreu o sucesso.
     * @param message Mensagem que será exibida.
     */
    public static void Good(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.GOOD, caller, message);
    }
    public static void Good(Object caller, String message) {
        Good(caller.getClass(), message);
    }
    public static void Good(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Good(callerClass, message);
    }

    /**
     * Exibe uma mensagem de falha no console.
     *
     * @param caller Classe em que ocorreu a falha.
     * @param message Mensagem que será exibida.
     */
    public static void Bad(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.BAD, caller, message);
    }
    public static void Bad(Object caller, String message) {
        Bad(caller.getClass(), message);
    }
    public static void Bad(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Bad(callerClass, message);
    }

    /**
     * Exibe uma mensagem de depuração no console.
     *
     * @param caller Classe em que ocorreu a depuração.
     * @param message Mensagem que será exibida.
     */
    public static void Debug(Class<?> caller, String message) {
        Logger instance = LoggerHolder.getOrCreate();
        instance.log(LogTypes.DEBUG, caller, message);
    }
    public static void Debug(Object caller, String message) {
        Debug(caller.getClass(), message);
    }
    public static void Debug(String message) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Debug(callerClass, message);
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
