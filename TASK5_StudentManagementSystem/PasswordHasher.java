import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    
    /**
     * Hash a password with salt and multiple iterations
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt and iterations
            String hashedPassword = hashWithSalt(password, salt, ITERATIONS);
            
            // Combine salt and hash for storage
            return Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify a password against stored hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false; // Invalid hash format
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedPasswordHash = parts[1];
            
            // Hash input password with same salt and iterations
            String inputPasswordHash = hashWithSalt(password, salt, ITERATIONS);
            
            // Compare hashes
            return storedPasswordHash.equals(inputPasswordHash);
            
        } catch (Exception e) {
            return false; // Any error means verification failed
        }
    }
    
    /**
     * Hash password with salt and iterations
     */
    private static String hashWithSalt(String password, byte[] salt, int iterations) 
            throws NoSuchAlgorithmException {
        
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        
        // Add salt to password
        md.update(salt);
        md.update(password.getBytes());
        
        // Apply multiple iterations
        byte[] hash = md.digest();
        for (int i = 1; i < iterations; i++) {
            md.reset();
            md.update(hash);
            hash = md.digest();
        }
        
        return Base64.getEncoder().encodeToString(hash);
    }
    
    /**
     * Generate a random secure password
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        String allChars = upperCase + lowerCase + numbers + specialChars;
        SecureRandom random = new SecureRandom();
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill remaining length with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password characters
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * Check password strength
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // Length bonus
        if (password.length() >= 12) score += 2;
        else if (password.length() >= 8) score += 1;
        
        // Character variety bonus
        if (password.matches(".*[a-z].*")) score += 1; // lowercase
        if (password.matches(".*[A-Z].*")) score += 1; // uppercase
        if (password.matches(".*\\d.*")) score += 1;   // digits
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) score += 1; // special chars
        
        // Deduct points for common patterns
        if (password.matches(".*(123|abc|qwe|password|admin).*")) score -= 2;
        if (password.matches(".*(.)\\1{2,}.*")) score -= 1; // repeated characters
        
        if (score >= 5) return PasswordStrength.STRONG;
        else if (score >= 3) return PasswordStrength.MEDIUM;
        else return PasswordStrength.WEAK;
    }
    
    /**
     * Password strength enumeration
     */
    public enum PasswordStrength {
        WEAK("Weak", "Password is too weak. Consider using a stronger password."),
        MEDIUM("Medium", "Password strength is acceptable but could be improved."),
        STRONG("Strong", "Password meets strong security requirements.");
        
        private final String displayName;
        private final String description;
        
        PasswordStrength(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
}
