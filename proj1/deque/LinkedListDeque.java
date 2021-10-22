package deque;

/** Circular Doubly Linked List. */
public class LinkedListDeque<Bibimbap> {

    private class Node {
        public Bibimbap item;
        public Node next;
        public Node prev;

        public Node(Bibimbap i, Node p, Node n) {
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

    public LinkedListDeque(Bibimbap x) {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel.next = new Node(x, sentinel, sentinel);
        size = 1;
    }

    /** Adds x to the front of the list. */
    public void addFirst(Bibimbap x) {
        sentinel.next = new Node(x, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    /** Adds x to the end of the list. */
    public void addLast(Bibimbap x) {
        sentinel.prev = new Node(x, sentinel.prev, sentinel);
        sentinel.prev.prev.next =  sentinel.prev;
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
        Bibimbap x =  get(0);
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    /** Removes and returns the item at the back of the deque. */
    public Bibimbap removeLast() {
        if (size == 0) {
            return null;
        }
        Bibimbap x =  get(size-1);
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return x;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item. */
    public Bibimbap get(int index) {
        if (size == 0 || index > size - 1) {
            return null;
        }
        int x = 0;
        Node current = sentinel;
        while (current.next != sentinel && x <= index) {
            current = current.next;
            x ++;
        }
        return current.item;
    }

    /** Prints the items in the deque from first to last, separated by a space. */
    public void printDeque() {
        Node current = sentinel;
        while (current.next != sentinel) {
            current = current.next;
            System.out.print(current.item + " ");
        }
        System.out.println();
    }








}
