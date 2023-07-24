package org.fermented.dairy.queues.priority.impl;

import org.fermented.dairy.queues.priority.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;
import org.fermented.dairy.queues.priority.PriorityQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DefaultPriorityQueue implementation
 */
public class DefaultPriorityQueueTest {

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
        defaultPriorityQueue.offer(lowMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOW);
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
        defaultPriorityQueue.offer(lowMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOW);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        assertEquals(6, defaultPriorityQueue.depth(), "message count is incorrect");
    }

    @DisplayName("when messages with priorities are put onto an empty queue and then purged then then the count should be 0 and poll result is empty optional")
    @Test
    void whenMessagesWithPrioritiesArePutOntoAnEmptyQueueAndThenPurgedThenThenTheCountShouldBe0AndPollResultIsEmptyOptional() {
        final TestMessage lowestMessage = new TestMessage(1, "message lowest");
        final TestMessage lowMessage = new TestMessage(2, "message low");
        final TestMessage mediumMessage = new TestMessage(3, "message medium");
        final TestMessage defaultPriority = new TestMessage(4, "message medium as default");
        final TestMessage highMessage = new TestMessage(5, "message high");
        final TestMessage urgentMessage = new TestMessage(6, "message urgent");
        defaultPriorityQueue.offer(lowMessage, Priority.LOWEST);
        defaultPriorityQueue.offer(lowestMessage, Priority.LOW);
        defaultPriorityQueue.offer(mediumMessage, Priority.MEDIUM);
        defaultPriorityQueue.offer(defaultPriority);
        defaultPriorityQueue.offer(highMessage, Priority.HIGH);
        defaultPriorityQueue.offer(urgentMessage, Priority.URGENT);
        defaultPriorityQueue.purge();
        assertAll("Verify purge was successful",
                () -> assertEquals(0, defaultPriorityQueue.depth(), "message count is incorrect"),
                () -> assertTrue(defaultPriorityQueue.poll().isEmpty(), "poll result should be empty"));
    }

}