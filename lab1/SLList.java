public class SLList {
    Node sentinel;

    public SLList() {
        this.sentinel = new Node();
    }

    private static class Node {
        int item;
        Node next;
    }

    public int findFirst(int n) {
        Node i = this.sentinel;
        int index = 0;
        int j = -1;
        while (i.next != null && j == -1) {
            j = findFirstHelper(n, index, i);
            index ++;
            i = i.next;
        }

        if (j == -1) {
            if (i.item == n) {
                j = index;
            }
        }
        return j;

    }

    private int findFirstHelper(int n, int index, Node curr) {
        if (curr == null) {
            return -1;
        }

        if (curr.item == n) {
            return index;
        } else {
            return -1;
        }
    }

}
