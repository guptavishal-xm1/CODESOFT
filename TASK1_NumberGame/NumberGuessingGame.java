import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 100;
    private static final int MAX_ATTEMPTS = 10;
    
    private Scanner scanner;
    private Random random;
    private int totalRounds;
    private int roundsWon;
    private int bestScore;
    
    public NumberGuessingGame() {
        scanner = new Scanner(System.in);
        random = new Random();
        totalRounds = 0;
        roundsWon = 0;
        bestScore = Integer.MAX_VALUE;
    }
    
    public void startGame() {
        System.out.println("*** Welcome to the Number Guessing Game! ***");
        System.out.println("I'm thinking of a number between " + MIN_RANGE + " and " + MAX_RANGE);
        System.out.println("You have " + MAX_ATTEMPTS + " attempts to guess it correctly.");
        System.out.println();
        
        boolean playAgain = true;
        
        while (playAgain) {
            playRound();
            playAgain = askToPlayAgain();
        }
        
        displayFinalStats();
        scanner.close();
    }
    
    private void playRound() {
        totalRounds++;
        int targetNumber = generateRandomNumber();
        int attempts = 0;
        boolean guessedCorrectly = false;
        
        System.out.println("=== Round " + totalRounds + " ===");
        
        while (attempts < MAX_ATTEMPTS && !guessedCorrectly) {
            attempts++;
            int remainingAttempts = MAX_ATTEMPTS - attempts;
            
            System.out.print("Enter your guess (attempt " + attempts + "/" + MAX_ATTEMPTS + "): ");
            
            try {
                int userGuess = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (userGuess < MIN_RANGE || userGuess > MAX_RANGE) {
                    System.out.println("ERROR: Please enter a number between " + MIN_RANGE + " and " + MAX_RANGE);
                    continue;
                }
                
                if (userGuess == targetNumber) {
                    guessedCorrectly = true;
                    roundsWon++;
                    updateBestScore(attempts);
                    
                    System.out.println("*** Congratulations! You guessed the number " + targetNumber + " correctly! ***");
                    System.out.println("SUCCESS: You won in " + attempts + " attempt(s)!");
                    
                    if (attempts == 1) {
                        System.out.println("*** Perfect! First try! ***");
                    } else if (attempts <= 3) {
                        System.out.println("*** Excellent! Great job! ***");
                    } else if (attempts <= 5) {
                        System.out.println("*** Good job! Well done! ***");
                    } else {
                        System.out.println("*** Nice work! You got it! ***");
                    }
                    
                } else if (userGuess < targetNumber) {
                    System.out.println("TOO LOW: Try a higher number.");
                    if (remainingAttempts > 0) {
                        System.out.println("HINT: The number is between " + userGuess + " and " + MAX_RANGE);
                    }
                } else {
                    System.out.println("TOO HIGH: Try a lower number.");
                    if (remainingAttempts > 0) {
                        System.out.println("HINT: The number is between " + MIN_RANGE + " and " + userGuess);
                    }
                }
                
                if (remainingAttempts > 0) {
                    System.out.println("ATTEMPTS REMAINING: " + remainingAttempts);
                }
                
            } catch (Exception e) {
                System.out.println("ERROR: Invalid input! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
                attempts--; // Don't count invalid input as an attempt
            }
            
            System.out.println();
        }
        
        if (!guessedCorrectly) {
            System.out.println("GAME OVER: You've used all " + MAX_ATTEMPTS + " attempts.");
            System.out.println("The number was: " + targetNumber);
        }
        
        displayRoundStats();
        System.out.println();
    }
    
    private int generateRandomNumber() {
        return random.nextInt(MAX_RANGE - MIN_RANGE + 1) + MIN_RANGE;
    }
    
    private boolean askToPlayAgain() {
        System.out.print("Would you like to play another round? (yes/no): ");
        String response = scanner.nextLine().toLowerCase().trim();
        
        return response.equals("yes") || response.equals("y") || 
               response.equals("yeah") || response.equals("sure");
    }
    
    private void updateBestScore(int attempts) {
        if (attempts < bestScore) {
            bestScore = attempts;
        }
    }
    
    private void displayRoundStats() {
        System.out.println("*** Round Statistics ***");
        System.out.println("   Rounds played: " + totalRounds);
        System.out.println("   Rounds won: " + roundsWon);
        System.out.println("   Win rate: " + String.format("%.1f", (double) roundsWon / totalRounds * 100) + "%");
        
        if (bestScore != Integer.MAX_VALUE) {
            System.out.println("   Best score: " + bestScore + " attempt(s)");
        }
    }
    
    private void displayFinalStats() {
        System.out.println("\n*** Game Over! Final Statistics ***");
        System.out.println("==================================");
        System.out.println("Total rounds played: " + totalRounds);
        System.out.println("Rounds won: " + roundsWon);
        System.out.println("Rounds lost: " + (totalRounds - roundsWon));
        System.out.println("Win rate: " + String.format("%.1f", (double) roundsWon / totalRounds * 100) + "%");
        
        if (bestScore != Integer.MAX_VALUE) {
            System.out.println("Best score: " + bestScore + " attempt(s)");
        }
        
        if (roundsWon > 0) {
            System.out.println("\n*** Congratulations on completing the game! ***");
        } else {
            System.out.println("\n*** Keep practicing! You'll get better! ***");
        }
        
        System.out.println("Thanks for playing!");
    }
    
    public static void main(String[] args) {
        NumberGuessingGame game = new NumberGuessingGame();
        game.startGame();
    }
}
