package bank;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.045;

    public SavingsAccount(String accountNumber, Customer owner) {
        super(accountNumber, owner);
    }

    @Override
    public void calculateInterest() {
        double interest = balance * (INTEREST_RATE / 12);
        deposit(interest);
    }
}
