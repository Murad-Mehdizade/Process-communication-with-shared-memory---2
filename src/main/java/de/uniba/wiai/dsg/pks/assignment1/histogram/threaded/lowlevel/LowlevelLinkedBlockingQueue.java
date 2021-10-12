package de.uniba.wiai.dsg.pks.assignment1.histogram.threaded.lowlevel;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@ThreadSafe
public class LowlevelLinkedBlockingQueue<T> extends LinkedList<T> implements Queue<T> {

    private static final long serialVersionUID = 5576153232405161849L;

    @GuardedBy(value = "this")
    public void put(T element) throws InterruptedException {
        synchronized (this) {
            while (!offer(element)) {
                wait();
            }

        }
    }

    @Override
    @GuardedBy(value = "this")
    public boolean add(T o) {
        synchronized (this) {
            addLast(o);
            notify();
        }
        return true;
    }

    @Override
    @GuardedBy(value = "this")
    public T peek() {
        try {
            return element();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    @GuardedBy(value = "this")
    public T element() {
        T object;
        synchronized (this) {
            object = getFirst();
            notify();
        }
        return object;
    }

    @Override
    @GuardedBy(value = "this")
    public T poll() {
        try {
            return remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    @GuardedBy(value = "this")
    public T remove() {
        T object;
        synchronized (this) {
            object = removeFirst();
            notify();
        }
        return object;
    }

    @Override
    @GuardedBy(value = "this")
    public boolean offer(T o) {
        try {
            return add(o);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @GuardedBy(value = "this")
    public T take() throws InterruptedException {
        synchronized (this) {
            while (isEmpty()) {
                wait();
            }
            return remove();
        }

    }
}
