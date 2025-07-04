package bank;

public class CurrentAccount extends Account {
    private static final double INTEREST_RATE = 0.01;

    public CurrentAccount(String accountNumber, Customer owner) {
        super(accountNumber, owner);
    }

    @Override
    public void calculateInterest() {
        double interest = balance * (INTEREST_RATE / 12);
        deposit(interest);
    }
}

