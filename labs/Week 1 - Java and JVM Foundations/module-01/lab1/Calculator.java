public class Calculator {
    public static void main(String[] args) {
        int sum = add(10, 20);
        System.out.println("Sum = " + sum);
    }

    private static int add(int a, int b) {
        return a + b;
    }
}