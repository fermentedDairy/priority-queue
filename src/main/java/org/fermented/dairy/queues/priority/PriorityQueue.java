package org.fermented.dairy.queues.priority;

import java.util.Map;
import java.util.Optional;
import org.fermented.dairy.queues.priority.impl.DefaultPriorityQueueImpl;
import org.fermented.dairy.queues.priority.impl.IntegerRangePriorityQueueImpl;

public interface PriorityQueue<M, P extends Comparable<P>> {

    String MAX_QUEUE_DEPTH_PROPERTY = "maxQueueDepth";
    String MAX_POLL_WAIT_TIME_PROPERTY = "maxPollWaitTime";
    String MAX_PUT_WAIT_TIME_PROPERTY = "maxPutWaitTime";

    static <T> DefaultPriorityQueue<T> getQueue() {
        return new DefaultPriorityQueueImpl<>(Map.of());
    }

    static <T> DefaultPriorityQueue<T> getQueue(final Map<String, Object> properties) {
        return new DefaultPriorityQueueImpl<>(properties);
    }

    static <T> IntergerRangePriorityQueue<T> getQueue(final Map<String, Object> properties, Integer max, Integer min) {
        return new IntegerRangePriorityQueueImpl<>(properties, max, min);
    }

    static <T> IntergerRangePriorityQueue<T> getIntegerQueue() {
        return new IntegerRangePriorityQueueImpl<>(Map.of(), 0, 4, 2);
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
