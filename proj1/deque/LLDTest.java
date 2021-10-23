package deque;

public class LLDTest {
    public static void main (String[] args) {
        LinkedListDeque<String> a = new LinkedListDeque<String>();
        a.addLast("a");
        a.addLast("b");
        a.addLast("c");

        ArrayDeque<String> o = new ArrayDeque<String>();
        o.addLast("a");
        o.addLast("b");
        o.addLast("c");

        /*boolean b = o.equals(a);

        Integer a1 = a.getRecursive(5);
        Integer a2 = a.removeFirst();
        for (int x : a) {
            System.out.print(x);
        }*/

        /*a.printDeque();*/
        MaxArrayDeque.NameComparator comp = new MaxArrayDeque.NameComparator();
        MaxArrayDeque<Integer> c = new MaxArrayDeque<Integer>(comp);
        c.addLast(122);
        c.addLast(2);
        c.addLast(3);
        Integer c_max= c.max();


    }
}
