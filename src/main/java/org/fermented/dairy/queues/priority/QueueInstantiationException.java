package org.fermented.dairy.queues.priority;

public class QueueInstantiationException extends QueueException {

    public QueueInstantiationException(final String message, final Object... params) {
        super(message, params);
    }
}
