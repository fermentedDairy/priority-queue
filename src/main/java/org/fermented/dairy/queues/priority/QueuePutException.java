package org.fermented.dairy.queues.priority;

/**
 * Exception thrown when queue put operation fails
 */
public class QueuePutException extends QueueException{

    /**
     * @param message Descriptive message
     * @param causedBy Exception being wrapped
     */
    public QueuePutException(final String message, final Throwable causedBy){
        super(message, causedBy);
    }

    /**
     * @param message Descriptive message
     */
    public QueuePutException(final String message){
        super(message);
    }
}
