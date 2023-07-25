package org.fermented.dairy.queues.priority;

import static org.fermented.dairy.queues.priority.PriorityQueue.MAX_PUT_WAIT_TIME_PROPERTY;
import static org.fermented.dairy.queues.priority.PriorityQueue.MAX_QUEUE_DEPTH_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for DefaultPriorityQueue implementation.
 */
class DefaultPriorityQueueTest {

    DefaultPriorityQueue<TestMessage> defaultPriorityQueue;

    @BeforeEach
    void createQueue() {
        defaultPriorityQueue = PriorityQueue.getQueue();
    }

    @DisplayName("when messages without any priority are put onto an empty queue then it should be polled in the order they were put")
    @Test
    void whenMessagesWithoutAnyPriorityArePutOntoAnEmptyQueueThenItShouldBePolledInTheOrderTheyWerePut() {
        final TestMessage firstMessage = new TestMessage(1, "message 1");
        final TestMessage secondMessage = new TestMessage(2, "message 2");
        defaultPriorityQueue.offer(firstMessage);
        defaultPriorityQueue.offer(secondMessage);
        assertAll("verify message order",
                () -> assertEquals(firstMessage, defaultPriorityQueue.poll().get(), "First message is not 1st"),
                () -> assertEquals(secondMessage, defaultPriorityQueue.poll().get(), "Second message is not 2nd")
        );
    }

    @DisplayName("when polling empty queue with no wait then return empty optional")
    @Test
    void whenPollingEmptyQueueWithNoWaitThenReturnEmptyOptional() {
        assertTrue(defaultPriorityQueue.poll().isEmpty());
    }

    @DisplayName("when messages with priorities are put onto an empty queue then it should be polled in the priority order")
    @Test
    void whenMessagesWithPrioritiesArePutOntoAnEmptyQueueThenItShouldBePolledInThePriorityOrder() {
        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowestMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(lowMessage, Priority.LOW);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        assertAll("verify message order",
                () -> assertEquals(urgentMessage, defaultPriorityQueue.poll().get(), "urgent should be first"),
                () -> assertEquals(highMessage, defaultPriorityQueue.poll().get(), "high should be second"),
                () -> assertEquals(mediumMessage, defaultPriorityQueue.poll().get(), "medium with priority should be third"),
                () -> assertEquals(defaultPriority, defaultPriorityQueue.poll().get(), "default should be in put order with medium"),
                () -> assertEquals(lowMessage, defaultPriorityQueue.poll().get(), "low should be 5th"),
                () -> assertEquals(lowestMessage, defaultPriorityQueue.poll().get(), "lowest should be last")
        );
    }

    @DisplayName("when messages with priorities are put onto an empty queue then all messages should be included in the count")
    @Test
    void whenMessagesWithPrioritiesArePutOntoAnEmptyQueueThenAllMessagesShouldBeIncludedInTheCount() {
        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowMessage, Priority.LOW);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        assertEquals(6, defaultPriorityQueue.depth(), "message count is incorrect");
    }

    @DisplayName("when messages with priorities are put onto an empty queue and then purged, the count should be 0 and poll result is empty optional")
    @Test
    void whenMessagesWithPrioritiesArePutOntoAnEmptyQueueAndThenPurgedTheCountShouldBe0AndPollResultIsEmptyOptional() {
        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowMessage, Priority.LOW);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        assertEquals(6, defaultPriorityQueue.depth(), "message count is incorrect (pre-purge)");
        defaultPriorityQueue.purge();
        assertAll("Verify purge was successful",
                () -> assertEquals(0, defaultPriorityQueue.depth(), "message count is incorrect"),
                () -> assertTrue(defaultPriorityQueue.poll().isEmpty(), "poll result should be empty"));
    }

    @DisplayName("when messages with priorities are put onto an empty queue and then peek should return next deilvered message")
    @Test
    void whenMessagesWithPrioritiesArePutOntoAnEmptyQueueAndThenPeekShouldReturnUrgentMessage() {
        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowMessage, Priority.LOW);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        assertAll("verify message order",
                () -> assertEquals(urgentMessage, defaultPriorityQueue.peek().get(), "urgent should be peeked first"),
                () -> assertEquals(urgentMessage, defaultPriorityQueue.poll().get(), "urgent should be polled first"),

                () -> assertEquals(highMessage, defaultPriorityQueue.peek().get(), "high should be peeked second"),
                () -> assertEquals(highMessage, defaultPriorityQueue.poll().get(), "high should be polled second"),

                () -> assertEquals(mediumMessage, defaultPriorityQueue.peek().get(), "medium with priority should be peeked third"),
                () -> assertEquals(mediumMessage, defaultPriorityQueue.poll().get(), "medium with priority should be polled third"),

                () -> assertEquals(defaultPriority, defaultPriorityQueue.peek().get(), "default should be in put order with peeked medium"),
                () -> assertEquals(defaultPriority, defaultPriorityQueue.poll().get(), "default should be in put order with peeked medium"),

                () -> assertEquals(lowMessage, defaultPriorityQueue.peek().get(), "low should be peeked 5th"),
                () -> assertEquals(lowMessage, defaultPriorityQueue.poll().get(), "low should be polled 5th"),

                () -> assertEquals(lowestMessage, defaultPriorityQueue.peek().get(), "lowest should be peeked last"),
                () -> assertEquals(lowestMessage, defaultPriorityQueue.poll().get(), "lowest should be polled last")
        );
    }

    @DisplayName("when the queue is full then put throws after timeout")
    @Test
    void whenTheQueueIsFullThenPutThrowsAfterTimeout() {
        defaultPriorityQueue = PriorityQueue.getQueue(
                Map.of(
                        MAX_PUT_WAIT_TIME_PROPERTY, 1000L,
                        MAX_QUEUE_DEPTH_PROPERTY, 5L
                        )
        );

        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowMessage, Priority.LOW);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        final QueuePutException exception = assertThrows(QueuePutException.class, () -> defaultPriorityQueue.offer(urgentMessage, Priority.URGENT));
        assertEquals("Put failed within timeout, max queue depth exceeded", exception.getMessage());
    }
}
