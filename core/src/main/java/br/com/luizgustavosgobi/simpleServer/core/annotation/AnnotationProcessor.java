package br.com.luizgustavosgobi.simpleServer.core.annotation;

import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationProcessor implements AnnotationProcessorPort {
    private final Map<Annotation, AnnotationDefinition<?>> processors = new ConcurrentHashMap<>();

    private final AnnotationRegistry annotationRegistry;
    private final BeanRegistry beanRegistry;

    public AnnotationProcessor(AnnotationRegistry annotationRegistry, BeanRegistry beanRegistry) {
        this.annotationRegistry = annotationRegistry;
        this.beanRegistry = beanRegistry;
    }


    public void processAnnotation(Annotation annotation, AnnotatedElement element) {
        Class<? extends Annotation> annotationType = annotation.annotationType();

        for (Annotation child : annotationType.getDeclaredAnnotations()) {
            String childPackage = child.annotationType().getPackage().getName();

            if (!childPackage.startsWith("java.") && !childPackage.startsWith("javax."))
                processAnnotation(child, element);
        }

        if (element instanceof Class<?> clazz)
            Logger.Debug(clazz.getName());

        AnnotationDefinition<?> processor = processors.getOrDefault(
                annotation,
                annotationRegistry.getProcessor(annotation)
        );

        if (processor == null) return;

        try {
            processor.process(element, annotation, beanRegistry);
        } catch (Exception e) {
            Logger.Error(this, "Error while processing annotation " + processor.getAnnotationType().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void processAnnotation(List<AnnotationDto> annotations) {
        Map<Annotation, List<AnnotationDto>> annotationListMap = new HashMap<>();

        for (AnnotationDto entry : annotations) {
            processors.computeIfAbsent(entry.annotation(), annotationRegistry::getProcessor);
            annotationListMap.computeIfAbsent(entry.annotation(), k -> new ArrayList<>()).add(entry);
        }

        List<Map.Entry<Annotation, AnnotationDefinition<?>>> sortedProcessors = processors.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getPriority()))
                .toList();

        for (Map.Entry<Annotation, AnnotationDefinition<?>> processorEntry : sortedProcessors) {
            Annotation annotationClass = processorEntry.getKey();

            List<AnnotationDto> entries = annotationListMap.get(annotationClass);
            for (AnnotationDto annEntry : entries) {
                processAnnotation(annEntry.annotation(), annEntry.element());
            }
        }
    }
}
