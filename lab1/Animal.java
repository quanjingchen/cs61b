public interface Animal {
    default void greet(Animal a) {
        System.out.println("hello animal");
    }
    default void sniff(Animal a) {
        System.out.println("sniff animal");
    }
    default void praise(Animal a) {
        System.out.println("cool animal");
    }
}
