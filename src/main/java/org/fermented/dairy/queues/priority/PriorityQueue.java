package org.fermented.dairy.queues.priority;

import java.util.Map;
import java.util.Optional;

import org.fermented.dairy.queues.priority.impl.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.impl.IntegerRangePriorityQueue;

public interface PriorityQueue<M, P extends Comparable<P>> {

    String MAX_QUEUE_DEPTH_PROPERTY = "maxQueueDepth";
    String MAX_POLL_WAIT_TIME_PROPERTY = "maxPollWaitTime";
    String MAX_PUT_WAIT_TIME_PROPERTY = "maxPutWaitTime";

    static <T> org.fermented.dairy.queues.priority.DefaultPriorityQueue<T> getQueue() {
        return new DefaultPriorityQueue<>(Map.of());
    }

    static <T> org.fermented.dairy.queues.priority.DefaultPriorityQueue<T> getQueue(final Map<String, Object> properties) {
        return new DefaultPriorityQueue<>(properties);
    }

    static <T> org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<T> getQueue(final Map<String, Object> properties, final int max, final int min) {
        return new IntegerRangePriorityQueue<>(properties, max, min);
    }

    static <T> org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<T> getQueue(final int max, final int min) {
        return new IntegerRangePriorityQueue<>(Map.of(), max, min);
    }

    static <T> org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<T> getQueue(final int max, final int min, final int defaultPriority) {
        return new IntegerRangePriorityQueue<>(Map.of(), max, min, defaultPriority);
    }

    void offer(M message, P priority);

    void offer(M message);

    Optional<M> poll();

    Optional<M> poll(boolean wait);

    Optional<M> poll(long waitTimeout);

    Optional<M> peek();

    void purge();

    long depth();
}
