package org.fermented.dairy.queues.priority;

import org.fermented.dairy.queues.priority.impl.IntegerRangePriorityQueueImpl;

/**
 * PriorityQueue that uses an integer range for priorities.
 *
 * @param <M> The type of objects placed on the queue.
 */
public sealed interface IntergerRangePriorityQueue<M> extends PriorityQueue<M, Integer>
        permits IntegerRangePriorityQueueImpl {
}
