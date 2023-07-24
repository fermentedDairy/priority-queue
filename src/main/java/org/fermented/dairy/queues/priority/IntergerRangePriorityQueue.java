package org.fermented.dairy.queues.priority;

/**
 * PriorityQueue that uses an integer range for priorities.
 *
 * @param <M> The type of objects placed on the queue.
 */
public interface IntergerRangePriorityQueue<M> extends PriorityQueue<M, Integer> {
}
