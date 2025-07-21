package br.com.luizgustavosgobi.simpleServer.core.beans.processor;

import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Interface para processadores de anotações personalizadas.
 *
 * @param <T> O tipo de anotação que este processador manipula.
 */
public interface AnnotationProcessor<T extends Annotation> {

    /**
     * Obtém o tipo da anotação que este processador manipula.
     *
     * @return A classe do tipo de anotação.
     */
    Class<T> getAnnotationType();

    /**
     * Obtém o tipo da anotação que este processador manipula.
     * 5  - Baixa prioridade: Processa depois
     * 0 - Máxima prioridade: Processa primeiro
     *
     * @return A classe do tipo de anotação.
     */
    int getPriority();

    /**
     * Processa um elemento anotado com a anotação especificada.
     *
     * @param element O elemento anotado (por exemplo, uma classe, método ou campo).
     * @param annotation A instância da anotação a ser processada.
     * @param applicationContext O contexto da aplicação onde o processamento ocorre.
     * @throws Exception Se ocorrer algum erro durante o processamento.
     */
    void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception;
}