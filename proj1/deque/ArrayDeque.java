package deque;

/** Array based deque. */
public class ArrayDeque<Bibimbap> {
    private Bibimbap[] items;
    private int size;
    private int first;
    private int last;

    public ArrayDeque() {
        items = (Bibimbap []) new Object[8];
        size = 0;
        first = 3;
        last = 3;

    }

    private void resize(int cap) {
        Bibimbap[] a = (Bibimbap []) new Object[cap];
        for (int i = 0; i < size; i ++) {
            a[3+i] = get(i);
        }

        items = a;
        first = 3;
        last = first + size - 1;
    }

    /** Adds x to the front of the list. */
    public void addFirst(Bibimbap x) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (items[first] == null) {
        } else if (first > 0) {
            first -= 1;
        } else if (first == 0) {
            first = items.length-1;
        }
        items[first] = x;
        size += 1;
    }

    /** Adds x to the end of the list. */
    public void addLast(Bibimbap x) {
        if (size == items.length) {
            resize(size * 2);
        }
        if (items[last] == null) {
        } else if (last < items.length - 1) {
            last += 1;
        } else if (last == items.length - 1) {
            last = 0;
        }
        items[last] = x;
        size += 1;
    }

    /** determine if deque is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** return the size of the list. */
    public int size() {
        return size;
    }

    /** Removes and returns the item at the front of the deque. */
    public Bibimbap removeFirst() {

        if (size == 0) {
            return null;
        }
        Bibimbap x = items[first];
        items[first] = null;
        size -= 1;
        if (items.length > 8 & size < items.length/4) {
            resize(items.length/2);
        } else if (last == first) {

        } else if (first < items.length - 1) {
            first += 1;
        } else if (first == items.length - 1) {
            first = 0;
        }
        return x;
    }

    /** Removes and returns the item at the back of the deque. */
    public Bibimbap removeLast() {
        if (size == 0) {
            return null;
        }
        Bibimbap x = items[last];
        items[last] = null;
        size -= 1;
        if (items.length > 8 & size < items.length/4) {
            resize(items.length/2);
        } else if (last == first) {

        } else if (last > 0) {
            last -= 1;
        } else if (last == 0) {
            last = items.length - 1;
        }

        return x;
    }


    /** Gets the item at the given index, where 0 is the front, 1 is the next item. */
    public Bibimbap get(int index) {
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
    public void printDeque() {
        int x = 0;
        while (x < size) {
            Bibimbap y = get(x);
            System.out.print(y + " ");
            x ++;
        }
        System.out.println();
    }
}
