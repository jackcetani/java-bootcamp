public class InheritanceDemo {
    public static void main(String[] args) {
        // Base-type array can hold either subclass.
        Account[] accounts = {
                new SavingsAccount(100.00),
                new CurrentAccount(100.00),
                new FrozenAccount(100.00)
        };

        for (Account account : accounts) {
            // Runtime type chooses the overridden withdraw/getAccountType.
            boolean ok = account.withdraw(20.00);
            System.out.printf("%s withdraw=%s balance=%.2f%n",
                    account.getAccountType(), ok, account.getBalance());
        }
    }
}