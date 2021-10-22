package deque;

public class LLDTest {
    public static void main (String[] args) {
        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        a.addLast(0);
        a.addLast(1);
        a.addLast(2);
        a.addLast(3);


        Integer a1 = a.get(12);
        Integer a2 = a.removeFirst();
        Integer a3 = a.removeLast();
        Integer a4 = a.removeLast();
        Integer a5 = a.removeLast();
        Integer a6 = a.removeLast();
        Integer a7 = a.removeLast();

        a.printDeque();
    }
}
