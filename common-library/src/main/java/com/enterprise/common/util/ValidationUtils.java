package com.enterprise.common.util;

import com.enterprise.common.exception.BusinessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    public static void requireNonNull(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new BusinessException(message);
        }
    }

    public static void requireNonEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(message);
        }
    }

    public static void requireNonEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    public static void requireNonEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    public static void requireFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }

    public static void requirePositive(Number number, String message) {
        requireNonNull(number, message);
        requireTrue(number.doubleValue() > 0, message);
    }

    public static void requireNonNegative(Number number, String message) {
        requireNonNull(number, message);
        requireTrue(number.doubleValue() >= 0, message);
    }
}