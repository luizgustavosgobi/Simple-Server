package br.com.luizgustavosgobi.simpleServer.http.entities.builders;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;

public interface HeaderBuilder <C extends HeaderBuilder<C>> {

    C status(HttpStatus status);

    C accept(String... acceptableMediaTypes);

    C acceptCharset(Charset... acceptableCharsets);

    C contentLength(long contentLength);

    C contentType(String contentType);

    C ifModifiedSince(ZonedDateTime ifModifiedSince);

    C ifModifiedSince(Instant ifModifiedSince);

    C ifModifiedSince(long ifModifiedSince);

    C ifNoneMatch(String... ifNoneMatches);

    C version(String version);
}
