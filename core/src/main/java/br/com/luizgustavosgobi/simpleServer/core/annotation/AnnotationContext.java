package br.com.luizgustavosgobi.simpleServer.core.annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationContext implements AnnotationRegistry {
    private final Map<Class<? extends Annotation>, AnnotationDefinition<?>> processors = new ConcurrentHashMap<>();

    @Override
    public void registerProcessor(AnnotationDefinition<?> processor) {
        if (processor == null)
            throw new IllegalArgumentException("Annotation Processor cannot be null!");

        processors.put(processor.getAnnotationType(), processor);
        //Logger.Debug("Registered processor " + processor.getAnnotationType());
    }

    @Override
    public void registerProcessor(AnnotationDefinition<?>... processors) {
        for (AnnotationDefinition<?> annotationDefinition : processors) {
            registerProcessor(annotationDefinition);
        }
    }

    @Override
    public void registerProcessor(List<AnnotationDefinition<?>> processors) {
        for (AnnotationDefinition<?> annotationDefinition : processors) {
            registerProcessor(annotationDefinition);
        }
    }

    @Override
    public <T extends Annotation> AnnotationDefinition<T> getProcessor(Class<T> annotation) {
        AnnotationDefinition<?> processor = processors.get(annotation);
        if (processor == null)
            return null;

        if (!processor.getAnnotationType().equals(annotation))
            throw new IllegalStateException("Processor incompatível");

        @SuppressWarnings("unchecked")
        AnnotationDefinition<T> castedProcessor = (AnnotationDefinition<T>) processor;
        return castedProcessor;
    }

    @Override
    public AnnotationDefinition<?> getProcessor(Annotation annotation) {
        return getProcessor(annotation.annotationType());
    }

    @Override
    public List<AnnotationDefinition<?>> getProcessors() {
        return processors.values().stream().toList();
    }
}
