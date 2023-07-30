package org.fermented.dairy.queues.priority.exceptions;

/**
 * Runtime Exception thrown when queue put operation fails.
 */
public class QueuePutException extends QueueException {

    /**
     * see {@link QueueException#QueueException(Throwable, String, Object...)}.
     */
    public QueuePutException(final Throwable causedBy,
                             final String message,
                             final Object... params) {
        super(String.format(message, params), causedBy);
    }

    /**
     * see {@link QueueException#QueueException(String, Object...)}.
     */
    public QueuePutException(final String message,
                             final Object... params) {
        super(String.format(message, params));
    }
}
