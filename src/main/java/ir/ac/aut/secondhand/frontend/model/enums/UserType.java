package ir.ac.aut.secondhand.frontend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserType {
    ADMIN,
    USER,
    UNKNOWN;

    @JsonCreator
    public static UserType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        String normalized = value.trim().toUpperCase();
        if (normalized.contains("ADMIN")) {
            return ADMIN;
        }
        if (normalized.contains("USER")) {
            return USER;
        }
        return UNKNOWN;
    }
}
