package br.com.luizgustavosgobi.simpleServer.logger.enums;

public enum LogTypes {
    DEBUG(Colors.MAGENTA_BOLD_BRIGHT + "DEBUG"),
    WARN(Colors.YELLOW_BOLD_BRIGHT + "WARN"),
    ERROR(Colors.RED_BOLD_BRIGHT + "ERROR"),
    INFO(Colors.GREEN_BOLD_BRIGHT + "INFO"),
    FATAL(Colors.RED_BACKGROUND + "FATAL"),
    GOOD(Colors.GREEN_BOLD_BRIGHT + "(+)"),
    BAD(Colors.RED_BOLD_BRIGHT + "(-)");

    private final String type;
    LogTypes(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + Colors.RESET;
    }
}
