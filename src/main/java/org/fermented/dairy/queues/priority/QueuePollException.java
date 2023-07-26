package org.fermented.dairy.queues.priority;

/**
 * Runtime Exception thrown when queue poll operation fails.
 */
public class QueuePollException extends QueueException {

    /**
     * see {@link QueueException#QueueException(Throwable, String, Object...)}.
     */
    public QueuePollException(final String message, final Object... params) {
        super(message, params);
    }
}
