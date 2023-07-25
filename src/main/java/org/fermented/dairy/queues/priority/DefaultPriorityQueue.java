package org.fermented.dairy.queues.priority;

import org.fermented.dairy.queues.priority.impl.DefaultPriorityQueueImpl;

/**
 * Priority queue using the {@link Priority} enum as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public sealed interface DefaultPriorityQueue<M> extends PriorityQueue<M, Priority>
        permits DefaultPriorityQueueImpl {
}
