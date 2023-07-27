package org.fermented.dairy.queues.priority.impl;

import static org.fermented.dairy.queues.priority.Priority.MEDIUM;

import java.util.Map;
import java.util.Optional;

import org.fermented.dairy.queues.priority.AbstractPriorityQueue;
import org.fermented.dairy.queues.priority.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;

/**
 * PriorityQueue implementation using the {@link Priority} enum as the priority.
 *
 * @param <M> The type of objects placed on the queue.
 */
public final class DefaultPriorityQueueAbstract<M>
        extends AbstractPriorityQueue<M, Priority>
        implements DefaultPriorityQueue<M> {

    public DefaultPriorityQueueAbstract(final Map<String, Object> properties) {
        super(properties, Priority.asSet(), MEDIUM);
    }

    @Override
    protected void offerMessage(final M message, final Priority priority) {

    }

    @Override
    protected Optional<M> pollMessage() {
        return Optional.empty();
    }

    @Override
    public Optional<M> peek() {
        return Optional.empty();
    }

    @Override
    public void purge() {

    }

    @Override
    public long depth() {
        return 0;
    }
}
