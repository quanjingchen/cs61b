package deque;

import gh2.GuitarString;

public class AGtest {
    public static void main(String[] args) {
        /* create a deque linked list */
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>(3);
        lld1.addFirst(2);
        lld1.addFirst(1);

        /* create a deque linked list */
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>(3);
        lld2.addFirst(2);
        lld2.addFirst(1);

        lld1.equals(lld2);


    }
}
