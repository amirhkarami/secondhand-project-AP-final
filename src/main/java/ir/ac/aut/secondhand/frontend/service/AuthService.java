package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.client.ApiException;
import ir.ac.aut.secondhand.frontend.dto.DashboardDto;
import ir.ac.aut.secondhand.frontend.dto.LoginResult;
import ir.ac.aut.secondhand.frontend.dto.request.LoginRequest;
import ir.ac.aut.secondhand.frontend.dto.request.RegisterRequest;
import ir.ac.aut.secondhand.frontend.model.enums.UserType;
import ir.ac.aut.secondhand.frontend.session.UserSession;
import ir.ac.aut.secondhand.frontend.util.JwtUtils;


public final class AuthService {
    private final ApiClient apiClient;

    public AuthService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void register(String username, String password, String fullName, String phoneNumber) {
        RegisterRequest request = new RegisterRequest(username, password, fullName, phoneNumber);
        apiClient.postJsonText("/auth/register", request, false);
    }

    public LoginResult login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        String rawResponse = apiClient.postJsonText("/auth/login", request, false);
        String token = extractToken(rawResponse);
        if (token == null || token.isBlank()) {
            throw new ApiException("Backend did not return a JWT token", 0);
        }
        token = token.replaceFirst("(?i)^Bearer\\s+", "").trim();

        UserType type = JwtUtils.extractUserType(token);
        Integer userId = JwtUtils.extractUserId(token);
        String tokenUsername = JwtUtils.extractUsername(token);
        String effectiveUsername = tokenUsername == null || tokenUsername.isBlank() ? username : tokenUsername;

        UserSession.getInstance().start(token, effectiveUsername, userId, type);
        if (type == UserType.UNKNOWN) {
            type = probeRole();
            UserSession.getInstance().start(token, effectiveUsername, userId, type);
        }
        return new LoginResult(token, effectiveUsername, userId, type);
    }

    public void logout() {
        UserSession.getInstance().clear();
    }

    private String extractToken(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return null;
        }
        String trimmed = rawResponse.trim();
        if (trimmed.startsWith("{")) {
            try {
                JsonNode node = apiClient.getObjectMapper().readTree(trimmed);
                for (String key : new String[]{"token", "jwt", "key", "accessToken"}) {
                    if (node.hasNonNull(key)) {
                        return node.get(key).asText();
                    }
                }
            } catch (Exception ignored) {

            }
        }
        return ApiClient.stripJsonQuotes(trimmed);
    }

    private UserType probeRole() {
        try {
            apiClient.get("/api/admin/dashboard", new TypeReference<DashboardDto>() { }, true);
            return UserType.ADMIN;
        } catch (ApiException exception) {
            if (exception.getStatusCode() == 401) {
                throw exception;
            }
            if (exception.getStatusCode() == 403) {
                return UserType.USER;
            }
            return UserType.USER;
        }
    }
}
