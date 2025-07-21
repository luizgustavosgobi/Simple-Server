package br.com.luizgustavosgobi.simpleServer.core.context;

public class ContextHolder {
    private static final InheritableThreadLocal<ApplicationContext> INSTANCE = new InheritableThreadLocal<>();

    public static void setInstance(ApplicationContext applicationContext) {
        INSTANCE.set(applicationContext);
    }

    public static void removeInstance() {
        INSTANCE.remove();
    }

    public static ApplicationContext create() {
        ApplicationContext applicationContext = new ApplicationContext();
        setInstance(applicationContext);

        return applicationContext;
    }

    public static ApplicationContext getOrCreate() {
        ApplicationContext applicationContext = INSTANCE.get();
        if (applicationContext == null) return create();
        return applicationContext;
    }

    public static ApplicationContext getContext() {
        return INSTANCE.get();
    }
}