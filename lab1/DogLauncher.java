public class DogLauncher {
    public static void main(String[] args) {
        Dog d1 = new Dog(28);
        Dog d2 = new Dog(18);
        Dog bigger = Dog.maxDog(d1,d2);
        bigger.makeNoise();
        Dog larger = d1.biggerDog(d2);
        larger.makeNoise();
        System.out.println(d1.binomen);
    }
}
