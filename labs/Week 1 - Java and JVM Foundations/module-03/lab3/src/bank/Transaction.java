package bank;

public class Transaction {
    private int transactionId;
    private double amount;
    private String type;
    private String date;
    private int accountNumber;

    public Transaction(int transactionId, double amount, String type, String date, int accountNumber) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.accountNumber = accountNumber;
    }

    public int getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public int getAccountNumber() { return accountNumber; }

    public void display() {
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("%s transaction on date: %s%n", type, date);
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Amount: " + amount);
    }
}