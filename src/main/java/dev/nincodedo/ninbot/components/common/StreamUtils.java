package dev.nincodedo.ninbot.components.common;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public final class StreamUtils {
    private StreamUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> Comparator<T> shuffle() {
        Map<Object, UUID> uuidMap = new IdentityHashMap<>();
        return Comparator.comparing(item -> uuidMap.computeIfAbsent(item, v -> UUID.randomUUID()));
    }
}
