package org.fermented.dairy.queues.priority;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IntergerRangePriorityQueueTest {

    IntegerRangePriorityQueue<TestMessage> priorityQueue;

    @BeforeEach
    void createQueue() {
        priorityQueue = PriorityQueue.getIntegerQueue();
    }

    @DisplayName("when messages without any priority are put onto an empty queue then it should be polled in the order they were put")
    @Test
    void whenMessagesWithoutAnyPriorityArePutOntoAnEmptyQueueThenItShouldBePolledInTheOrderTheyWerePut() {
        final TestMessage firstMessage = new TestMessage(1, "message 1");
        final TestMessage secondMessage = new TestMessage(2, "message 2");
        priorityQueue.offer(firstMessage);
        priorityQueue.offer(secondMessage);
        assertAll("verify message order",
                () -> assertEquals(firstMessage, priorityQueue.poll().get(), "First message is not 1st"),
                () -> assertEquals(secondMessage, priorityQueue.poll().get(), "Second message is not 2nd")
        );
    }

    @DisplayName("when polling empty queue with no wait then return empty optional")
    @Test
    void whenPollingEmptyQueueWithNoWaitThenReturnEmptyOptional() {
        assertTrue(priorityQueue.poll().isEmpty());
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
        priorityQueue.offer(lowestMessage, Priority.LOWEST.ordinal());
        priorityQueue.offer(lowMessage, Priority.LOW.ordinal());
        priorityQueue.offer(mediumMessage, Priority.MEDIUM.ordinal());
        priorityQueue.offer(defaultPriority);
        priorityQueue.offer(highMessage, Priority.HIGH.ordinal());
        priorityQueue.offer(urgentMessage, Priority.URGENT.ordinal());
        assertAll("verify message order",
                () -> assertEquals(urgentMessage, priorityQueue.poll().get(), "urgent should be first"),
                () -> assertEquals(highMessage, priorityQueue.poll().get(), "high should be second"),
                () -> assertEquals(mediumMessage, priorityQueue.poll().get(), "medium with priority should be third"),
                () -> assertEquals(defaultPriority, priorityQueue.poll().get(), "default should be in put order with medium"),
                () -> assertEquals(lowMessage, priorityQueue.poll().get(), "low should be 5th"),
                () -> assertEquals(lowestMessage, priorityQueue.poll().get(), "lowest should be last")
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
        priorityQueue.offer(lowMessage, Priority.LOW.ordinal());
        priorityQueue.offer(lowestMessage, Priority.LOWEST.ordinal());
        priorityQueue.offer(mediumMessage, Priority.MEDIUM.ordinal());
        priorityQueue.offer(defaultPriority);
        priorityQueue.offer(highMessage, Priority.HIGH.ordinal());
        priorityQueue.offer(urgentMessage, Priority.URGENT.ordinal());
        assertEquals(6, priorityQueue.depth(), "message count is incorrect");
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
        priorityQueue.offer(lowMessage, Priority.LOW.ordinal());
        priorityQueue.offer(lowestMessage, Priority.LOWEST.ordinal());
        priorityQueue.offer(mediumMessage, Priority.MEDIUM.ordinal());
        priorityQueue.offer(defaultPriority);
        priorityQueue.offer(highMessage, Priority.HIGH.ordinal());
        priorityQueue.offer(urgentMessage, Priority.URGENT.ordinal());
        assertEquals(6, priorityQueue.depth(), "message count is incorrect (pre-purge)");
        priorityQueue.purge();
        assertAll("Verify purge was successful",
                () -> assertEquals(0, priorityQueue.depth(), "message count is incorrect"),
                () -> assertTrue(priorityQueue.poll().isEmpty(), "poll result should be empty"));
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
        priorityQueue.offer(lowMessage, Priority.LOW.ordinal());
        priorityQueue.offer(lowestMessage, Priority.LOWEST.ordinal());
        priorityQueue.offer(mediumMessage, Priority.MEDIUM.ordinal());
        priorityQueue.offer(defaultPriority);
        priorityQueue.offer(highMessage, Priority.HIGH.ordinal());
        priorityQueue.offer(urgentMessage, Priority.URGENT.ordinal());
        assertAll("verify message order",
                () -> assertEquals(urgentMessage, priorityQueue.peek().get(), "urgent should be peeked first"),
                () -> assertEquals(urgentMessage, priorityQueue.poll().get(), "urgent should be polled first"),

                () -> assertEquals(highMessage, priorityQueue.peek().get(), "high should be peeked second"),
                () -> assertEquals(highMessage, priorityQueue.poll().get(), "high should be polled second"),

                () -> assertEquals(mediumMessage, priorityQueue.peek().get(), "medium with priority should be peeked third"),
                () -> assertEquals(mediumMessage, priorityQueue.poll().get(), "medium with priority should be polled third"),

                () -> assertEquals(defaultPriority, priorityQueue.peek().get(), "default should be in put order with peeked medium"),
                () -> assertEquals(defaultPriority, priorityQueue.poll().get(), "default should be in put order with peeked medium"),

                () -> assertEquals(lowMessage, priorityQueue.peek().get(), "low should be peeked 5th"),
                () -> assertEquals(lowMessage, priorityQueue.poll().get(), "low should be polled 5th"),

                () -> assertEquals(lowestMessage, priorityQueue.peek().get(), "lowest should be peeked last"),
                () -> assertEquals(lowestMessage, priorityQueue.poll().get(), "lowest should be polled last")
        );
    }

}