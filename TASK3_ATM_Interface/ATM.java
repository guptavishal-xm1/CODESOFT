import java.util.Scanner;

public class ATM {
    private BankAccount account;
    private Scanner scanner;
    private static final String DEMO_PIN = "1234";
    private boolean isAuthenticated = false;
    private boolean isCardInserted = false;
    private Card currentCard;
    private CardDatabase cardDatabase;
    
    // ATM Cash Management
    private int cashAvailable;
    private static final int[] DENOMINATIONS = {2000, 500, 200, 100}; // Only 100 multiples for security
    private int[] notesAvailable = {50, 100, 200, 500}; // Notes of each denomination
    
    // Constructor
    public ATM(BankAccount account) {
        this.account = account;
        this.scanner = new Scanner(System.in);
        this.cardDatabase = new CardDatabase();
        
        // Initialize ATM with cash
        initializeATMCash();
    }
    
    // Initialize ATM with cash
    private void initializeATMCash() {
        cashAvailable = 0;
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            cashAvailable += DENOMINATIONS[i] * notesAvailable[i];
        }
    }
    
    // Main method to start ATM
    public void start() {
        System.out.println("Welcome to ATM Machine");
        System.out.println("======================");
        System.out.println("Account Holder: " + account.getAccountHolder());
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println();
        
        System.out.println("Initializing ATM...");
        try {
            Thread.sleep(1200); // Wait for 1.2 seconds
        } catch (InterruptedException e) {
            // Handle interruption if needed
        }
        System.out.println("ATM Ready!");
        System.out.println();
        
        // Show demo information
        System.out.println("*** DEMO MODE ***");
        System.out.println("Demo PIN: 1234");
        System.out.println("Use this PIN to access your account");
        System.out.println();
        
        // Show demo cards
        cardDatabase.displayDemoCards();
        
        // Show ATM denomination info (without revealing total cash)
        System.out.println("*** ATM DENOMINATIONS ***");
        System.out.println("Available Notes: Rs.2000, Rs.500, Rs.200, Rs.100");
        System.out.println("Minimum Withdrawal: Rs.100");
        System.out.println();
        
        boolean exit = false;
        
        while (!exit) {
            if (!isCardInserted) {
                insertCard();
            } else {
                displayMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        if (authenticateUser()) {
                            checkBalance();
                        }
                        break;
                    case 2:
                        if (authenticateUser()) {
                            withdrawMoney();
                        }
                        break;
                    case 3:
                        depositMoney();
                        break;
                    case 4:
                        checkATMCash();
                        break;
                    case 5:
                        logout();
                        break;
                    case 6:
                        ejectCard();
                        break;
                    case 7:
                        exit = true;
                        System.out.println("Thank you for using our ATM!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
                
                if (!exit) {
                    System.out.println();
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    scanner.nextLine(); // Clear buffer
                    
                    // Add a small delay before showing menu again
                    System.out.println("Loading menu...");
                    try {
                        Thread.sleep(800); // Wait for 0.8 seconds
                    } catch (InterruptedException e) {
                        // Handle interruption if needed
                    }
                    System.out.println();
                }
            }
        }
        
        scanner.close();
    }
    
    // Display main menu
    private void displayMenu() {
        System.out.println("ATM Menu:");
        System.out.println("1. Check Balance");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Deposit Money");
        System.out.println("4. ATM Information");
        System.out.println("5. Logout");
        System.out.println("6. Eject Card");
        System.out.println("7. Exit");
        
        if (isAuthenticated) {
            System.out.println("*** Status: Authenticated ***");
        } else {
            System.out.println("*** Status: Not Authenticated - PIN Required ***");
        }
        System.out.println("*** Card: " + currentCard.getCardNumber() + " ***");
        System.out.print("Enter your choice (1-7): ");
    }
    
    // Get user choice with validation
    private int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            if (choice < 1 || choice > 7) {
                System.out.println("Please enter a number between 1 and 7.");
                return 0;
            }
            return choice;
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("Invalid input! Please enter a number.");
            return 0;
        }
    }
    
    // Method to check balance
    private void checkBalance() {
        System.out.println();
        System.out.println("=== Balance Inquiry ===");
        System.out.println("Current Balance: Rs." + String.format("%.2f", account.getBalance()));
        
        // Add delay to make it feel more realistic
        System.out.println("Processing...");
        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            // Handle interruption if needed
        }
    }
    
    // Method to withdraw money
    private void withdrawMoney() {
        System.out.println();
        System.out.println("=== Withdraw Money ===");
        System.out.println("Current Balance: Rs." + String.format("%.2f", account.getBalance()));
        System.out.println("Available Notes: Rs.2000, Rs.500, Rs.200, Rs.100");
        
        System.out.print("Enter amount to withdraw: Rs.");
        double amount = getAmountInput();
        
        if (amount <= 0) {
            System.out.println("Invalid amount! Please enter a positive number.");
            return;
        }
        
        // Check if amount is multiple of 100
        if (amount % 100 != 0) {
            System.out.println("Invalid amount! Please enter amount in multiples of Rs.100.");
            System.out.println("Example: Rs.100, Rs.200, Rs.500, Rs.1000, etc.");
            return;
        }
        
        if (amount > cashAvailable) {
            System.out.println("Insufficient cash in ATM!");
            System.out.println("Please try a smaller amount.");
            return;
        }
        
        if (account.canWithdraw(amount)) {
            System.out.println("Processing withdrawal...");
            try {
                Thread.sleep(1500); // Wait for 1.5 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
            
            if (account.withdraw(amount)) {
                // Update ATM cash
                updateATMCash((int)amount);
                
                System.out.println("Withdrawal successful!");
                System.out.println("Amount withdrawn: Rs." + String.format("%.2f", amount));
                System.out.println("New balance: Rs." + String.format("%.2f", account.getBalance()));
                System.out.println("Please collect your cash from the dispenser.");
            } else {
                System.out.println("Withdrawal failed! Please try again.");
            }
        } else {
            System.out.println("Insufficient balance!");
            System.out.println("You can only withdraw up to Rs." + String.format("%.2f", account.getBalance()));
        }
    }
    
    // Method to deposit money
    private void depositMoney() {
        System.out.println();
        System.out.println("=== Deposit Money ===");
        System.out.println("Current Balance: Rs." + String.format("%.2f", account.getBalance()));
        
        System.out.print("Enter amount to deposit: Rs.");
        double amount = getAmountInput();
        
        if (amount <= 0) {
            System.out.println("Invalid amount! Please enter a positive number.");
            return;
        }
        
        System.out.println("Processing deposit...");
        try {
            Thread.sleep(1500); // Wait for 1.5 seconds
        } catch (InterruptedException e) {
            // Handle interruption if needed
        }
        
        if (account.deposit(amount)) {
            System.out.println("Deposit successful!");
            System.out.println("Amount deposited: Rs." + String.format("%.2f", amount));
            System.out.println("New balance: Rs." + String.format("%.2f", account.getBalance()));
        } else {
            System.out.println("Deposit failed! Please try again.");
        }
    }
    
    // Helper method to get amount input with validation
    private double getAmountInput() {
        try {
            return scanner.nextDouble();
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return -1;
        }
    }
    
    // Method to authenticate user with PIN
    private boolean authenticateUser() {
        if (isAuthenticated) {
            return true; // Already authenticated
        }
        
        System.out.println();
        System.out.println("=== PIN Authentication Required ===");
        System.out.print("Enter your PIN: ");
        
        String enteredPin = scanner.next();
        scanner.nextLine(); // Clear buffer
        
        if (enteredPin.equals(DEMO_PIN)) {
            isAuthenticated = true;
            System.out.println("PIN verified successfully!");
            System.out.println("Access granted to your account.");
            try {
                Thread.sleep(1000); // Wait for 1 second
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
            return true;
        } else {
            System.out.println("Invalid PIN! Access denied.");
            System.out.println("Please try again with the correct PIN.");
            try {
                Thread.sleep(1500); // Wait for 1.5 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
            return false;
        }
    }
    
    // Method to check ATM denomination info (without revealing cash amounts)
    private void checkATMCash() {
        System.out.println();
        System.out.println("=== ATM Information ===");
        System.out.println("Available Denominations:");
        System.out.println("----------------------");
        System.out.println("Rs.2000 notes: Available");
        System.out.println("Rs.500 notes: Available");
        System.out.println("Rs.200 notes: Available");
        System.out.println("Rs.100 notes: Available");
        System.out.println();
        System.out.println("Minimum Withdrawal: Rs.100");
        System.out.println("Amounts must be in multiples of Rs.100");
        System.out.println();
        System.out.println("Processing...");
        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            // Handle interruption if needed
        }
    }
    
    // Method to update ATM cash after withdrawal
    private void updateATMCash(int amount) {
        int remainingAmount = amount;
        
        // Try to dispense notes in order of denomination (highest first)
        for (int i = 0; i < DENOMINATIONS.length && remainingAmount > 0; i++) {
            int notesToDispense = Math.min(remainingAmount / DENOMINATIONS[i], notesAvailable[i]);
            if (notesToDispense > 0) {
                notesAvailable[i] -= notesToDispense;
                remainingAmount -= notesToDispense * DENOMINATIONS[i];
            }
        }
        
        // Update total cash available
        cashAvailable -= amount;
    }
    
    // Method to insert card
    private void insertCard() {
        System.out.println();
        System.out.println("=== INSERT YOUR CARD ===");
        System.out.println("Please enter your card number:");
        System.out.println("Format: XXXX-XXXX-XXXX-XXXX");
        System.out.print("Card Number: ");
        
        String cardNumber = scanner.nextLine();
        
        // Validate card
        Card card = cardDatabase.validateCard(cardNumber);
        if (card != null) {
            currentCard = card;
            isCardInserted = true;
            System.out.println();
            System.out.println("Card accepted!");
            System.out.println("Card Number: " + card.getCardNumber());
            System.out.println("Expiry Date: " + card.getExpiryDate());
            System.out.println("Linked Account: " + card.getLinkedAccountNumber());
            System.out.println();
            
            // Update account based on card
            updateAccountForCard(card);
            
            try {
                Thread.sleep(1500); // Wait for 1.5 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        } else {
            System.out.println();
            System.out.println("Invalid card or card not found!");
            System.out.println("Please check your card number and try again.");
            System.out.println();
            
            try {
                Thread.sleep(2000); // Wait for 2 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        }
    }
    
    // Method to update account based on card
    private void updateAccountForCard(Card card) {
        String linkedAccountNumber = card.getLinkedAccountNumber();
        
        // Create new account based on card (in real system, this would fetch from database)
        switch (linkedAccountNumber) {
            case "1234567890":
                account = new BankAccount("1234567890", "John Doe", 50000.00);
                break;
            case "0987654321":
                account = new BankAccount("0987654321", "Jane Smith", 75000.00);
                break;
            case "1122334455":
                account = new BankAccount("1122334455", "Mike Johnson", 30000.00);
                break;
            case "5566778899":
                account = new BankAccount("5566778899", "Sarah Wilson", 100000.00);
                break;
            default:
                System.out.println("Account not found for card!");
                break;
        }
        
        System.out.println("Account loaded: " + account.getAccountHolder() + " (Rs." + String.format("%.2f", account.getBalance()) + ")");
    }
    
    // Method to eject card
    private void ejectCard() {
        if (isCardInserted) {
            System.out.println();
            System.out.println("=== CARD EJECTED ===");
            System.out.println("Please collect your card: " + currentCard.getCardNumber());
            System.out.println("Thank you for using our ATM!");
            
            // Reset all states
            isCardInserted = false;
            isAuthenticated = false;
            currentCard = null;
            
            try {
                Thread.sleep(2000); // Wait for 2 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        } else {
            System.out.println("No card inserted.");
        }
    }
    
    // Method to logout user
    private void logout() {
        if (isAuthenticated) {
            isAuthenticated = false;
            System.out.println();
            System.out.println("=== Logout Successful ===");
            System.out.println("You have been logged out.");
            System.out.println("PIN will be required for next access.");
            try {
                Thread.sleep(1500); // Wait for 1.5 seconds
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        } else {
            System.out.println();
            System.out.println("You are not currently logged in.");
        }
    }
}
