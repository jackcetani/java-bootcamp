public class Employee {
    int id;
    String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void display() {
        System.out.println(this.id + " - " + this.name);
    }

    public static void main(String[] args) {
        Employee e = new Employee(101, "Aman");
        e.display();
    }




}