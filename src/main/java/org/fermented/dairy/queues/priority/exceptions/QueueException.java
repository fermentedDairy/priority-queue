package org.fermented.dairy.queues.priority.exceptions;

/**
 * Parent exception for all exceptions thrown by the queue implementations.
 */
public class QueueException extends RuntimeException {

    /**
     * Constructor with caused by throwable and formatted message.
     *
     * @param causedBy Exception being wrapped
     * @param message  Descriptive message {@link java.util.Formatter}
     * @param params Parameters to be used when formatting the message
     */
    public QueueException(final Throwable causedBy, final String message, final Object... params) {
        super(String.format(message, params), causedBy);
    }

    /**
     * Constructor with formatted message.
     *
     * @param message Descriptive message {@link java.util.Formatter}
     * @param params Parameters to be used when formatting the message
     */
    public QueueException(final String message, final Object... params) {
        super(String.format(message, params));
    }
}
