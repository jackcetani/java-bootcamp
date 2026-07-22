package bank;

import java.sql.SQLOutput;
import java.util.Scanner;

public class BankService {
    private static final int MAX_CUSTOMERS = 50;
    private static final int MAX_ACCOUNTS = 100;
    private static final int MAX_TRANSACTIONS = 500;

    private final Customer[] customers = new Customer[MAX_CUSTOMERS];
    private final Account[] accounts = new Account[MAX_ACCOUNTS];
    private final Transaction[] transactions = new Transaction[MAX_TRANSACTIONS];

    private int customerCount = 0;
    private int accountCount = 0;
    private int transactionCount = 0;
    private int nextAccountNumber = 10001;
    private int nextTransactionNumber = 1;

    private final Scanner scanner;

    public BankService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void createCustomer() {
        int id = -1;
        String name = "";
        String email = "";
        String phone = "";
        while (true) {
            System.out.print("Enter Customer ID: ");
            String input = scanner.nextLine().trim();
            id = readPositiveInt(input);
            if (id <= 0) {
                continue;
            }
            if (findCustomer(id)) {
                System.out.println("Customer with that ID already exists. Try a different number.");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter customer name: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid name. Try again.");
                continue;
            }
            name = input;
            break;
        }

        while (true) {
            System.out.print("Enter customer email: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid email. Try again.");
                continue;
            }
            email = input;
            break;
        }

        while (true) {
            System.out.print("Enter customer phone number: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid phone number. Try again.");
                continue;
            }
            phone = input;
            break;
        }

        Customer customer = new Customer(id, name, email, phone);
        customers[customerCount++] = customer;
        System.out.println("Customer Created Successfully.");
    }

    public void createSavings() {
        Customer customer = null;
        while (true) {
            System.out.print("Enter Customer ID to create Savings Account: ");
            String input = scanner.nextLine().trim();
            int id = readPositiveInt(input);
            if (id <= 0) {
                continue;
            }
            customer = readExistingCustomer(id);
            if (customer == null) {
                System.out.println("Customer could not be found. Try again.");
                continue;
            }
            break;
        }
        double balance = -1;
        double interestRate= -1;
        while (true) {
            System.out.print("Customer found!\nEnter initial account balance: ");
            String input = scanner.nextLine().trim();
            balance = readPositiveDouble(input);
            if (balance < 0) {
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter interest rate: ");
            String input = scanner.nextLine().trim();
            interestRate = readPositiveDouble(input);
            if (interestRate < 0 || interestRate > 1) {
                System.out.println("Interest rate must be between 0-1. Try again.");
                continue;
            }
            break;
        }

        String accountNumber = String.valueOf(nextAccountNumber++);
        SavingsAccount savings = new SavingsAccount(accountNumber, balance, customer, interestRate);
        accounts[accountCount++] = savings;

        System.out.println("...\nSavings Account Created.");
        savings.displayAccount();
    }

    public void createCurrent() {
        Customer customer = null;
        while (true) {
            System.out.print("Enter Customer ID to create an account: ");
            String input = scanner.nextLine().trim();
            int id = readPositiveInt(input);
            if (id <= 0) {
                continue;
            }
            customer = readExistingCustomer(id);
            if (customer == null) {
                System.out.println("Customer could not be found. Try again.");
                continue;
            }
            break;
        }
        double balance = -1;
        double fee= -1;
        while (true) {
            System.out.print("Customer found!\nEnter initial account balance: ");
            String input = scanner.nextLine().trim();
            balance = readPositiveDouble(input);
            if (balance < 0) {
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Enter transaction fee: ");
            String input = scanner.nextLine().trim();
            fee = readPositiveDouble(input);
            if (fee <= 0) {
                continue;
            }
            break;
        }

        String accountNumber = String.valueOf(nextAccountNumber++);
        CurrentAccount current = new CurrentAccount(accountNumber, balance, customer, fee);
        accounts[accountCount++] = current;

        System.out.println("...\nAccount Created.");
        current.displayAccount();
    }

    public void deposit() {
        Account account = null;
        while (true) {
            System.out.print("Enter account number to deposit: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid account number. Try again.");
                continue;
            }

            account = findAccount(input);
            if (account == null) {
                System.out.println("Account could not be found. Try again.");
                continue;
            }
            break;
        }

        double amount = -1;
        while (true) {
            System.out.print("Enter amount to be deposited: ");
            String input = scanner.nextLine().trim();
            amount = readPositiveDouble(input);
            if (amount <= 0) {
                System.out.println("Deposit rejected: Amount must be positive. Try again.");
                continue;
            }
            break;
        }
        account.deposit(amount);
        System.out.println("...\nDeposit Complete.\nBalance Updated : " + account.getBalance());
    }

    public void withdraw() {
        Account account = null;
        while (true) {
            System.out.print("Enter account number to withdraw: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid account number. Try again.");
                continue;
            }
            account = findAccount(input);
            if (account == null) {
                System.out.println("Account could not be found. Try again.");
                continue;
            }
            break;
        }

        double amount = -1;
        while (true) {
            System.out.print("Enter amount to be withdrawn: ");
            String input = scanner.nextLine().trim();
            amount = readPositiveDouble(input);
            if (amount <= 0 || amount + account.calculateCharges() > account.getBalance()) {
                System.out.println("Withdraw rejected: Amount must be positive and less than balance. Try again.");
                continue;
            }
            break;
        }
        account.withdraw(amount);
        System.out.println("...\nWithdraw complete.");
        if (account.getAccountType().equals("Current")) {
            System.out.println("Transaction Fee : " + account.calculateCharges());
        }

        System.out.println("Balance Updated : " + account.getBalance());
    }

    public void displayAccounts() {
        for (int i = 0; i < accountCount; i++) {
            System.out.println("----------------------------------");
            accounts[i].displayAccount();
        }
        System.out.println("----------------------------------");
    }

    public void displayCustomers() {
        for (int i = 0; i < customerCount; i++) {
            System.out.println("----------------------------------");
            customers[i].printDetails();
        }
        System.out.println("----------------------------------");
    }

    // Returns true if customer with id is found,
    // false otherwise
    private boolean findCustomer(int id) {
        for (int i = 0; i < customerCount; i++) {
            if (id == customers[i].getCustomerId()) {
                return true;
            }
        }
        return false;
    }

    private int readPositiveInt(String input) {
        int id = -1;
        if (input.isEmpty()) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        if (id < 0) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        return id;
    }

    private double readPositiveDouble(String input) {
        double id = -1;
        if (input.isEmpty()) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        try {
            id = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        if (id < 0) {
            System.out.println("Invalid input. Try again.");
            return -1;
        }
        return id;
    }

    private Customer readExistingCustomer(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                return c;
            }
        }
        return null;
    }

    private Account findAccount(String number) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(number)) {
                return accounts[i];
            }
        }
        return null;
    }

}