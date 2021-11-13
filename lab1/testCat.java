public class testCat {
    public static void main(String[] args) {
        Animal a = new Cat();
        Cat c = new Cat();
        a.greet(c);
        a.sniff(c);
        a.sniff(a);
        c.praise(c);
        c.praise(a);
        a.praise(c);
        a.praise(a);
    }
}
