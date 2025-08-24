import java.util.HashMap;
import java.util.Map;

public class CardDatabase {
    private Map<String, Card> cards;
    
    // Constructor
    public CardDatabase() {
        cards = new HashMap<>();
        initializeDemoCards();
    }
    
    // Initialize demo cards
    private void initializeDemoCards() {
        // Demo Card 1
        Card card1 = new Card("1234-5678-9012-3456", "12/25", "1234567890");
        cards.put("1234-5678-9012-3456", card1);
        
        // Demo Card 2
        Card card2 = new Card("9876-5432-1098-7654", "06/26", "0987654321");
        cards.put("9876-5432-1098-7654", card2);
        
        // Demo Card 3
        Card card3 = new Card("1111-2222-3333-4444", "09/27", "1122334455");
        cards.put("1111-2222-3333-4444", card3);
        
        // Demo Card 4
        Card card4 = new Card("5555-6666-7777-8888", "03/28", "5566778899");
        cards.put("5555-6666-7777-8888", card4);
    }
    
    // Method to validate card
    public Card validateCard(String cardNumber) {
        Card card = cards.get(cardNumber);
        if (card != null && card.isActive() && !card.isExpired()) {
            return card;
        }
        return null;
    }
    
    // Method to get linked account number
    public String getLinkedAccountNumber(String cardNumber) {
        Card card = cards.get(cardNumber);
        if (card != null) {
            return card.getLinkedAccountNumber();
        }
        return null;
    }
    
    // Method to display all demo cards
    public void displayDemoCards() {
        System.out.println("*** DEMO CARDS AVAILABLE ***");
        System.out.println("============================");
        System.out.println("Card Number              | Expiry | Account Number");
        System.out.println("-------------------------|--------|---------------");
        
        for (Card card : cards.values()) {
            System.out.printf("%-24s | %-6s | %s%n", 
                card.getCardNumber(), 
                card.getExpiryDate(), 
                card.getLinkedAccountNumber());
        }
        System.out.println();
    }
    
    // Method to add new card
    public void addCard(Card card) {
        cards.put(card.getCardNumber(), card);
    }
    
    // Method to remove card
    public void removeCard(String cardNumber) {
        cards.remove(cardNumber);
    }
}
