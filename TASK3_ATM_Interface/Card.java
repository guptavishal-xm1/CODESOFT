public class Card {
    private String cardNumber;
    private String expiryDate;
    private String linkedAccountNumber;
    private boolean isActive;
    
    // Constructor
    public Card(String cardNumber, String expiryDate, String linkedAccountNumber) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.linkedAccountNumber = linkedAccountNumber;
        this.isActive = true;
    }
    
    // Getter methods
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public String getLinkedAccountNumber() {
        return linkedAccountNumber;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    // Method to deactivate card
    public void deactivate() {
        this.isActive = false;
    }
    
    // Method to activate card
    public void activate() {
        this.isActive = true;
    }
    
    // Method to check if card is expired
    public boolean isExpired() {
        // Simple expiry check (you can enhance this)
        return false; // For demo purposes, assume cards are not expired
    }
}
