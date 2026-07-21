package ir.ac.aut.secondhand.frontend.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class QueryBuilder {
    private final String path;
    private final List<String> parameters = new ArrayList<>();

    public QueryBuilder(String path) {
        this.path = path;
    }

    public QueryBuilder add(String key, Object value) {
        if (value != null && !value.toString().isBlank()) {
            parameters.add(encode(key) + "=" + encode(value.toString()));
        }
        return this;
    }

    public String build() {
        return parameters.isEmpty() ? path : path + "?" + String.join("&", parameters);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
