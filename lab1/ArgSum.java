public class ArgSum {
    public static void main(String[] args) {
        int len = args.length;
        int i = 0;
        int sum = 0;
        while (i < len) {
            int newint = Integer.parseInt(args[i]);
            sum = sum + newint;
            i = i + 1;

        }
        System.out.println(sum + "");
    }
}
