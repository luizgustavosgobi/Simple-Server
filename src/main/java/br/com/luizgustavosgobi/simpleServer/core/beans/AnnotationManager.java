package br.com.luizgustavosgobi.simpleServer.core.beans;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.beans.scanner.AnnotationProcessorScanner;
import br.com.luizgustavosgobi.simpleServer.core.beans.scanner.ClassScanner;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationManager {
    private final Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors = new ConcurrentHashMap<>();
    private final ClassScanner scanner = new ClassScanner();
    private final ApplicationContext applicationContext = ContextHolder.getContext();

    // Cache para otimização
    private final Map<String, List<Class<?>>> packageClassCache = new ConcurrentHashMap<>();

    //
    // Register Processors
    //

    public AnnotationManager registerProcessor(AnnotationProcessor<?> processor) {
        processors.put(processor.getAnnotationType(), processor);
        return this;
    }

    public AnnotationManager registerProcessor(AnnotationProcessor<?>... processorArray) {
        for (AnnotationProcessor<?> annotationProcessor : processorArray) {
            registerProcessor(annotationProcessor);
        }
        return this;
    }

    /**
     * Auto-descobre e registra todos os processadores de anotação encontrados no classpath
     * Isso inclui processadores do seu projeto e de dependências externas
     */
    public AnnotationManager autoDiscoverAndRegisterProcessors() {
        AnnotationProcessorScanner processorScanner = new AnnotationProcessorScanner();
        List<Class<? extends AnnotationProcessor<?>>> processorClasses = processorScanner.findAllAnnotationProcessors();

        for (Class<? extends AnnotationProcessor<?>> processorClass : processorClasses) {
            try {
                AnnotationProcessor<?> processor = processorClass.getDeclaredConstructor().newInstance();
                registerProcessor(processor);
            } catch (Exception e) {
                Logger.Error(this, "Error to register annotation processor " + processorClass.getName() + ": " + e.getMessage());
            }
        }

        return this;
    }

    //
    // Scan Functions
    //

    public void scanAndProcess(String basePackage) {
        try {
            List<Class<?>> classes = getClassesFromPackage(basePackage);
            List<ClassAnnotationInfo> classesWithAnnotations = classes.parallelStream()
                    .map(this::analyzeClassForRegisteredAnnotations)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(info -> info.minPriority))
                    .toList();

            for (ClassAnnotationInfo classInfo : classesWithAnnotations) {
                processAllAnnotations(classInfo.clazz);
            }

        } catch (Exception e) {
            Logger.Error(this, "Error during scan: " + e.getMessage());
            throw new RuntimeException("Failed to scan and process annotations", e);
        }
    }


    private List<Class<?>> getClassesFromPackage(String basePackage) {
        return packageClassCache.computeIfAbsent(basePackage, pkg -> {
            try {
                return scanner.findClassesInPackage(pkg);
            } catch (Exception e) {
                Logger.Error(this, "Error finding classes in package " + pkg + ": " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    private ClassAnnotationInfo analyzeClassForRegisteredAnnotations(Class<?> clazz) {
        List<AnnotationProcessorEntry> entries = new ArrayList<>();
        int minPriority = Integer.MAX_VALUE;

        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (processors.containsKey(annotation.annotationType())) {
                AnnotationProcessor<?> processor = processors.get(annotation.annotationType());
                entries.add(new AnnotationProcessorEntry(processor, annotation));
                minPriority = Math.min(minPriority, processor.getPriority());
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (processors.containsKey(annotation.annotationType())) {
                    AnnotationProcessor<?> processor = processors.get(annotation.annotationType());
                    entries.add(new AnnotationProcessorEntry(processor, annotation));
                    minPriority = Math.min(minPriority, processor.getPriority());
                }
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (processors.containsKey(annotation.annotationType())) {
                    AnnotationProcessor<?> processor = processors.get(annotation.annotationType());
                    entries.add(new AnnotationProcessorEntry(processor, annotation));
                    minPriority = Math.min(minPriority, processor.getPriority());
                }
            }
        }

        return entries.isEmpty() ? null : new ClassAnnotationInfo(clazz, entries, minPriority);
    }

    // Process annotations

    public void recursiveProcessAnnotation(Annotation annotation, AnnotatedElement annotatedElement) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        for (Annotation child : annotationType.getDeclaredAnnotations()) {
            String childPackage = child.annotationType().getPackage().getName();
            if (!childPackage.startsWith("java.") && !childPackage.startsWith("javax."))
                recursiveProcessAnnotation(child, annotatedElement);
        }

        processAnnotation(annotatedElement, annotation);
    }

    private void processAnnotation(AnnotatedElement element, Annotation annotation) {
        AnnotationProcessor<?> processor = processors.get(annotation.annotationType());
        if (processor == null) { return; }

        try {
            processor.process(element, annotation, applicationContext);
        } catch (Exception e) {
            Logger.Error(this, "Error while processing annotation " + processor.getAnnotationType().getName() + ": " + e.getMessage());
        }
    }

    private void processAllAnnotations(Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            recursiveProcessAnnotation(annotation, clazz);
        }

        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                recursiveProcessAnnotation(annotation, field);
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                recursiveProcessAnnotation(annotation, method);
            }
        }
    }

    // Auxiliary classes

    private static class ClassAnnotationInfo {
        final Class<?> clazz;
        final List<AnnotationProcessorEntry> entries;
        final int minPriority;

        ClassAnnotationInfo(Class<?> clazz, List<AnnotationProcessorEntry> entries, int minPriority) {
            this.clazz = clazz;
            this.entries = entries;
            this.minPriority = minPriority;
        }
    }

    private static class AnnotationProcessorEntry {
        final AnnotationProcessor<?> processor;
        final Annotation annotation;

        AnnotationProcessorEntry(AnnotationProcessor<?> processor, Annotation annotation) {
            this.processor = processor;
            this.annotation = annotation;
        }
    }
}
