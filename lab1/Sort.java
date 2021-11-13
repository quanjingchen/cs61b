public class Sort {
    /**test the sort class.*/

    public static void sort(String[] x) {
        sort(x,0);
    }

    private static void sort(String[] x, int start) {
        if (start == x.length) {
            return;
        }

        int smallestIndex = findSmallest(x,start);
        swap(x,smallestIndex,start);
        sort(x,start + 1);
    }


    /**return the index of the smallest String in x, starting at start.*/
    public static int findSmallest(String[] x, int start) {
        int smallestIndex = start;
        for (int i = start; i < x.length; i += 1) {
            int cmp = x[i].compareTo(x[smallestIndex]);
            if (cmp < 0) {
                smallestIndex = i;
            }
        }
        return smallestIndex;

    }

    public static void swap(String[] x, int a, int b) {
        String tmp = x[a];
        x[a] = x[b];
        x[b] = tmp;
    }



}