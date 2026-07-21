package com.academy.student;

import java.util.Scanner;

public class StudentManager {
    private static final int MAX_STUDENTS = 20;

    private final Student[] students = new Student[MAX_STUDENTS];
    private int studentCount = 0;
    private final Scanner scanner;

    public StudentManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("===============================");
        System.out.println("Student Management System");
        System.out.println("===============================");
        System.out.println("1. Add Student");
        System.out.println("2. Display Students");
        System.out.println("3. Search Student");
        System.out.println("4. Average Marks");
        System.out.println("5. Exit");
        System.out.print("Enter Choice : ");
    }

    public void addStudent() {
        if (studentCount >= MAX_STUDENTS) {
            System.out.println("Max number of students reached!");
            return;
        }

        int id = 0;
        String name = "";
        String course = "";
        double marks = 0;

        boolean valid = false;
        while (!valid) {
            System.out.println("Enter student ID : ");
            String input = scanner.nextLine().trim();
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid Input");
                System.out.println("Enter a positive integer.");
                continue;
            }

            if (id <= 0) {
                System.out.println("Invalid Input");
                System.out.println("Enter a positive integer.");
                continue;
            }

            valid = true;
        }

        valid = false;
        while (!valid) {
            System.out.println("Enter Student Name : ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Invalid Input");
                System.out.println("Please Try Again.");
                continue;
            }

            name = input;
            valid = true;
        }

        valid = false;
        while (!valid) {
            System.out.println("Enter Course Name : ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Invalid Input");
                System.out.println("Please Try Again.");
                continue;
            }

            course = input;
            valid = true;
        }

        valid = false;
        while (!valid) {
            System.out.println("Enter student marks : ");
            String input = scanner.nextLine().trim();
            try {
                marks = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid Input");
                System.out.println("Enter a number 0 - 100.");
                continue;
            }

            if (marks < 0 || marks > 100) {
                System.out.println("Invalid Input");
                System.out.println("Enter a number 0 - 100.");
                continue;
            }

            valid = true;
        }

        students[studentCount] = new Student(id, name, course, marks);
        studentCount++;
        System.out.println("Student Added Successfully.");
    }

    public void displayStudents() {
        for (int i = 0; i < studentCount; i++) {
            System.out.println("-------------------------------------------------------------");
            System.out.printf("%-8d %-20s %-15s %-8.2f%n",
                    students[i].getStudentId(),
                    students[i].getName(),
                    students[i].getCourse(),
                    students[i].getMarks());
        }
        System.out.println("-------------------------------------------------------------");
    }

    public void searchStudent() {
        if (studentCount == 0) {
            System.out.println("No students to search.");
        }
        int id = 0;
        while (true) {
            System.out.println("Enter student ID : ");
            String input = scanner.nextLine().trim();
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid Input");
                System.out.println("Enter a positive integer.");
                continue;
            }

            if (id <= 0) {
                System.out.println("Invalid Input");
                System.out.println("Enter a positive integer.");
                continue;
            }
            break;
        }
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId() == id) {
                students[i].display();
                return;
            }
        }
        System.out.println("Student Not Found.");
    }

    public void calculateAverage() {
        if (studentCount == 0) {
            System.out.println("No students available.");
            return;
        }
        double sum = 0;
        for (int i = 0; i < studentCount; i++) {
            sum += students[i].getMarks();
        }

        double avg = sum / studentCount;
        System.out.printf("Average Marks : %.2f%n", avg);
    }
}