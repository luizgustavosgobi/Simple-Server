package br.com.luizgustavosgobi.simpleServer.core.annotation;

import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Interface para processadores de anotações personalizadas.
 *
 * @param <T> O tipo de anotação que este processador manipula.
 */
public interface AnnotationDefinition<T extends Annotation> {
    Class<T> getAnnotationType();

    AnnotationPriority getPriority();

    void process(AnnotatedElement element, Annotation annotation, BeanRegistry registry) throws Exception;
}