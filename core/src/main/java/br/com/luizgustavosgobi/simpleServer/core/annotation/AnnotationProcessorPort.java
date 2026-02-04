package br.com.luizgustavosgobi.simpleServer.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public interface AnnotationProcessorPort {
    void processAnnotation(Annotation annotation, AnnotatedElement element);
    void processAnnotation(List<AnnotationDto> annotations);
}
