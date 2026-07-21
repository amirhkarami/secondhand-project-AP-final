package ir.ac.aut.secondhand.frontend.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ir.ac.aut.secondhand.frontend.config.AppConfig;
import ir.ac.aut.secondhand.frontend.dto.ApiError;
import ir.ac.aut.secondhand.frontend.session.UserSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public final class ApiClient {
    private final AppConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient(AppConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.getConnectTimeout())
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T get(String path, TypeReference<T> type, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .GET()
                .build();
        return sendAndRead(request, type);
    }

    public <T> T getOptionalAuth(String path, TypeReference<T> type) {
        HttpRequest request = requestBuilderOptionalAuth(path)
                .GET()
                .build();
        return sendAndRead(request, type);
    }

    public String getText(String path, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .GET()
                .build();
        return sendAndReadText(request);
    }



    public String postEmpty(String path, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return sendAndReadText(request);
    }

    public <T> T postJson(String path, Object body, TypeReference<T> type, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return sendAndRead(request, type);
    }

    public String postJsonText(String path, Object body, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return sendAndReadText(request);
    }

    public <T> T putJson(String path, Object body, TypeReference<T> type, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return sendAndRead(request, type);
    }

    public String putJsonText(String path, Object body, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return sendAndReadText(request);
    }

    public String putEmpty(String path, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return sendAndReadText(request);
    }

    public String delete(String path, boolean authenticated) {
        HttpRequest request = requestBuilder(path, authenticated)
                .DELETE()
                .build();
        return sendAndReadText(request);
    }

    public String sendMultipart(String method, String path, MultipartBody multipartBody,
                                boolean authenticated) {
        byte[] content = multipartBody.build();
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofByteArray(content);
        HttpRequest.Builder builder = requestBuilder(path, authenticated)
                .header("Content-Type", multipartBody.getContentType());

        HttpRequest request = switch (method.toUpperCase()) {
            case "POST" -> builder.POST(publisher).build();
            case "PUT" -> builder.PUT(publisher).build();
            default -> builder.method(method.toUpperCase(), publisher).build();
        };
        return sendAndReadText(request);
    }

    private HttpRequest.Builder requestBuilder(String path, boolean authenticated) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(resolve(path))
                .timeout(config.getRequestTimeout())
                .header("Accept", "application/json");

        if (authenticated) {
            String token = UserSession.getInstance().token().orElseThrow(() -> new ApiException("You must sign in first", 401));
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    private HttpRequest.Builder requestBuilderOptionalAuth(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(resolve(path))
                .timeout(config.getRequestTimeout())
                .header("Accept", "application/json");

        UserSession.getInstance().token().ifPresent(token ->
                builder.header("Authorization", "Bearer " + token));
        return builder;
    }

    private URI resolve(String path) {
        String normalized = path.startsWith("/") ? path : "/" + path;
        return URI.create(config.getBaseUrl() + normalized);
    }

    private <T> T sendAndRead(HttpRequest request, TypeReference<T> type) {
        String responseBody = sendAndReadText(request);
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(responseBody, type);
        } catch (JsonProcessingException exception) {
            throw new ApiException("Backend response could not be parsed", 0, exception);
        }
    }

    private String sendAndReadText(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }
            throw parseApiException(response.statusCode(), response.body());
        } catch (IOException exception) {
            throw new ApiException("Could not connect to backend. Check server address and status.", 0, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request was interrupted", 0, exception);
        }
    }

    private ApiException parseApiException(int httpStatus, String body) {
        if (body != null && !body.isBlank()) {
            try {
                ApiError apiError = objectMapper.readValue(body, ApiError.class);
                int effectiveStatus = apiError.getStatus() == 0 ? httpStatus : apiError.getStatus();
                String message = apiError.getMessage();
                if (message != null && !message.isBlank()) {
                    return new ApiException(message, effectiveStatus);
                }
            } catch (JsonProcessingException ignored) {
                try {
                    JsonNode node = objectMapper.readTree(body);
                    String message = node.hasNonNull("error") ? node.get("error").asText() : null;
                    if (message != null && !message.isBlank()) {
                        return new ApiException(message, httpStatus);
                    }
                } catch (JsonProcessingException ignoredAgain) {
                    // Fall through to plain-text error.
                }
            }
            return new ApiException(stripJsonQuotes(body), httpStatus);
        }
        return new ApiException("Backend returned HTTP " + httpStatus, httpStatus);
    }

    private String toJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body == null ? Map.of() : body);
        } catch (JsonProcessingException exception) {
            throw new ApiException("Request data could not be serialized", 0, exception);
        }
    }

    public static String stripJsonQuotes(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n");
        }
        return trimmed;
    }
}
