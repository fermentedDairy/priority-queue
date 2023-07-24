package org.fermented.dairy.queues.priority;

import org.fermented.dairy.queues.priority.impl.DefaultPriorityQueueImpl;

import java.util.Map;
import java.util.Optional;

public interface PriorityQueue<M, P extends Comparable<P>> {

    String MAX_QUEUE_DEPTH_PROPERTY = "maxQueueDepth";
    String MAX_POLL_WAIT_TIME_PROPERTY = "maxPollWaitTime";
    String MAX_PUT_WAIT_TIME_PROPERTY = "maxPutWaitTime";

    static <T> DefaultPriorityQueueImpl<T> getQueue(){
        return new DefaultPriorityQueueImpl<>(Map.of());
    }

    static <T> DefaultPriorityQueueImpl<T> getQueue(final Map<String, Object> properties){
        return new DefaultPriorityQueueImpl<>(properties);
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
