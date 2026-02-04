package br.com.luizgustavosgobi.simpleServer.core.annotation;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotationRegistry {
    void registerProcessor(AnnotationDefinition<?> processor);
    void registerProcessor(AnnotationDefinition<?>... processors);
    void registerProcessor(List<AnnotationDefinition<?>> processors);

    <T extends Annotation> AnnotationDefinition<T> getProcessor(Class<T> annotation);
    AnnotationDefinition<?> getProcessor(Annotation annotation);

    List<AnnotationDefinition<?>> getProcessors();
}
