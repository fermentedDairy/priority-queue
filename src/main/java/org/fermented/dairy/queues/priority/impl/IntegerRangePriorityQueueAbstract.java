package org.fermented.dairy.queues.priority.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.fermented.dairy.queues.priority.AbstractPriorityQueue;
import org.fermented.dairy.queues.priority.IntergerRangePriorityQueue;
import org.fermented.dairy.queues.priority.QueueInstantiationException;

/**
 * PriorityQueue implementation using {@link Integer} as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class IntegerRangePriorityQueueAbstract<M> extends AbstractPriorityQueue<M, Integer>
        implements IntergerRangePriorityQueue<M> {
    public IntegerRangePriorityQueueAbstract(final Map<String, Object> properties,
                                             final Integer minPriority,
                                             final Integer maxPriority) {

        super(properties, getBoundedIntegerSet(minPriority, maxPriority));
    }

    public IntegerRangePriorityQueueAbstract(final Map<String, Object> properties,
                                             final Integer minPriority,
                                             final Integer maxPriority,
                                             final Integer defaultPriority) {
        super(properties, getBoundedIntegerSet(minPriority, maxPriority), defaultPriority);
    }

    private static Set<Integer> getBoundedIntegerSet(final Integer min, final Integer max) {
        if (min > max) {
            throw new QueueInstantiationException("minPriority cannot be greater than maxPriority");
        }
        return IntStream.range(min, max + 1).boxed().collect(Collectors.toSet());
    }
}
