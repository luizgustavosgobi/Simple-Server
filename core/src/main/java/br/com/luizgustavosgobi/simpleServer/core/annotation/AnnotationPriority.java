package br.com.luizgustavosgobi.simpleServer.core.annotation;

public enum AnnotationPriority {
    /** Configurações iniciais */
    INITIALIZATION(0),

    /** Escaneamento de componentes */
    COMPONENT_SCAN(100),

    /** Criação de beans */
    BEAN_CREATION(200),

    /** Injeção de dependências */
    DEPENDENCY_INJECTION(300),

    /** Métodos @PreConstruct */
    PRE_CONSTRUCT(400),

    /** Métodos @PostConstruct */
    POST_CONSTRUCT(500),

    /** Configurações finais */
    CONFIGURATION(600),

    /** Validações */
    VALIDATION(700),

    /** Aplicação pronta */
    READY(1000);

    private final int priority;

    AnnotationPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
