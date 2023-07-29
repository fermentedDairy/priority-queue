package org.fermented.dairy.queues.priority;

/**
 * Exception thrown during the queue instantiation phase.
 */
public class QueueInstantiationException extends QueueException {

    public QueueInstantiationException(final String message, final Object... params) {
        super(message, params);
    }
}
