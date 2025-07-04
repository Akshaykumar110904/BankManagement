# Bank Management System

A comprehensive Java-based banking application with a graphical user interface (GUI) built using Swing. This system provides essential banking operations including account creation, deposits, withdrawals, balance inquiries, and statement generation with persistent data storage.

## 📋 Table of Contents

- [Features](#features)
- [System Architecture](#system-architecture)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [Class Structure](#class-structure)
- [File Storage](#file-storage)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Banking Operations
- **Account Creation**: Create Savings and Current accounts with customer details
- **Deposit Money**: Add funds to existing accounts
- **Withdraw Money**: Withdraw funds with insufficient balance protection
- **Balance Inquiry**: Check current account balance and details
- **Statement Generation**: Generate detailed account statements with transaction history

### Advanced Features
- **Automatic Interest Calculation**: Daily interest calculation for all accounts
  - Savings Account: 4.5% annual interest rate
  - Current Account: 1% annual interest rate
- **Persistent Data Storage**: All data saved to text files for persistence
- **Thread-Safe Operations**: Synchronized methods for concurrent access
- **Exception Handling**: Custom exceptions for banking operations
- **Transaction History**: Complete audit trail of all transactions

## 🏗️ System Architecture

The application follows Object-Oriented Programming principles with a layered architecture:

```
┌─────────────────┐
│   GUI Layer     │ ← BankGUI.java
├─────────────────┤
│ Business Layer  │ ← Bank.java
├─────────────────┤
│  Entity Layer   │ ← Account, Customer, Transaction
├─────────────────┤
│ Exception Layer │ ← Custom Exceptions
└─────────────────┘
```

## 🛠️ Technologies Used

- **Java SE 8+**: Core programming language
- **Swing**: GUI framework for desktop application
- **File I/O**: Text-based persistent storage
- **Multithreading**: Scheduled executor for interest calculations
- **Serialization**: Object persistence support

## 🚀 Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- IDE (Eclipse, IntelliJ IDEA, or VS Code with Java extensions)

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/bank-management-system.git
   cd bank-management-system
   ```

2. **Compile the Application**
   ```bash
   javac -d bin src/bank/*.java
   ```

3. **Run the Application**
   ```bash
   java -cp bin bank.BankGUI
   ```

### Alternative: Using IDE
1. Import the project into your preferred IDE
2. Ensure all `.java` files are in the `bank` package
3. Run `BankGUI.java` as the main class

## 📖 Usage

### Starting the Application
Launch the application by running `BankGUI.java`. The main window will display with the following sections:
- **Account Operations**: Input fields for account number and amount
- **Account Management**: Buttons for various banking operations
- **Transaction Output**: Display area for operation results

### Creating Accounts

1. **Savings Account**:
   - Click "Create Savings Account"
   - Enter customer name, address, and phone number
   - Account number is automatically generated
   - Initial balance is $0.00

2. **Current Account**:
   - Click "Create Current Account"
   - Enter customer details
   - Account number is automatically generated
   - Initial balance is $0.00

### Performing Transactions

1. **Deposit**:
   - Enter account number and amount
   - Click "Deposit"
   - System validates account and processes transaction

2. **Withdrawal**:
   - Enter account number and amount
   - Click "Withdraw"
   - System checks for sufficient funds

3. **Balance Inquiry**:
   - Enter account number
   - Click "Check Balance"
   - View account details and current balance

4. **Statement Generation**:
   - Enter account number
   - Click "Generate Statement"
   - Statement file is saved with timestamp

## 🗂️ Class Structure

### Core Classes

#### `Account` (Abstract Class)
- **Purpose**: Base class for all account types
- **Key Methods**:
  - `deposit(double amount)`: Add funds to account
  - `withdraw(double amount)`: Remove funds from account
  - `calculateInterest()`: Abstract method for interest calculation
- **Thread Safety**: Synchronized methods for concurrent access

#### `SavingsAccount` extends `Account`
- **Interest Rate**: 4.5% annually (0.375% monthly)
- **Features**: Higher interest rate for long-term savings

#### `CurrentAccount` extends `Account`
- **Interest Rate**: 1% annually (0.083% monthly)
- **Features**: Lower interest rate for frequent transactions

#### `Customer`
- **Purpose**: Represents bank customers
- **Attributes**: ID, name, address, phone number
- **Serializable**: Supports object persistence

#### `Transaction`
- **Purpose**: Records all banking transactions
- **Attributes**: Type, amount, balance after transaction, timestamp
- **Features**: Immutable transaction records

#### `Bank`
- **Purpose**: Main business logic controller
- **Features**:
  - Account management
  - Transaction processing
  - Data persistence
  - Interest calculation scheduling

#### `BankGUI`
- **Purpose**: Graphical user interface
- **Features**:
  - User-friendly forms
  - Error handling with dialogs
  - Real-time feedback

### Exception Classes

#### `InsufficientFundsException`
- Thrown when withdrawal amount exceeds account balance

#### `AccountNotFoundException`
- Thrown when specified account number doesn't exist

## 💾 File Storage

The application uses text-based storage with three main files:

### `customers_data.txt`
```
=== CUSTOMER ===
ID: CUS1703847392847
Name: John Doe
Address: 123 Main St
Phone: +1-555-0123
=== END CUSTOMER ===
```

### `accounts_data.txt`
```
=== ACCOUNT ===
Number: ACC1703847392847
Type: SavingsAccount
Balance: 1500.50
CustomerID: CUS1703847392847
=== END ACCOUNT ===
```

### `transactions_data.txt`
```
=== TRANSACTION ===
AccountNumber: ACC1703847392847
Timestamp: 2024-12-29 14:30:15
Type: Deposit
Amount: 500.00
BalanceAfter: 1500.50
=== END TRANSACTION ===
```

## 🔧 Configuration

### Interest Rates
- **Savings Account**: 4.5% annual (modifiable in `SavingsAccount.java`)
- **Current Account**: 1% annual (modifiable in `CurrentAccount.java`)

### Interest Calculation Schedule
- **Frequency**: Daily (configurable in `Bank.java`)
- **Implementation**: Uses `ScheduledExecutorService`

## 🚨 Error Handling

The system includes comprehensive error handling:

- **Input Validation**: Checks for valid amounts and account numbers
- **Custom Exceptions**: Specific exceptions for banking operations
- **User Feedback**: Clear error messages in GUI dialogs
- **Data Integrity**: Validates data before file operations

## 🔒 Security Features

- **Thread Safety**: Synchronized methods prevent race conditions
- **Data Validation**: Input sanitization and validation
- **Exception Handling**: Graceful error recovery
- **File Locking**: Prevents concurrent file access issues

## 🔄 Future Enhancements

Potential improvements for future versions:

- **Database Integration**: Replace text files with SQL database
- **User Authentication**: Add login system with roles
- **Network Support**: Multi-user client-server architecture
- **Enhanced UI**: Modern JavaFX interface
- **Reporting**: Advanced reporting and analytics
- **Mobile App**: Companion mobile application
- **API Integration**: RESTful web services

## 🐛 Known Issues

- File corruption may occur if application terminates unexpectedly
- No user authentication system
- Limited concurrent user support
- Text-based storage is not suitable for large datasets

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
