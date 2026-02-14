package br.com.luizgustavosgobi.simpleServer.core.context;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationContext;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationRegistry;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.core.scanner.ScannerProvider;

public class ApplicationContextBootstrap {
    private final Logger logger;
    private final ScannerProvider scannerProvider;
    private final BeanRegistry beanRegistry;
    private final AnnotationRegistry annotationRegistry;

    public ApplicationContextBootstrap(Class<?> mainClass, Logger logger, BeanRegistry externalBeanRegistry) {
        this.logger = logger;
        this.beanRegistry = externalBeanRegistry;

        this.scannerProvider = new ScannerProvider(this.beanRegistry)
                .includePackage(mainClass.getPackageName())
                .findAnnotatedComponents();

        this.annotationRegistry = scannerProvider.getAnnotationRegistry();

        registerCoreBeans();
    }

    public ApplicationContextBootstrap(Class<?> mainClass, Logger logger) {
        this(mainClass, logger, new ApplicationContext());
    }

    private void registerCoreBeans() {
        beanRegistry.register(
            new BeanDefinition("LOGGER", Logger.class, BeanScope.SINGLETON, logger)
        );

        beanRegistry.register(
            new BeanDefinition("ANNOTATION_CONTEXT", AnnotationContext.class, BeanScope.SINGLETON, annotationRegistry)
        );
    }


    public BeanRegistry getBeanRegistry() {
        return beanRegistry;
    }

    public AnnotationRegistry getAnnotationRegistry() {
        return annotationRegistry;
    }

    public ScannerProvider getScannerProvider() {
        return scannerProvider;
    }
}
