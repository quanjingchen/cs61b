public class Puzzle {
    public static void main(String[] args) {
        A y = new B();
        B z = new B();
        int x1 = y.fish(y);
        int x2 = y.fish(z);
        int x3 = z.fish(y);
        int x4 = z.fish((A)z);
        int x5 = y.fish((B)y);
        int x6 = ((A)z).fish(z);

        System.out.println(y.fish(y));
        System.out.println(y.fish(z));
        System.out.println(z.fish(y));
        System.out.println(z.fish(z));


    }
}
