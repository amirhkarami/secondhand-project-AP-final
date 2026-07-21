package ir.ac.aut.secondhand.frontend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.aut.secondhand.frontend.model.enums.UserType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JwtUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtUtils() {
    }

    public static UserType extractUserType(String token) {
        JsonNode payload = payload(token);
        if (payload == null) {
            return UserType.UNKNOWN;
        }
        for (String field : new String[]{"type", "role", "userType", "authority", "authorities"}) {
            JsonNode node = payload.get(field);
            if (node != null) {
                UserType result = UserType.fromValue(node.toString());
                if (result != UserType.UNKNOWN) {
                    return result;
                }
            }
        }
        return UserType.UNKNOWN;
    }

    public static Integer extractUserId(String token) {
        JsonNode payload = payload(token);
        if (payload == null) {
            return null;
        }
        for (String field : new String[]{"id", "userId", "uid"}) {
            JsonNode node = payload.get(field);
            if (node != null && node.canConvertToInt()) {
                return node.asInt();
            }
        }
        return null;
    }

    public static String extractUsername(String token) {
        JsonNode payload = payload(token);
        if (payload == null) {
            return null;
        }
        for (String field : new String[]{"sub", "username", "user"}) {
            JsonNode node = payload.get(field);
            if (node != null && node.isTextual()) {
                return node.asText();
            }
        }
        return null;
    }

    private static JsonNode payload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            return MAPPER.readTree(new String(decoded, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            return null;
        }
    }
}
