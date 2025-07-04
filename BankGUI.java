package bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BankGUI extends JFrame {
    private Bank bank;
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea outputArea;

    public BankGUI() {
        bank = new Bank();
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Top Panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 5, 10),
            new TitledBorder("Account Operations")
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Account Number:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        accountNumberField = new JTextField(20);
        topPanel.add(accountNumberField, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        amountField = new JTextField(20);
        topPanel.add(amountField, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new CompoundBorder(
            new EmptyBorder(5, 10, 5, 10),
            new TitledBorder("Account Management")
        ));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        // Add buttons in a column
        addButton(centerPanel, "Create Savings Account", this::createSavingsAccount, gbc, 0);
        addButton(centerPanel, "Create Current Account", this::createCurrentAccount, gbc, 1);
        addButton(centerPanel, "Deposit", this::deposit, gbc, 2);
        addButton(centerPanel, "Withdraw", this::withdraw, gbc, 3);
        addButton(centerPanel, "Check Balance", this::checkBalance, gbc, 4);
        addButton(centerPanel, "Generate Statement", this::generateStatement, gbc, 5);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(centerPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Output Area
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new CompoundBorder(
            new EmptyBorder(5, 10, 10, 10),
            new TitledBorder("Transaction Output")
        ));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel, String label, Runnable action, 
                         GridBagConstraints gbc, int gridy) {
        gbc.gridy = gridy;
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(e -> action.run());
        panel.add(button, gbc);
    }

    private void createSavingsAccount() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name != null && !name.trim().isEmpty()) {
            String address = JOptionPane.showInputDialog(this, "Enter address:");
            String phone = JOptionPane.showInputDialog(this, "Enter phone:");
            
            Customer customer = new Customer(
                "CUS" + System.currentTimeMillis(),
                name, address, phone
            );
            
            Account account = bank.createAccount("savings", customer);
            JOptionPane.showMessageDialog(this, 
                "Savings Account Created Successfully!\n\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Customer Name: " + customer.getName() + "\n" +
                "Customer ID: " + customer.getId(),
                "Account Created",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            outputArea.setText(String.format(
                "Created Savings Account\n" +
                "Account Number: %s\n" +
                "Customer Name: %s\n" +
                "Customer ID: %s\n" +
                "Initial Balance: $%.2f",
                account.getAccountNumber(),
                customer.getName(),
                customer.getId(),
                account.getBalance()
            ));
        }
    }

    private void createCurrentAccount() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name != null && !name.trim().isEmpty()) {
            String address = JOptionPane.showInputDialog(this, "Enter address:");
            String phone = JOptionPane.showInputDialog(this, "Enter phone:");
            
            Customer customer = new Customer(
                "CUS" + System.currentTimeMillis(),
                name, address, phone
            );
            
            Account account = bank.createAccount("current", customer);
            JOptionPane.showMessageDialog(this, 
                "Current Account Created Successfully!\n\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Customer Name: " + customer.getName() + "\n" +
                "Customer ID: " + customer.getId(),
                "Account Created",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            outputArea.setText(String.format(
                "Created Current Account\n" +
                "Account Number: %s\n" +
                "Customer Name: %s\n" +
                "Customer ID: %s\n" +
                "Initial Balance: $%.2f",
                account.getAccountNumber(),
                customer.getName(),
                customer.getId(),
                account.getBalance()
            ));
        }
    }

    private void deposit() {
        try {
            Account account = bank.findAccount(accountNumberField.getText());
            double amount = Double.parseDouble(amountField.getText());
            if (account.deposit(amount)) {
                outputArea.setText(String.format(
                    "Deposit Successful\n" +
                    "Amount: $%.2f\n" +
                    "New Balance: $%.2f",
                    amount,
                    account.getBalance()
                ));
                JOptionPane.showMessageDialog(this, 
                    String.format("Successfully deposited $%.2f\nNew balance: $%.2f", 
                                amount, account.getBalance()),
                    "Deposit Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid deposit amount",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void withdraw() {
        try {
            Account account = bank.findAccount(accountNumberField.getText());
            double amount = Double.parseDouble(amountField.getText());
            if (account.withdraw(amount)) {
                outputArea.setText(String.format(
                    "Withdrawal Successful\n" +
                    "Amount: $%.2f\n" +
                    "New Balance: $%.2f",
                    amount,
                    account.getBalance()
                ));
                JOptionPane.showMessageDialog(this, 
                    String.format("Successfully withdrew $%.2f\nNew balance: $%.2f", 
                                amount, account.getBalance()),
                    "Withdrawal Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Insufficient funds or invalid withdrawal amount",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void checkBalance() {
        try {
            Account account = bank.findAccount(accountNumberField.getText());
            Customer owner = account.getOwner();
            outputArea.setText(String.format(
                "Account Balance\n" +
                "Account Number: %s\n" +
                "Account Type: %s\n" +
                "Customer Name: %s\n" +
                "Current Balance: $%.2f",
                account.getAccountNumber(),
                account.getClass().getSimpleName(),
                owner.getName(),
                account.getBalance()
            ));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void generateStatement() {
        try {
            String accountNumber = accountNumberField.getText();
            bank.generateStatement(accountNumber);
            outputArea.setText(
                "Statement generated successfully.\n" +
                "File saved as: statement_" + accountNumber + "_[timestamp].txt"
            );
            JOptionPane.showMessageDialog(this, 
                "Statement generated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {
        accountNumberField.setText("");
        amountField.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new BankGUI().setVisible(true);
        });
    }
}