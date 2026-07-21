package ir.ac.aut.secondhand.frontend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductStatus {
    PENDING,
    ACTIVE,
    DENIED,
    REJECTED,
    DELETED,
    SOLD,
    UNKNOWN;

    @JsonCreator
    public static ProductStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        try {
            return ProductStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }

    public boolean isRejected() {
        return this == DENIED || this == REJECTED;
    }
}
