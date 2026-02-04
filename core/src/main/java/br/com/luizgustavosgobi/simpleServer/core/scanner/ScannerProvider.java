package br.com.luizgustavosgobi.simpleServer.core.scanner;

import br.com.luizgustavosgobi.simpleServer.core.annotation.*;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScannerProvider {

    private final AnnotationRegistry annotationRegistry;
    private final BeanRegistry beanRegistry;

    private final ClassPathScanner classPathScanner = new ClassPathScanner();
    private final AnnotationScanner annotationScanner = new AnnotationScanner();
    private final AnnotationProcessorScanner annotationProcessorScanner = new AnnotationProcessorScanner();

    private final List<String> knowPackages = new ArrayList<>(List.of(
            "br.com.luizgustavosgobi.simpleServer"
    ));

    public ScannerProvider() {
        this.annotationRegistry = new AnnotationContext();
        this.beanRegistry = new ApplicationContext();
    }

    public ScannerProvider(BeanRegistry externalBeanRegistry) {
        this.annotationRegistry = new AnnotationContext();
        this.beanRegistry = externalBeanRegistry;
    }

    public ScannerProvider findAnnotatedComponents() {
        List<Class<?>> classes = classPathScanner.scan(knowPackages.toArray());
        List<AnnotationDto> annotations = annotationScanner.scan(classes);
        List<AnnotationDefinition<?>> processor = annotationProcessorScanner.scan(classes);

        annotationRegistry.registerProcessor(processor);

        AnnotationProcessorPort annotationProcessor = new AnnotationProcessor(annotationRegistry, beanRegistry);
        annotationProcessor.processAnnotation(annotations);

        return this;
    }

    public ScannerProvider includePackage(String packageName) {
        if (packageName.isBlank())
            throw new IllegalArgumentException("The package name must not be blank");

        this.knowPackages.add(packageName);

        return this;
    }

    public ScannerProvider includePackage(String... packageNames) {
        this.knowPackages.addAll(Arrays.asList(packageNames));

        return this;
    }

    public List<String> getKnowPackages() {
        return knowPackages;
    }

    public AnnotationRegistry getAnnotationRegistry() {
        return this.annotationRegistry;
    }

    public BeanRegistry getBeanRegistry() {
        return this.beanRegistry;
    }

    public ClassPathScanner getClassPathScanner() {
        return classPathScanner;
    }

    public AnnotationScanner getAnnotationScanner() {
        return annotationScanner;
    }

    public AnnotationProcessorScanner getAnnotationProcessorScanner() {
        return annotationProcessorScanner;
    }
}
