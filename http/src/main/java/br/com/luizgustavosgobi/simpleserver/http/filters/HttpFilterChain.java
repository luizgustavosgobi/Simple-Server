package br.com.luizgustavosgobi.simpleServer.http.filters;

import br.com.luizgustavosgobi.simpleServer.core.filters.Filter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;

import java.util.List;
import java.util.regex.Pattern;

public class HttpFilterChain extends FilterChain {
    private final Pattern urlPattern;

    public HttpFilterChain(String urlPattern, List<Filter> filters) {
        super(filters);
        this.urlPattern = Pattern.compile(urlPattern);
    }

    public boolean matches(String url) {
        return urlPattern.matcher(url).matches();
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }
}

