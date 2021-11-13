public class IntList {
    public int first;
    public IntList rest;

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /** Return the size of the list using... recursion! */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + this.rest.size();
    }

    /** Return the size of the list using no recursion! */
    public int iterativeSize() {
        IntList p = this;
        int totalSize = 0;
        while (p != null) {
            totalSize += 1;
            p = p.rest;
        }
        return totalSize;
    }

    public int get(int w) {
        IntList p = this;
        int i= 0;
        while (i < w) {
            p = p.rest;
            i ++;
        }
        return p.first;
    }


    public static IntList dincrList(IntList L, int x) {
        IntList p = L;
        while (p != null) {
            p.first += x;
            p = p.rest;
        }
        return L;
    }

    public static IntList incrList(IntList L, int x) {
        IntList M = new IntList(L.first , L.rest);
        return dincrList(M,x);
    }


    public static void evenOdd(IntList lst) {

        if (lst == null || lst.rest == null) {
            return;
        }
        IntList evenList = lst;
        IntList oddList = lst.rest;
        IntList second = lst.rest;
        while (evenList.rest != null && oddList.rest != null) {
            evenList.rest = evenList.rest.rest;
            oddList.rest = oddList.rest.rest;
            evenList = evenList.rest;
            oddList = oddList.rest;
        }

        evenList.rest = second;
    }

    public static void main(String[] args) {
        IntList L = new IntList(3, null);
        L = new IntList(6, L);
        L = new IntList(2, L);
        L = new IntList(5, L);
        L = new IntList(1, L);
        L = new IntList(4, L);
        L = new IntList(0, L);

        evenOdd(L);


    }
}
