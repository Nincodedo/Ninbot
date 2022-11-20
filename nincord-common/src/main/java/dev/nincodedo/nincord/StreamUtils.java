package dev.nincodedo.nincord;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class StreamUtils {
    public static <T> Comparator<T> shuffle() {
        Map<Object, UUID> uuidMap = new IdentityHashMap<>();
        return Comparator.comparing(item -> uuidMap.computeIfAbsent(item, v -> UUID.randomUUID()));
    }
}
