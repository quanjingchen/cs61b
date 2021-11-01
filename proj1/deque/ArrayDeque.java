package deque;

import java.util.Iterator;

/** Array based deque. */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int first;
    private int last;

    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
        first = 3;
        last = 3;

    }

    private void resize(int cap) {
        T[] a = (T []) new Object[cap];
        for (int i = 0; i < size; i++) {
            a[3 + i] = get(i);
        }

        items = a;
        first = 3;
        last = first + size - 1;
    }

    /** Adds x to the front of the list. */
    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (items[first] != null & first > 0) {
            first -= 1;
        } else if (items[first] != null & first == 0) {
            first = items.length - 1;
        }
        items[first] = x;
        size += 1;
    }

    /** Adds x to the end of the list. */
    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resize(size * 2);
        }
        if (items[last] != null & last < items.length - 1) {
            last += 1;
        } else if (items[last] != null & last == items.length - 1) {
            last = 0;
        }
        items[last] = x;
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
        T x = items[first];
        items[first] = null;
        size -= 1;
        if (items.length > 8 & size < items.length / 4) {
            first += 1;
            resize(items.length / 2);
        } else if (last == first) {
            return x;
        } else if (first < items.length - 1) {
            first += 1;
        } else if (first == items.length - 1) {
            first = 0;
        }
        return x;
    }

    /** Removes and returns the item at the back of the deque. */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T x = items[last];
        items[last] = null;
        size -= 1;
        if (items.length > 8 & size < items.length / 4) {
            resize(items.length / 2);
        } else if (last != first & last > 0) {
            last -= 1;
        } else if (last != first & last == 0) {
            last = items.length - 1;
        }

        return x;
    }


    /** Gets the item at the given index, where 0 is the front, 1 is the next item. */
    @Override
    public T get(int index) {
        if (size == 0 || (index > size - 1)) {
            return null;
        }

        if (first + index <= items.length - 1) {
            return items[first + index];
        } else {
            return items[(first + index) % items.length];
        }
    }

    /** Prints the items in the deque from first to last, separated by a space. */
    @Override
    public void printDeque() {
        int x = 0;
        while (x < size) {
            T y = get(x);
            System.out.print(y + " ");
            x++;
        }
        System.out.println();
    }

    /** The Deque objects we’ll make are iterable. */
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
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
