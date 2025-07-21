package br.com.luizgustavosgobi.simpleServer.http.parser.handlers;

import br.com.luizgustavosgobi.simpleServer.http.types.File;
import br.com.luizgustavosgobi.simpleServer.http.types.FormData;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultipartFormDataHandler {

    public static FormData parse(String body, String contentType) {
        Map<String, Object> result = new HashMap<>();

        String boundary = contentType.split("boundary=")[1];
        String[] parts = body.split("--" + boundary);

        for (String part : parts) {
            if (part.isEmpty() || part.equals("--\r\n")) continue;

            String[] headersAndBody = part.split("\r\n\r\n", 2);
            if (headersAndBody.length < 2) continue;

            String headers = headersAndBody[0];
            String content = headersAndBody[1].replace("\r\n", "");

            Map<String, String> headerMap = parseHeaders(headers);
            String disposition = headerMap.get("Content-Disposition");
            if (disposition == null) continue;

            String name = disposition.split("name=")[1].split(";")[0].replace("\"", "");
            if (headerMap.containsKey("Content-Type")) {
                String filename = disposition.split("filename=")[1].replace("\"", "");
                result.put(name, new File(filename, content.getBytes(StandardCharsets.UTF_8), headerMap.get("Content-Type")));
            } else {
                result.put(name, content);
            }
        }

        return new FormData(result);
    }

    private static Map<String, String> parseHeaders(String headers) {
        Map<String, String> headerMap = new HashMap<>();
        String[] lines = headers.split("\r\n");
        for (String line : lines) {
            String[] keyValue = line.split(": ", 2);
            if (keyValue.length == 2) {
                headerMap.put(keyValue[0], keyValue[1]);
            }
        }
        return headerMap;
    }
}
