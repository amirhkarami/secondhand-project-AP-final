package ir.ac.aut.secondhand.frontend.util;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static Long parseNullableLong(String value, String fieldName) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer");
        }
    }

    public static long parsePositiveLong(String value, String fieldName) {
        Long result = parseNullableLong(value, fieldName);
        if (result == null || result <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
        return result;
    }

    public static void requireText(String value, String fieldName) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void requirePassword(String password) {
        requireText(password, "Password");
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must contain at least 4 characters");
        }
    }
}
