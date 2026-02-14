package br.com.luizgustavosgobi.simpleServer.http.parser;

import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.InvalidHttpRequestException;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.HttpRequestLine;
import br.com.luizgustavosgobi.simpleServer.http.entities.components.URI;

import java.util.Arrays;
import java.util.List;

public class HttpParser {

    public static RequestEntity<?> parse(String data) throws InvalidHttpRequestException {
        List<String> requestLines = Arrays.asList(data.split("\r\n"));

        HttpRequestLine startLine = parseStartLine(requestLines.getFirst());
        if (startLine == null) { throw new InvalidHttpRequestException(); }

        int i = 1;
        while (i < requestLines.size() && !requestLines.get(i).isEmpty()) { i++; }
        HttpHeaders headers = parseHeaders(requestLines.subList(1, i));

        String contentType = headers.get("Content-Type");
        Object bodyContent =  null;
        if (contentType != null) {
            bodyContent = br.com.luizgustavosgobi.simpleServer.http.parser.HttpContentParser.valueOf(
                    contentType
                            .split("/")[1]
                            .split(";")[0].toUpperCase()
                            .replace("-", "_"))
                    .parse(data.split("\r\n", i + 2)[i + 1], contentType);
        }

        return new RequestEntity<>(startLine, headers, bodyContent);
    }

    private static HttpRequestLine parseStartLine(String startLine) {
        String[] parts = startLine.trim().split(" ");
        if (parts.length != 3) return null;
        return new HttpRequestLine(HttpMethod.parse(parts[0]), new URI(parts[1]), parts[2]);
    }

    private static HttpHeaders parseHeaders(List<String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String part : headers) {
            String[] keyValue = part.split(": ");
            if (keyValue.length != 2) continue;
            httpHeaders.add(keyValue[0], keyValue[1]);
        }

        return httpHeaders;
    }
}