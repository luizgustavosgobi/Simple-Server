package br.com.luizgustavosgobi.simpleServer.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public record AnnotationDto(
        Annotation annotation,
        AnnotatedElement element
) {
}
