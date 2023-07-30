package org.fermented.dairy.queues.priority.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.fermented.dairy.queues.priority.AbstractArrayPriorityQueue;
import org.fermented.dairy.queues.priority.exceptions.QueueInstantiationException;

/**
 * PriorityQueue implementation using {@link Integer} as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class IntegerRangePriorityQueueImpl<M> extends AbstractArrayPriorityQueue<M, Integer>
        implements org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<M> {

    private final Integer minPriority;

    public IntegerRangePriorityQueueImpl(final Map<String, Object> properties,
                                         final Integer minPriority,
                                         final Integer maxPriority) {
        super(properties, getBoundedIntegerSet(minPriority, maxPriority));
        this.minPriority = minPriority;
    }

    public IntegerRangePriorityQueueImpl(final Map<String, Object> properties,
                                         final Integer minPriority,
                                         final Integer maxPriority,
                                         final Integer defaultPriority) {
        super(properties, getBoundedIntegerSet(minPriority, maxPriority), defaultPriority);
        this.minPriority = minPriority;
    }

    private static Set<Integer> getBoundedIntegerSet(final Integer min, final Integer max) {
        if (min > max) {
            throw new QueueInstantiationException("minPriority cannot be greater than maxPriority");
        }
        return IntStream.range(min, max + 1).boxed().collect(Collectors.toSet());
    }

    @Override
    public int getPriorityIndex(final Integer priority) {
        return priority - minPriority;
    }
}
