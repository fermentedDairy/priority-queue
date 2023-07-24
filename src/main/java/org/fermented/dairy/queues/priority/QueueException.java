package org.fermented.dairy.queues.priority;

/**
 * Parent exception for all exceptions thrown by the queue implementations
 */
public class QueueException extends RuntimeException{

    /**
     * @param message Descriptive message
     * @param causedBy Exception being wrapped
     */
    public QueueException(final String message, final Throwable causedBy) {
        super(message, causedBy);
    }

    /**
     * @param message Descriptive message
     */
    public QueueException(final String message) {
        super(message);
    }
}
