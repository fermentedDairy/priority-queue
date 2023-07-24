package org.fermented.dairy.queues.priority.impl;

import org.fermented.dairy.queues.priority.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;

import java.util.Map;

import static org.fermented.dairy.queues.priority.Priority.MEDIUM;

public final class DefaultPriorityQueueImpl<M> extends CustomPriorityQueueImpl<M, Priority> implements DefaultPriorityQueue<M> {

    public DefaultPriorityQueueImpl(final Map<String, Object> properties) {
        super(properties, Priority.asSet() ,MEDIUM);
    }
}
