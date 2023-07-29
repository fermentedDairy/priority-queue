package org.fermented.dairy.queues.priority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum providing a default priority.
 */
public enum Priority {
    LOWEST,
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    public static Set<Priority> asSet() {
        return Arrays.stream(values()).collect(Collectors.toSet());
    }
}
