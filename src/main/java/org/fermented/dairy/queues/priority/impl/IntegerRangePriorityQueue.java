package org.fermented.dairy.queues.priority.impl;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.fermented.dairy.queues.priority.AbstractPriorityQueue;
import org.fermented.dairy.queues.priority.QueueInstantiationException;

/**
 * PriorityQueue implementation using {@link Integer} as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class IntegerRangePriorityQueue<M> extends AbstractPriorityQueue<M, Integer>
        implements org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<M> {
    public IntegerRangePriorityQueue(final Map<String, Object> properties,
                                     final Integer minPriority,
                                     final Integer maxPriority) {
        super(properties, getBoundedIntegerSet(minPriority, maxPriority));
        queues = createQueueArray(getBoundedIntegerSet(minPriority, maxPriority));
        this.minPriority = minPriority;
    }

    public IntegerRangePriorityQueue(final Map<String, Object> properties,
                                     final Integer minPriority,
                                     final Integer maxPriority,
                                     final Integer defaultPriority) {
        super(properties, getBoundedIntegerSet(minPriority, maxPriority), defaultPriority);
        queues = createQueueArray(getBoundedIntegerSet(minPriority, maxPriority));
        this.minPriority = minPriority;
    }

    private final Queue<M>[] queues;
    private final Integer minPriority;

    @Override
    protected void offerMessage(final M message, final Integer priority) {
        final int queueIndex = priority - minPriority;
        queues[queueIndex].offer(message);
    }

    @Override
    protected Optional<M> pollMessage() {
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if (!queue.isEmpty()) {
                return Optional.of(queue.poll());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<M> peek() {
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if (!queue.isEmpty()) {
                return Optional.of(queue.peek());
            }
        }
        return Optional.empty();
    }

    @Override
    public void purge() {
        for (final Queue<M> queue: queues) {
            queue.clear();
        }
    }

    @Override
    public long depth() {
        long sum = 0;
        for (final Queue<M> queue: queues) {
            sum += queue.size();
        }
        return sum;
    }

    private static Set<Integer> getBoundedIntegerSet(final Integer min, final Integer max) {
        if (min > max) {
            throw new QueueInstantiationException("minPriority cannot be greater than maxPriority");
        }
        return IntStream.range(min, max + 1).boxed().collect(Collectors.toSet());
    }

    private Queue<M>[] createQueueArray(final Set<Integer> boundedIntegerSet) {
        @SuppressWarnings("unchecked") final Queue<M>[] queueArray = new Queue[boundedIntegerSet.size()];
        for(int i = 0; i < boundedIntegerSet.size(); i++) {
            queueArray[i] = new LinkedList<>();
        }
        return queueArray;
    }
}
