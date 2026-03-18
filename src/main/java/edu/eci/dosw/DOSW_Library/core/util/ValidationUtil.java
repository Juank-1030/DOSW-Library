package edu.eci.dosw.DOSW_Library.core.util;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public static void validateNotEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }

    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public static void validateStringLength(String str, int minLength, int maxLength, String fieldName) {
        validateNotEmpty(str, fieldName);
        if (str.length() < minLength || str.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters");
        }
    }
}
