package bank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {
    protected String accountNumber;
    protected double balance;
    protected Customer owner;
    protected List<Transaction> transactions;

    public Account(String accountNumber, Customer owner) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public synchronized boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction("Deposit", amount, balance));
            return true;
        }
        return false;
    }

    public synchronized boolean withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        if (amount > 0) {
            balance -= amount;
            transactions.add(new Transaction("Withdrawal", -amount, balance));
            return true;
        }
        return false;
    }

    public abstract void calculateInterest();

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public Customer getOwner() {
        return owner;
    }
}
