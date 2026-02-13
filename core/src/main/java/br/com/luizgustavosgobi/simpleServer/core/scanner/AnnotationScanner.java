package br.com.luizgustavosgobi.simpleServer.core.scanner;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationScanner {

    public List<AnnotationDto> scan(List<Class<?>> classes) {
        List<AnnotationDto> annotations = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotation())
                continue;

            for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                annotations.add(new AnnotationDto(annotation, clazz));
            }

            for (Field field : clazz.getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    annotations.add(new AnnotationDto(annotation, field));
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    annotations.add(new AnnotationDto(annotation, method));
                }
            }
        }

        return annotations;
    }
}
