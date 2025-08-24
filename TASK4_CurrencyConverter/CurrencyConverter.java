import java.util.Scanner;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CurrencyConverter {
    private Scanner scanner;
    private HttpClient httpClient;
    private static final String API_KEY = ApiKey.API_KEY; // USe Your own Api Key Configured in ApiKey.java file for Real World Project we will use .env file to store the Api Key
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    
    // Popular currencies with their symbols
    private Map<String, String> currencies;
    
    public CurrencyConverter() {
        this.scanner = new Scanner(System.in);
        this.httpClient = HttpClient.newHttpClient();
        initializeCurrencies();
    }
    
    // Initialize supported currencies
    private void initializeCurrencies() {
        currencies = new HashMap<>();
        currencies.put("USD", "$");
        currencies.put("EUR", "EUR");
        currencies.put("GBP", "GBP");
        currencies.put("JPY", "JPY");
        currencies.put("INR", "Rs.");
        currencies.put("AUD", "A$");
        currencies.put("CAD", "C$");
        currencies.put("CHF", "CHF");
        currencies.put("CNY", "CNY");
        currencies.put("SGD", "S$");
        currencies.put("NZD", "NZ$");
        currencies.put("KRW", "KRW");
        currencies.put("BRL", "R$");
        currencies.put("MXN", "$");
        currencies.put("RUB", "RUB");
    }
    
    // Main method to start the converter
    public void start() {
        System.out.println("=== CURRENCY CONVERTER ===");
        System.out.println("==========================");
        System.out.println();
        
        boolean exit = false;
        
        while (!exit) {
            displayMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    convertCurrency();
                    break;
                case 2:
                    displaySupportedCurrencies();
                    break;
                case 3:
                    showExchangeRates();
                    break;
                case 4:
                    exit = true;
                    System.out.println("Thank you for using Currency Converter!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
            
            if (!exit) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                scanner.nextLine(); // Clear buffer
                System.out.println();
            }
        }
        
        scanner.close();
    }
    
    // Display main menu
    private void displayMenu() {
        System.out.println("Currency Converter Menu:");
        System.out.println("1. Convert Currency");
        System.out.println("2. View Supported Currencies");
        System.out.println("3. Show Exchange Rates");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
    }
    
    // Get user choice with validation
    private int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            if (choice < 1 || choice > 4) {
                System.out.println("Please enter a number between 1 and 4.");
                return 0;
            }
            return choice;
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("Invalid input! Please enter a number.");
            return 0;
        }
    }
    
    // Main currency conversion method
    private void convertCurrency() {
        System.out.println();
        System.out.println("=== CURRENCY CONVERSION ===");
        System.out.println("===========================");
        
        // Get base currency
        String baseCurrency = getCurrencyInput("base");
        if (baseCurrency == null) return;
        
        // Get target currency
        String targetCurrency = getCurrencyInput("target");
        if (targetCurrency == null) return;
        
        // Get amount to convert
        double amount = getAmountInput();
        if (amount <= 0) return;
        
        // Perform conversion
        performConversion(baseCurrency, targetCurrency, amount);
    }
    
    // Get currency input from user
    private String getCurrencyInput(String type) {
        System.out.println();
        System.out.println("Available currencies:");
        displayCurrencyList();
        System.out.print("Enter " + type + " currency code (e.g., USD): ");
        
        String currency = scanner.next().toUpperCase();
        scanner.nextLine(); // Clear buffer
        
        if (currencies.containsKey(currency)) {
            return currency;
        } else {
            System.out.println("Invalid currency code! Please try again.");
            return null;
        }
    }
    
    // Get amount input from user
    private double getAmountInput() {
        System.out.print("Enter amount to convert: ");
        
        try {
            double amount = scanner.nextDouble();
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return -1;
            }
            return amount;
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("Invalid amount! Please enter a valid number.");
            return -1;
        }
    }
    
    // Perform the actual currency conversion
    private void performConversion(String baseCurrency, String targetCurrency, double amount) {
        System.out.println();
        System.out.println("Converting " + amount + " " + baseCurrency + " to " + targetCurrency + "...");
        System.out.println("Fetching real-time exchange rate...");
        
        try {
            // Fetch exchange rate from API
            double exchangeRate = fetchExchangeRate(baseCurrency, targetCurrency);
            
            if (exchangeRate > 0) {
                // Calculate converted amount
                double convertedAmount = amount * exchangeRate;
                
                // Display result
                System.out.println();
                System.out.println("=== CONVERSION RESULT ===");
                System.out.println("Base Amount: " + currencies.get(baseCurrency) + String.format("%.2f", amount) + " " + baseCurrency);
                System.out.println("Exchange Rate: 1 " + baseCurrency + " = " + String.format("%.6f", exchangeRate) + " " + targetCurrency);
                System.out.println("Converted Amount: " + currencies.get(targetCurrency) + String.format("%.2f", convertedAmount) + " " + targetCurrency);
                System.out.println("=========================");
            } else {
                System.out.println("Failed to fetch exchange rate. Please try again later.");
            }
            
        } catch (Exception e) {
            System.out.println("Error during conversion: " + e.getMessage());
            System.out.println("Please check your internet connection and try again.");
        }
    }
    
    // Fetch exchange rate from API
    private double fetchExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            // For demo purposes, we'll use a free API
            String url = BASE_URL + baseCurrency;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse the JSON response to get exchange rate
                return parseExchangeRate(response.body(), targetCurrency);
            } else {
                System.out.println("API request failed with status: " + response.statusCode());
                return -1;
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching exchange rate: " + e.getMessage());
            return -1;
        }
    }
    
    // Parse exchange rate from JSON response
    private double parseExchangeRate(String jsonResponse, String targetCurrency) {
        try {
            // Check for the new API format with "conversion_rates"
            if (jsonResponse.contains("\"conversion_rates\"")) {
                // Extract the conversion_rates section
                int ratesStart = jsonResponse.indexOf("\"conversion_rates\"");
                int ratesEnd = jsonResponse.indexOf("}", ratesStart);
                
                if (ratesStart != -1 && ratesEnd != -1) {
                    String ratesSection = jsonResponse.substring(ratesStart, ratesEnd);
                    
                    // Look for target currency
                    String targetPattern = "\"" + targetCurrency + "\":";
                    int targetStart = ratesSection.indexOf(targetPattern);
                    
                    if (targetStart != -1) {
                        int valueStart = targetStart + targetPattern.length();
                        int valueEnd = ratesSection.indexOf(",", valueStart);
                        if (valueEnd == -1) {
                            valueEnd = ratesSection.indexOf("}", valueStart);
                        }
                        
                        if (valueStart != -1 && valueEnd != -1) {
                            String rateString = ratesSection.substring(valueStart, valueEnd).trim();
                            return Double.parseDouble(rateString);
                        }
                    }
                }
            }
            // Also check for the old API format with "rates" (fallback)
            else if (jsonResponse.contains("\"rates\"")) {
                // Extract the rates section
                int ratesStart = jsonResponse.indexOf("\"rates\"");
                int ratesEnd = jsonResponse.indexOf("}", ratesStart);
                
                if (ratesStart != -1 && ratesEnd != -1) {
                    String ratesSection = jsonResponse.substring(ratesStart, ratesEnd);
                    
                    // Look for target currency
                    String targetPattern = "\"" + targetCurrency + "\":";
                    int targetStart = ratesSection.indexOf(targetPattern);
                    
                    if (targetStart != -1) {
                        int valueStart = targetStart + targetPattern.length();
                        int valueEnd = ratesSection.indexOf(",", valueStart);
                        if (valueEnd == -1) {
                            valueEnd = ratesSection.indexOf("}", valueStart);
                        }
                        
                        if (valueStart != -1 && valueEnd != -1) {
                            String rateString = ratesSection.substring(valueStart, valueEnd).trim();
                            return Double.parseDouble(rateString);
                        }
                    }
                }
            }
            
            // Fallback: return demo rate for common pairs
            return getDemoExchangeRate(targetCurrency);
            
        } catch (Exception e) {
            System.out.println("Error parsing exchange rate: " + e.getMessage());
            return getDemoExchangeRate(targetCurrency);
        }
    }
    
    // Demo exchange rates for when API is not available
    private double getDemoExchangeRate(String targetCurrency) {
        Map<String, Double> demoRates = new HashMap<>();
        demoRates.put("USD", 1.0);
        demoRates.put("EUR", 0.8568);
        demoRates.put("GBP", 0.7418);
        demoRates.put("JPY", 147.2885);
        demoRates.put("INR", 87.4251);
        demoRates.put("AUD", 1.5484);
        demoRates.put("CAD", 1.3855);
        demoRates.put("CHF", 0.805);
        demoRates.put("CNY", 7.1744);
        demoRates.put("SGD", 1.2851);
        demoRates.put("NZD", 1.7119);
        demoRates.put("KRW", 1386.7007);
        demoRates.put("BRL", 5.4683);
        demoRates.put("MXN", 18.6428);
        demoRates.put("RUB", 80.6101);
        
        Double rate = demoRates.get(targetCurrency);
        if (rate != null) {
            System.out.println("Using demo exchange rate (API unavailable)");
            return rate;
        }
        
        return 1.0; // Default fallback
    }
    
    // Display supported currencies
    private void displaySupportedCurrencies() {
        System.out.println();
        System.out.println("=== SUPPORTED CURRENCIES ===");
        System.out.println("=============================");
        displayCurrencyList();
    }
    
    // Display currency list in a formatted way
    private void displayCurrencyList() {
        Set<String> sortedCurrencies = new TreeSet<>(currencies.keySet());
        int count = 0;
        
        for (String currency : sortedCurrencies) {
            System.out.printf("%-4s ", currency);
            count++;
            if (count % 5 == 0) {
                System.out.println();
            }
        }
        if (count % 5 != 0) {
            System.out.println();
        }
        System.out.println();
    }
    
    // Show current exchange rates
    private void showExchangeRates() {
        System.out.println();
        System.out.println("=== CURRENT EXCHANGE RATES ===");
        System.out.println("==============================");
        System.out.println("Base Currency: USD");
        System.out.println();
        
        // Show rates for major currencies
        String[] majorCurrencies = {"EUR", "GBP", "JPY", "INR", "AUD", "CAD"};
        
        for (String currency : majorCurrencies) {
            if (!currency.equals("USD")) {
                double rate = fetchExchangeRate("USD", currency);
                if (rate > 0) {
                    System.out.printf("1 USD = %s%.4f %s%n", 
                        currencies.get(currency), rate, currency);
                } else {
                    // Use demo rate
                    double demoRate = getDemoExchangeRate(currency);
                    System.out.printf("1 USD = %s%.4f %s (demo)%n", 
                        currencies.get(currency), demoRate, currency);
                }
            }
        }
        System.out.println();
    }
    
    // Main method
    public static void main(String[] args) {
        System.out.println("Currency Converter Application");
        System.out.println("=============================");
        System.out.println();
        // System.out.println("Note: This application uses real-time exchange rates from an API.");
        // System.out.println("If the API is unavailable, demo rates will be used.");
        if(ApiKey.API_KEY.equals("demo")) {
            System.out.println("Note: This application uses real-time exchange rates from an API.");
            System.out.println("Please, Configure your own Api Key in ApiKey.java file for Real World Project we will use .env file to store the Api Key");
        }else if(ApiKey.API_KEY.equals(null)) {
            System.out.println("Please, Configure your own Api Key in ApiKey.java file for Real World Project we will use .env file to store the Api Key");
        } else{
            System.out.println("ApiKey Configured Successfully");
        }
        System.out.println();
        
        CurrencyConverter converter = new CurrencyConverter();
        converter.start();
    }
}
