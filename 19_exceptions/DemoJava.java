public class DemoJava {
    public static void function3() {
        throw new RuntimeException("Boban's Everywhere!");
    }

    public static void function2() {
        function3();
    }

    public static void function1() {
        function2();
    }

    public static void main(String[] args) {
        function1();
    }
}
