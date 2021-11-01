package deque;

import java.util.Iterator;

/** Circular Doubly Linked List. */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node {
        private T item;
        private Node next;
        private Node prev;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    /** create an empty LinkedListDeque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel.prev = sentinel;
        size = 0;
    }

    /** Adds x to the front of the list. */
    @Override
    public void addFirst(T x) {
        sentinel.next = new Node(x, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    /** Adds x to the end of the list. */
    @Override
    public void addLast(T x) {
        sentinel.prev = new Node(x, sentinel.prev, sentinel);
        sentinel.prev.prev.next =  sentinel.prev;
        size += 1;
    }


    /** return the size of the list. */
    @Override
    public int size() {
        return size;
    }

    /** Removes and returns the item at the front of the deque. */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T x =  sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    /** Removes and returns the item at the back of the deque. */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T x = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return x;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item. */
    @Override
    public T get(int index) {
        if (size == 0 || index > size - 1) {
            return null;
        }
        int x = 0;
        Node current = sentinel;
        while (current.next != sentinel && x <= index) {
            current = current.next;
            x++;
        }
        return current.item;
    }

    /** Same as get, but uses recursion. */
    public T getRecursive(int index) {
        if (size == 0 || index > size - 1) {
            return null;
        }
        return getRecursiveHelp(index).item;
    }

    private Node getRecursiveHelp(int index) {

        if (index == 0) {
            return sentinel.next;
        }
        if (index > 0) {
            return getRecursiveHelp(index - 1).next;
        }
        return null;
    }


    /** Prints the items in the deque from first to last, separated by a space. */
    @Override
    public void printDeque() {
        Node current = sentinel;
        while (current.next != sentinel) {
            current = current.next;
            System.out.print(current.item + " ");
        }
        System.out.println();
    }


    /** The Deque objects we’ll make are iterable. */
    public Iterator<T> iterator() {
        return new LinkedListDeque.LLDIterator();
    }

    private class LLDIterator implements Iterator<T> {
        private int wizPos;

        LLDIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        /* do they refer to the same object? */
        if (this == o) {
            return true;
        }

        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;

        if (this.size() != other.size()) {
            return false;
        }

        for (int index = 0; index < size; index++) {
            if (this.get(index) != other.get(index)) {
                return false;
            }
        }
        return true;
    }

}
