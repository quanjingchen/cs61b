package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  public static void main(String[] args) {

      randomizedTest();
  }
    public static void testThreeAddThreeRemove() {
        AListNoResizing<Integer> A = new AListNoResizing();
        A.addLast(4);
        A.addLast(5);
        A.addLast(6);

        BuggyAList<Integer> B = new BuggyAList();
        B.addLast(4);
        B.addLast(5);
        B.addLast(6);

        assertEquals(A.removeLast(), B.removeLast());
        assertEquals(A.removeLast(), B.removeLast());
        assertEquals(A.removeLast(), B.removeLast());
    }

    public static void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> A = new BuggyAList();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                A.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), A.size());
            } else if (operationNumber == 2 && L.size() > 0) {
                // getLast
                assertEquals(L.getLast(), A.getLast());

            } else if (operationNumber == 3 && L.size() > 0) {
                // removeLast
                assertEquals(L.removeLast(), A.removeLast());
            }
        }
    }


}
