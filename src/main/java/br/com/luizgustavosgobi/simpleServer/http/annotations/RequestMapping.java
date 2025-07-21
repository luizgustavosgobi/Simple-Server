package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.http.annotations.ParametersType.PathVariable;
import br.com.luizgustavosgobi.simpleServer.http.annotations.ParametersType.RequestBody;
import br.com.luizgustavosgobi.simpleServer.http.annotations.ParametersType.RequestHeader;
import br.com.luizgustavosgobi.simpleServer.http.annotations.ParametersType.RequestParam;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.InternalServerErrorException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.MalFormedRequestDataException;
import br.com.luizgustavosgobi.simpleServer.http.router.RouteHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;
import br.com.luizgustavosgobi.simpleServer.http.types.BodyData;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String path() default "";
    HttpMethod method() default HttpMethod.GET;

    class Handler implements AnnotationProcessor<RequestMapping> {

        @Override
        public Class<RequestMapping> getAnnotationType() {
            return RequestMapping.class;
        }

        @Override
        public int getPriority() {
            return 4;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {
            RequestMapping requestMapping = (RequestMapping) annotation;

            String path = requestMapping.path();
            HttpMethod httpMethod = requestMapping.method();

            if (path.isEmpty())
                path = "/";

            Method method = (Method) element;
            Router router = applicationContext.get(Router.class);
            router.add(httpMethod, path, createHandler(method, applicationContext));
        }

        public static RouteHandler createHandler(Method method, ApplicationContext applicationContext) {
            return (request) -> {
                ObjectMapper objectMapper = applicationContext.get(ObjectMapper.class);
                Validator validator = applicationContext.get(Validator.class);
                List<Object> args = new LinkedList<>();

                for (Parameter parameter : method.getParameters()) {
                    Object value = null;

                    if (parameter.isAnnotationPresent(RequestBody.class)) {
                        Object body = request.getBody();

                        if  (body instanceof BodyData bodyData) {
                            try {
                                value = objectMapper.treeToValue(bodyData.json(), parameter.getType());
                                Set<ConstraintViolation<Object>> violations = validator.validate(value);
                                if (!violations.isEmpty())
                                    throw new MalFormedRequestDataException();
                            } catch (JacksonException e) {
                                throw new MalFormedRequestDataException();
                            }
                        }
                    }

                    else if (parameter.isAnnotationPresent(PathVariable.class)) {
                        PathVariable pathVar = parameter.getAnnotation(PathVariable.class);
                        String pathVarName = pathVar.value().isEmpty() ? parameter.getName() : pathVar.value();
                        String rawValue = request.getPathVariable(pathVarName);
                        value = convertValue(rawValue, parameter.getType());
                    }

                    else if (parameter.isAnnotationPresent(RequestParam.class)) {
                        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                        String paramName = requestParam.value().isEmpty() ? parameter.getName() : requestParam.value();
                        String rawValue = request.getUri().getQueryParam(paramName);

                        if (rawValue == null && requestParam.required()) {
                            throw new MalFormedRequestDataException();
                        }

                        value = convertValue(rawValue, parameter.getType());
                    }

                    else if (parameter.isAnnotationPresent(RequestHeader.class)) {
                        RequestHeader header = parameter.getAnnotation(RequestHeader.class);
                        String headerName = header.value().isEmpty() ? parameter.getName() : header.value();
                        String rawValue = request.getHeaders().get(headerName);

                        if (rawValue == null && header.required()) {
                            throw new MalFormedRequestDataException();
                        }

                        value = convertValue(rawValue, parameter.getType());
                    }

                    else {
                        Object body = request.getBody();

                        if  (body instanceof BodyData bodyData) {
                            try {
                                value = objectMapper.treeToValue(bodyData.json(), parameter.getType());
                                Set<ConstraintViolation<Object>> violations = validator.validate(value);
                                if (!violations.isEmpty())
                                    throw new MalFormedRequestDataException();
                            } catch (JacksonException e) {
                                throw new MalFormedRequestDataException();
                            }
                        }
                    }

                    args.add(value);
                }

                try {
                    return (ResponseEntity<?>) method.invoke(applicationContext.get(method.getDeclaringClass()), args.toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new InternalServerErrorException();
                }
            };
        }

        private static Object convertValue(String value, Class<?> targetType) {
            if (value == null) return null;

            if (targetType == String.class) {
                return value;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(value);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(value);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(value);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(value);
            }

            throw new MalFormedRequestDataException();
        }
    }
}
