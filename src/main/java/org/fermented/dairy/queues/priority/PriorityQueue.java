package org.fermented.dairy.queues.priority;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fermented.dairy.queues.priority.impl.DefaultPriorityQueueAbstract;
import org.fermented.dairy.queues.priority.impl.IntegerRangePriorityQueue;

public interface PriorityQueue<M, P extends Comparable<P>> {

    String MAX_QUEUE_DEPTH_PROPERTY = "maxQueueDepth";
    String MAX_POLL_WAIT_TIME_PROPERTY = "maxPollWaitTime";
    String MAX_PUT_WAIT_TIME_PROPERTY = "maxPutWaitTime";

    static <T> DefaultPriorityQueue<T> getQueue() {
        return new DefaultPriorityQueueAbstract<>(Map.of());
    }

    static <T> DefaultPriorityQueue<T> getQueue(final Map<String, Object> properties) {
        return new DefaultPriorityQueueAbstract<>(properties);
    }

    static <T> org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<T> getQueue(final Map<String, Object> properties, Integer max, Integer min) {
        return new IntegerRangePriorityQueue<>(properties, max, min);
    }

    static <T> org.fermented.dairy.queues.priority.IntegerRangePriorityQueue<T> getIntegerQueue() {
        return new IntegerRangePriorityQueue<>(Map.of(), 0, 4, 2);
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
