import java.util.HashMap;
import java.util.Map;

public class ATMSystem {
    public static void main(String[] args) {
        System.out.println("ATM System Demo");
        System.out.println("===============");
        System.out.println();
        
        // Create multiple bank accounts for demo cards
        Map<String, BankAccount> accounts = new HashMap<>();
        
        // Account 1 - linked to card 1234-5678-9012-3456
        accounts.put("1234567890", new BankAccount("1234567890", "John Doe", 50000.00));
        
        // Account 2 - linked to card 9876-5432-1098-7654
        accounts.put("0987654321", new BankAccount("0987654321", "Jane Smith", 75000.00));
        
        // Account 3 - linked to card 1111-2222-3333-4444
        accounts.put("1122334455", new BankAccount("1122334455", "Mike Johnson", 30000.00));
        
        // Account 4 - linked to card 5555-6666-7777-8888
        accounts.put("5566778899", new BankAccount("5566778899", "Sarah Wilson", 100000.00));
        
        System.out.println("*** DEMO ACCOUNTS CREATED ***");
        System.out.println("=============================");
        for (Map.Entry<String, BankAccount> entry : accounts.entrySet()) {
            BankAccount acc = entry.getValue();
            System.out.println("Account: " + acc.getAccountNumber() + " | Holder: " + acc.getAccountHolder() + " | Balance: Rs." + String.format("%.2f", acc.getBalance()));
        }
        System.out.println();
        
        // Create ATM with the first account (will be updated based on card)
        ATM atm = new ATM(accounts.get("1234567890"));
        
        // Start the ATM
        atm.start();
    }
}
