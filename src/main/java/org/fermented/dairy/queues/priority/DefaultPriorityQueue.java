package org.fermented.dairy.queues.priority;

/**
 * Priority queue using the {@link Priority} enum as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public interface DefaultPriorityQueue<M> extends PriorityQueue<M, Priority> {
}
