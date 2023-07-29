package org.fermented.dairy.queues.priority;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of the PriorityQueue that uses a custom set of priorities.
 *
 * @param <M> The type of objects placed on the queue.
 * @param <P> The priority type
 */
public abstract class AbstractArrayPriorityQueue<M, P extends Comparable<P>> implements PriorityQueue<M, P> {
    private static final long DEFAULT_MAX_QUEUE_DEPTH = 50000L;
    private static final long DEFAULT_PUT_BLOCK_TIMEOUT_MS = 0L;
    private static final long DEFAULT_POLL_WAIT_TIMEOUT_MS = 100L;

    private final Set<P> prioritySet;

    private final P defaultPriority;

    private final long maxQueueDepth;
    private final long putBlockTimeout;
    private final long pollWaitTimeout;

    private final ReentrantLock pollLock = new ReentrantLock();
    private final ReentrantLock putLock = new ReentrantLock();

    private final Queue<M>[] queues;

    protected AbstractArrayPriorityQueue(final Map<String, Object> properties, final Set<P> prioritySet) {
        this.maxQueueDepth = (long) properties.getOrDefault(MAX_QUEUE_DEPTH_PROPERTY, DEFAULT_MAX_QUEUE_DEPTH);
        this.putBlockTimeout = (long) properties.getOrDefault(MAX_PUT_WAIT_TIME_PROPERTY, DEFAULT_PUT_BLOCK_TIMEOUT_MS);
        this.pollWaitTimeout = (long) properties.getOrDefault(MAX_POLL_WAIT_TIME_PROPERTY, DEFAULT_POLL_WAIT_TIMEOUT_MS);
        this.prioritySet = prioritySet;
        final List<P> orderedPriorities = prioritySet.stream().sorted(Comparator.reverseOrder()).toList();
        this.defaultPriority = orderedPriorities.get(orderedPriorities.size() / 2);
        queues = createQueueArray(prioritySet);
    }

    protected AbstractArrayPriorityQueue(final Map<String, Object> properties, final Set<P> prioritySet, final P defaultPriority) {
        this.maxQueueDepth = (long) properties.getOrDefault(MAX_QUEUE_DEPTH_PROPERTY, DEFAULT_MAX_QUEUE_DEPTH);
        this.putBlockTimeout = (long) properties.getOrDefault(MAX_PUT_WAIT_TIME_PROPERTY, DEFAULT_PUT_BLOCK_TIMEOUT_MS);
        this.pollWaitTimeout = (long) properties.getOrDefault(MAX_POLL_WAIT_TIME_PROPERTY, DEFAULT_POLL_WAIT_TIMEOUT_MS);
        this.prioritySet = prioritySet;
        this.defaultPriority = defaultPriority;
        queues = createQueueArray(prioritySet);
    }

    public abstract int getPriorityIndex(final P priority);

    @Override
    public void offer(final M message, final P priority) {
        if (!prioritySet.contains(priority)) {
            throw new QueuePutException("%s is not in the priority set", priority);
        }
        try {

            final long startWaitTime = System.currentTimeMillis(); //start the clock before trying to get the lock
            if (!putLock.isHeldByCurrentThread()
                    && !putLock.tryLock(putBlockTimeout, TimeUnit.MILLISECONDS)) {
                throw new QueuePutException("Could not gain the lock on poll within the timeout period");
            }
            while (maxQueueDepth <= depth()) {
                if (System.currentTimeMillis() - startWaitTime >= putBlockTimeout) {
                    throw new QueuePutException("Put failed after timeout, max queue depth exceeded");
                }
            }
            queues[getPriorityIndex(priority)].offer(message);
        } catch (final InterruptedException e) { //NOSONAR: java:S2142, Throwing wrapped exception
            throw new QueuePutException("Could not gain the lock on offer", e);
        } finally {
            if (putLock.isHeldByCurrentThread()) {
                putLock.unlock();
            }
        }
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
        try {
            if (!pollLock.isHeldByCurrentThread() && !pollLock.tryLock(waitTimeout, TimeUnit.MILLISECONDS)) {
                throw new QueuePollException("Could not gain the lock on poll within the timeout");
            }

            return pollMessage();
        } catch (final InterruptedException e) { //NOSONAR: java:S2142, Throwing wrapped exception
            throw new QueuePollException("Could not gain the lock on poll", e);
        } finally {
            if (pollLock.isHeldByCurrentThread()) {
                pollLock.unlock();
            }
        }
    }

    @Override
    public long depth() {
        long sum = 0;
        for (Queue<M> queue : queues) {
            if (!queue.isEmpty()) {
                sum += queue.size();
            }
        }
        return sum;
    }

    @Override
    public void purge() {
        for (Queue<M> queue : queues) {
            if (!queue.isEmpty()) {
                queue.clear();
            }
        }
    }

    @Override
    public Optional<M> peek() {
        for (int i = queues.length - 1; i >= 0; i--) {
            final Queue<M> queue = queues[i];
            if (!queue.isEmpty()) {
                return Optional.of(queue.peek());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isEmpty() {
        for (Queue<M> queue : queues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static <M, P> Queue<M>[] createQueueArray(final Set<P> prioritySet) {
        @SuppressWarnings("unchecked") final Queue<M>[] queueArray = new Queue[prioritySet.size()];
        for (int i = 0; i < Priority.values().length; i++) {
            queueArray[i] = new LinkedList<>();
        }
        return queueArray;
    }

    private Optional<M> pollMessage() {
        for (int i = queues.length - 1; i >= 0; i--) {
            final Queue<M> queue = queues[i];
            if (!queue.isEmpty()) {
                return Optional.of(queue.poll());
            }
        }
        return Optional.empty();
    }
}
