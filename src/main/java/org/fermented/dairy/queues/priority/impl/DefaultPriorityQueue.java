package org.fermented.dairy.queues.priority.impl;

import static org.fermented.dairy.queues.priority.Priority.MEDIUM;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.fermented.dairy.queues.priority.AbstractPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;

/**
 * PriorityQueue implementation using the {@link Priority} enum as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class DefaultPriorityQueue<M>
        extends AbstractPriorityQueue<M, Priority>
        implements org.fermented.dairy.queues.priority.DefaultPriorityQueue<M> {

    private final Queue<M>[] queues;

    public DefaultPriorityQueue(final Map<String, Object> properties) {
        super(properties, Priority.asSet(), MEDIUM);
        queues = createQueueArray();
    }

    @Override
    protected void offerMessage(final M message, final Priority priority) {
        queues[priority.ordinal()].offer(message);
    }

    @Override
    protected Optional<M> pollMessage() {
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if(!queue.isEmpty()) {
                return Optional.of(queue.poll());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<M> peek() {
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if(!queue.isEmpty()) {
                return Optional.of(queue.peek());
            }
        }
        return Optional.empty();
    }

    @Override
    public void purge() {
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if(!queue.isEmpty()) {
                queue.clear();
            }
        }
    }

    @Override
    public long depth() {
        long sum = 0;
        for(int i = queues.length -1; i >= 0; i--){
            final Queue<M> queue = queues[i];
            if(!queue.isEmpty()) {
                sum += queue.size();
            }
        }
        return sum;
    }

    private Queue<M>[] createQueueArray() {
        @SuppressWarnings("unchecked") final Queue<M>[] queueArray = new Queue[Priority.values().length];
        for(int i = 0; i < Priority.values().length; i++) {
            queueArray[i] = new LinkedList<>();
        }
        return queueArray;
    }
}
