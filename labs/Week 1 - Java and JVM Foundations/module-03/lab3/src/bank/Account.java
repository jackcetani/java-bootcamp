package bank;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private Customer customer;

    protected Account(String accountNumber, double balance, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customer = customer;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public Customer getCustomer() { return customer; }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit rejected: amount must be positive.");
            return;
        }
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount + calculateCharges() > balance) {
            System.out.println("Withdrawl rejected.");
            return false;
        }
        balance -= amount + calculateCharges();
        return true;
    }

    abstract void displayAccount();

    double calculateCharges() { return 0.0; }
    double calculateInterest() { return 0.0; }
    String getAccountType() { return "Account"; }


}