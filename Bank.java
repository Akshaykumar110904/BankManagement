package bank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bank {
    private List<Account> accounts;
    private ScheduledExecutorService scheduler;
    private static final String TRANSACTIONS_FILE = "transactions_data.txt";
    private static final String ACCOUNTS_FILE = "accounts_data.txt";
    private static final String CUSTOMERS_FILE = "customers_data.txt";
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Bank() {
        accounts = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        loadAllData();
        startInterestCalculation();
    }

    // Load all data from files
    private void loadAllData() {
        loadCustomersAndAccounts();
        loadTransactions();
    }

    // Save customer data
    private void saveCustomerData(Customer customer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            writer.println("=== CUSTOMER ===");
            writer.println("ID: " + customer.getId());
            writer.println("Name: " + customer.getName());
            writer.println("Address: " + customer.getAddress());
            writer.println("Phone: " + customer.getPhone());
            writer.println("=== END CUSTOMER ===\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save account data
    private void saveAccountData(Account account) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            writer.println("=== ACCOUNT ===");
            writer.println("Number: " + account.getAccountNumber());
            writer.println("Type: " + account.getClass().getSimpleName());
            writer.println("Balance: " + account.getBalance());
            writer.println("CustomerID: " + account.getOwner().getId());
            writer.println("=== END ACCOUNT ===\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save transaction data
    private void saveTransactionData(String accountNumber, Transaction transaction) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            writer.println("=== TRANSACTION ===");
            writer.println("AccountNumber: " + accountNumber);
            writer.println("Timestamp: " + transaction.getTimestamp().format(DATE_FORMATTER));
            writer.println("Type: " + transaction.getType());
            writer.println("Amount: " + transaction.getAmount());
            writer.println("BalanceAfter: " + transaction.getBalanceAfter());
            writer.println("=== END TRANSACTION ===\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load customers and accounts
    private void loadCustomersAndAccounts() {
        List<Customer> customers = new ArrayList<>();
        
        // First load all customers
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            Customer currentCustomer = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("=== CUSTOMER ===")) {
                    currentCustomer = null;
                    continue;
                }
                if (line.startsWith("ID: ")) {
                    String id = line.substring(4);
                    String name = reader.readLine().substring(6);
                    String address = reader.readLine().substring(9);
                    String phone = reader.readLine().substring(7);
                    currentCustomer = new Customer(id, name, address, phone);
                    customers.add(currentCustomer);
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine for first run
        }

        // Then load accounts and link them to customers
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("=== ACCOUNT ===")) {
                    String number = reader.readLine().substring(8);
                    String type = reader.readLine().substring(6);
                    double balance = Double.parseDouble(reader.readLine().substring(9));
                    String customerId = reader.readLine().substring(12);
                    
                    // Find the corresponding customer
                    Customer owner = customers.stream()
                        .filter(c -> c.getId().equals(customerId))
                        .findFirst()
                        .orElse(null);
                    
                    if (owner != null) {
                        Account account = type.equals("SavingsAccount") ?
                            new SavingsAccount(number, owner) :
                            new CurrentAccount(number, owner);
                        account.balance = balance;
                        accounts.add(account);
                    }
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine for first run
        }
    }

    // Load transactions with fixed lambda issue
    private void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("=== TRANSACTION ===")) {
                    // Store the account number in a final variable
                    final String accountNumber = reader.readLine().substring(14);
                    LocalDateTime timestamp = LocalDateTime.parse(reader.readLine().substring(11), DATE_FORMATTER);
                    String type = reader.readLine().substring(6);
                    double amount = Double.parseDouble(reader.readLine().substring(8));
                    double balanceAfter = Double.parseDouble(reader.readLine().substring(14));
                    
                    // Use the final accountNumber variable in the lambda
                    accounts.stream()
                        .filter(a -> a.getAccountNumber().equals(accountNumber))
                        .findFirst()
                        .ifPresent(account -> {
                            Transaction transaction = new Transaction(type, amount, balanceAfter, timestamp);
                            account.transactions.add(transaction);
                        });
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine for first run
        }
    }

    public Account createAccount(String type, Customer customer) {
        // Save customer data first
        saveCustomerData(customer);
        
        // Create and save account
        String accountNumber = generateAccountNumber();
        Account account = type.equalsIgnoreCase("savings") ?
            new SavingsAccount(accountNumber, customer) :
            new CurrentAccount(accountNumber, customer);
        accounts.add(account);
        saveAccountData(account);
        
        return account;
    }

    public synchronized boolean deposit(String accountNumber, double amount) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        if (account.deposit(amount)) {
            // Save the transaction
            Transaction transaction = account.getTransactions().get(account.getTransactions().size() - 1);
            saveTransactionData(accountNumber, transaction);
            // Update account data
            updateAccountData(account);
            return true;
        }
        return false;
    }

    public synchronized boolean withdraw(String accountNumber, double amount) 
            throws AccountNotFoundException, InsufficientFundsException {
        Account account = findAccount(accountNumber);
        if (account.withdraw(amount)) {
            // Save the transaction
            Transaction transaction = account.getTransactions().get(account.getTransactions().size() - 1);
            saveTransactionData(accountNumber, transaction);
            // Update account data
            updateAccountData(account);
            return true;
        }
        return false;
    }

    private void updateAccountData(Account account) {
        // Create temporary list of all accounts except the one being updated
        List<String> updatedAccounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            StringBuilder currentAccount = new StringBuilder();
            boolean skip = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("=== ACCOUNT ===")) {
                    currentAccount = new StringBuilder();
                    currentAccount.append(line).append("\n");
                } else if (line.equals("=== END ACCOUNT ===")) {
                    currentAccount.append(line).append("\n");
                    if (!skip) {
                        updatedAccounts.add(currentAccount.toString());
                    }
                    skip = false;
                } else if (line.startsWith("Number: ")) {
                    skip = line.substring(8).equals(account.getAccountNumber());
                    currentAccount.append(line).append("\n");
                } else {
                    currentAccount.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write updated account data
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (String acc : updatedAccounts) {
                writer.print(acc);
            }
            writer.println("=== ACCOUNT ===");
            writer.println("Number: " + account.getAccountNumber());
            writer.println("Type: " + account.getClass().getSimpleName());
            writer.println("Balance: " + account.getBalance());
            writer.println("CustomerID: " + account.getOwner().getId());
            writer.println("=== END ACCOUNT ===\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        return accounts.stream()
            .filter(a -> a.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    private void startInterestCalculation() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Account account : accounts) {
                account.calculateInterest();
                // Save interest transaction and update account data
                if (!account.getTransactions().isEmpty()) {
                    Transaction transaction = account.getTransactions().get(account.getTransactions().size() - 1);
                    saveTransactionData(account.getAccountNumber(), transaction);
                    updateAccountData(account);
                }
            }
        }, 1, 1, TimeUnit.DAYS);
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }

    public void generateStatement(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        String filename = "statement_" + accountNumber + "_" + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Account Statement ===");
            writer.println("Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("Account Number: " + accountNumber);
            writer.println("Account Type: " + account.getClass().getSimpleName());
            writer.println("Customer Name: " + account.getOwner().getName());
            writer.println("Customer ID: " + account.getOwner().getId());
            writer.println("Current Balance: $" + String.format("%.2f", account.getBalance()));
            writer.println("\nTransaction History:");
            writer.println("----------------------------------------");
            
            for (Transaction t : account.getTransactions()) {
                writer.printf("%s | %s | $%.2f | Balance: $%.2f%n",
                    t.getTimestamp().format(DATE_FORMATTER),
                    t.getType(),
                    Math.abs(t.getAmount()),
                    t.getBalanceAfter());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}