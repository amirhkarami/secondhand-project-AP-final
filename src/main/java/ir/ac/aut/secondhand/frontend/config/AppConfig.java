package ir.ac.aut.secondhand.frontend.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class AppConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final String baseUrl;
    private final Duration connectTimeout;
    private final Duration requestTimeout;

    public AppConfig() {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getResourceAsStream("/frontend.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {

        }

        String environmentBaseUrl = System.getenv("BACKEND_URL");
        String configuredBaseUrl = environmentBaseUrl == null || environmentBaseUrl.isBlank()
                ? properties.getProperty("backend.base-url", DEFAULT_BASE_URL)
                : environmentBaseUrl;

        this.baseUrl = stripTrailingSlash(configuredBaseUrl.trim());
        this.connectTimeout = Duration.ofSeconds(parsePositiveInt(
                properties.getProperty("http.connect-timeout-seconds"), 10));
        this.requestTimeout = Duration.ofSeconds(parsePositiveInt(
                properties.getProperty("http.request-timeout-seconds"), 30));
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    private static int parsePositiveInt(String value, int fallback) {
        try {
            int result = Integer.parseInt(value);
            return result > 0 ? result : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static String stripTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
