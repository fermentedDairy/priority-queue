package org.fermented.dairy.queues.priority.impl;

import org.fermented.dairy.queues.priority.PriorityQueue;
import org.fermented.dairy.queues.priority.QueuePutException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomPriorityQueueImpl<M, P extends Comparable<P>> implements PriorityQueue<M, P> {
    private static final long DEFAULT_MAX_QUEUE_DEPTH = 50000L;
    private static final long DEFAULT_PUT_BLOCK_TIMEOUT_MS = 0L;
    private static final long DEFAULT_POLL_WAIT_TIMEOUT_MS = 100L;



    private final List<P> orderedPriorities;

    private final Map<P, Queue<M>> priorityQueueMap;

    private final P defaultPriority;

    private final long maxQueueDepth;
    private final long putBlockTimeout;
    private final long pollWaitTimeout;

    private final ReentrantLock pollLock = new ReentrantLock();

    public CustomPriorityQueueImpl(final Map<String, Object> properties, Set<P> prioritySet) {
        this.maxQueueDepth = (long) properties.getOrDefault(MAX_QUEUE_DEPTH_PROPERTY, DEFAULT_MAX_QUEUE_DEPTH);
        this.putBlockTimeout = (long) properties.getOrDefault(MAX_PUT_WAIT_TIME_PROPERTY, DEFAULT_PUT_BLOCK_TIMEOUT_MS);
        this.pollWaitTimeout = (long) properties.getOrDefault(MAX_POLL_WAIT_TIME_PROPERTY, DEFAULT_POLL_WAIT_TIMEOUT_MS);
        this.priorityQueueMap = createPriorityQueueMap(prioritySet);
        this.orderedPriorities = prioritySet.stream().sorted(Comparator.reverseOrder()).toList();
        this.defaultPriority = orderedPriorities.get(orderedPriorities.size()/2);
    }

    public CustomPriorityQueueImpl(final Map<String, Object> properties, Set<P> prioritySet, P defaultPriority) {
        this.maxQueueDepth = (long) properties.getOrDefault(MAX_QUEUE_DEPTH_PROPERTY, DEFAULT_MAX_QUEUE_DEPTH);
        this.putBlockTimeout = (long) properties.getOrDefault(MAX_PUT_WAIT_TIME_PROPERTY, DEFAULT_PUT_BLOCK_TIMEOUT_MS);
        this.pollWaitTimeout = (long) properties.getOrDefault(MAX_POLL_WAIT_TIME_PROPERTY, DEFAULT_POLL_WAIT_TIMEOUT_MS);
        this.priorityQueueMap = createPriorityQueueMap(prioritySet);
        this.orderedPriorities = prioritySet.stream().sorted(Comparator.reverseOrder()).toList();
        this.defaultPriority = defaultPriority;
    }

    private Map<P, Queue<M>> createPriorityQueueMap(Set<P> prioritySet) {
        return prioritySet.stream().collect(Collectors.toMap(
                Function.identity(),
                p -> new LinkedList<>()
        ));
    }

    @Override
    public void offer(final M message, final P priority) {
        priorityQueueMap.get(priority).offer(message);
    }

    @Override
    public void offer(final M message) {
        offer(message, defaultPriority);
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
        boolean isLocked = false;
        try
        {
            isLocked = pollLock.tryLock(waitTimeout, TimeUnit.MILLISECONDS);

            return orderedPriorities.stream()
                    .filter(
                            priority -> !priorityQueueMap.get(priority).isEmpty()
                    ).findFirst()
                    .map(priority -> priorityQueueMap.get(priority).poll());
        } catch (final InterruptedException e) {
            throw new QueuePutException("could not gain the lock on put within the timeout", e);
        } finally {
            if (isLocked || pollLock.isHeldByCurrentThread()) pollLock.unlock();
        }
    }

    @Override
    public Optional<M> peek() {
        return orderedPriorities.stream().map(priorityQueueMap::get)
                .filter(queue -> !queue.isEmpty())
                .map(Queue::peek)
                .findFirst();
    }

    @Override
    public void purge() {
        orderedPriorities.stream().map(priorityQueueMap::get).forEach(Queue::clear);
    }

    @Override
    public long depth() {
        return orderedPriorities.stream().map(priorityQueueMap::get)
                .map(Queue::size)
                .mapToInt(Integer::intValue).sum();
    }
}