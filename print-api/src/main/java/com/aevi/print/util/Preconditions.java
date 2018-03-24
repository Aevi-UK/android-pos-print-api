package com.aevi.print.util;

public final class Preconditions {
    private Preconditions() {
    }

    public static void checkState(boolean check, String message, Object... messageParams) {
        if (messageParams.length > 0) {
            message = String.format(message, messageParams);
        }

        if (!check) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkArgument(boolean check, String message, Object... messageParams) {
        if (messageParams.length > 0) {
            message = String.format(message, messageParams);
        }

        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }

    public static void checkArgument(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
