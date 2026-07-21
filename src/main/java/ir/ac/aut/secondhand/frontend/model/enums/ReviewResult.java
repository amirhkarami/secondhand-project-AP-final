package ir.ac.aut.secondhand.frontend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReviewResult {
    APPROVED,
    REJECTED,
    UNKNOWN;

    @JsonCreator
    public static ReviewResult fromValue(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        try {
            return ReviewResult.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
