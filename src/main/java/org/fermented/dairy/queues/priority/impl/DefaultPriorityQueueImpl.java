package org.fermented.dairy.queues.priority.impl;

import org.fermented.dairy.queues.priority.DefaultPriorityQueue;
import org.fermented.dairy.queues.priority.Priority;
import org.fermented.dairy.queues.priority.QueuePutException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DefaultPriorityQueueImpl<M> implements DefaultPriorityQueue<M> {

    private static final long DEFAULT_MAX_QUEUE_DEPTH = 50000L;
    private static final long DEFAULT_PUT_BLOCK_TIMEOUT_MS = 0L;
    private static final long DEFAULT_POLL_WAIT_TIMEOUT_MS = 100L;



    private static final List<Priority> ORDERED_PRIORITIES = Arrays
            .stream(Priority.values())
            .sorted(Comparator.reverseOrder())
            .toList();

    private final Map<Priority, Queue<M>> priorityQueueMap = createPriorityQueueMap();

    private final long maxQueueDepth;
    private final long putBlockTimeout;
    private final long pollWaitTimeout;

    private final ReentrantLock pollLock = new ReentrantLock();

    public DefaultPriorityQueueImpl(final Map<String, Object> properties) {
        maxQueueDepth = (long) properties.getOrDefault(MAX_QUEUE_DEPTH_PROPERTY, DEFAULT_MAX_QUEUE_DEPTH);
        putBlockTimeout = (long) properties.getOrDefault(MAX_PUT_WAIT_TIME_PROPERTY, DEFAULT_PUT_BLOCK_TIMEOUT_MS);
        pollWaitTimeout = (long) properties.getOrDefault(MAX_POLL_WAIT_TIME_PROPERTY, DEFAULT_POLL_WAIT_TIMEOUT_MS);
    }

    private Map<Priority, Queue<M>> createPriorityQueueMap() {
        return Arrays.stream(Priority.values()).collect(Collectors.toMap(
                Function.identity(),
                p -> new LinkedList<>()
        ));
    }

    @Override
    public void offer(final M message, final Priority priority) {
        priorityQueueMap.get(priority).offer(message);
    }

    @Override
    public void offer(final M message) {
        offer(message, Priority.MEDIUM);
    }

    @Override
    public Optional<M> poll() {
        return poll(0L);
    }

    @Override
    public Optional<M> poll(final boolean wait) {
        return poll(pollWaitTimeout);
    }

    @Override
    public Optional<M> poll(final long waitTimeout) {
        try
        {
            if(waitTimeout != 0) pollLock.tryLock(waitTimeout, TimeUnit.MILLISECONDS);

            return ORDERED_PRIORITIES.stream()
                    .filter(
                            priority -> !priorityQueueMap.get(priority).isEmpty()
                    ).findFirst()
                    .map(priority -> priorityQueueMap.get(priority).poll());
        } catch (final InterruptedException e) {
            throw new QueuePutException("could not gain the lock on put within the timeout", e);
        } finally {
            if(pollLock.isHeldByCurrentThread()) pollLock.unlock();
        }
    }

    @Override
    public Optional<M> peek() {
        return Optional.empty();
    }

    @Override
    public void purge() {

    }
}
