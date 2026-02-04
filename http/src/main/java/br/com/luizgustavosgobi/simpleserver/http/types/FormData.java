package br.com.luizgustavosgobi.simpleServer.http.types;

import br.com.luizgustavosgobi.simpleServer.http.exceptions.TypeMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.UUID;

public record FormData(String boundary, Map<String, Object> values) implements br.com.luizgustavosgobi.simpleServer.http.types.BodyData {

    public FormData(Map<String, Object> values) {
        this(generateBoundary(), values);
    }

    private static String generateBoundary() {
        return "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
    }

    public <T> T getValue(String key, Class<T> type) {
        if (values == null) return null;
        if (values.containsKey(key)) {
            return type.cast(values.get(key));
        }
        return null;
    }

    public File getFile(String key) {
        if (values == null) return null;
        if (values.containsKey(key)) {
            Object obj = values.get(key);
            if (!(obj instanceof File)) throw new TypeMismatchException();
            return (File) obj;
        }
        return null;
    }

    public String getString(String key) {
        if (values == null) return null;
        if (values.containsKey(key)) {
            Object obj = values.get(key);
            if (!(obj instanceof String)) {
                throw new TypeMismatchException();
            }
            return (String) obj;
        }
        return null;
    }

    @Override
    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public String makeBody() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            result.append("--").append(boundary).append("\r\n");
            result.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"");
            if (entry.getValue() instanceof File f) {
                result.append("; filename=\"").append(f.fileName()).append("\"\r\n");
                result.append("Content-Type: ").append(f.contentType());
            }
            result.append("\r\n\r\n");
            result.append(entry.getValue().toString());
            result.append("\r\n");
        }

        result.append("--").append(boundary).append("--").append("\r\n");
        return result.toString();
    }

    @Override
    public JsonNode json() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof File file) {
                    ObjectNode fileNode = mapper.createObjectNode();
                    fileNode.put("fileName", file.fileName());
                    fileNode.put("contentType", file.contentType());
                    fileNode.put("isFile", true);
                    rootNode.set(key, fileNode);
                } else {
                    switch (value) {
                        case String s -> rootNode.put(key, s);
                        case Number number -> {
                            switch (value) {
                                case Integer i -> rootNode.put(key, i);
                                case Long l -> rootNode.put(key, l);
                                case Double d -> rootNode.put(key, d);
                                default -> rootNode.put(key, value.toString());
                            }
                        }
                        case Boolean b -> rootNode.put(key, b);
                        default -> rootNode.put(key, value.toString());
                    }
                }
            }
        }

        return rootNode;
    }

    @Override
    public String toString() {
        return makeBody();
    }
}

