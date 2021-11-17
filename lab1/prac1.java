public class prac1 {
    public static int j = 0;
    public static void flip(int n) {
        if (n <= 1) {
            j ++;
            return;
        }
        int stop = n/2;

        for (int i = 1; i <= n; i ++) {
            j ++;
            if (i == stop) {
                flop(i, n);
                return;
            }

        }

    }


    public static void flop(int i, int n) {
        int m = Math.min(i, n - i);
        flip(m);
        flip(m);

    }

    public static void main(String[] args) {
        flip(20);
        System.out.println(j);
    }

}
