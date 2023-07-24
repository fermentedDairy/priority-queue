package org.fermented.dairy.queues.priority.impl;

import static org.fermented.dairy.queues.priority.Priority.MEDIUM;

import java.util.Map;
import org.fermented.dairy.queues.priority.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;

/**
 * PriorityQueue implementation using the {@link Priority} enum as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class DefaultPriorityQueueImpl<M>
        extends CustomPriorityQueueImpl<M, Priority>
        implements DefaultPriorityQueue<M> {

    public DefaultPriorityQueueImpl(final Map<String, Object> properties) {
        super(properties, Priority.asSet(), MEDIUM);
    }
}
