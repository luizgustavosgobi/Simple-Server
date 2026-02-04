package br.com.luizgustavosgobi.simpleServer.core.context;

public enum BeanScope {
    /** Shared on the entire application */
    SINGLETON,

    /** New instance on each request */
    PROTOTYPE,

    /** New instance on each socket request */
    REQUEST,

    /** One bean for the entire user session */
    SESSION
}