import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GradeCalculator {
    private Scanner scanner;
    private List<Subject> subjects;
    private Student student;
    private static final String REPORTS_DIR = "reports";
    private static final String DATA_FILE = "student_data.txt";
    
    // Constructor
    public GradeCalculator() {
        this.scanner = new Scanner(System.in);
        this.subjects = new ArrayList<>();
        this.student = new Student();
        createReportsDirectory();
    }
    
    // Create reports directory if it doesn't exist
    private void createReportsDirectory() {
        File directory = new File(REPORTS_DIR);
        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("Created reports directory: " + REPORTS_DIR);
        }
    }
    
    // Main method
    public static void main(String[] args) {
        System.out.println("Student Grade Calculator with Report Management");
        System.out.println("=====================================================");
        System.out.println();
        
        GradeCalculator calculator = new GradeCalculator();
        calculator.showMainMenu();
    }
    
    // Show main menu with options
    public void showMainMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("=== MAIN MENU ===");
            System.out.println("=================");
            System.out.println("1. Create New Grade Report");
            System.out.println("2. View All Reports");
            System.out.println("3. Search Report by Student ID");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1-4): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        createNewReport();
                        break;
                    case 2:
                        viewAllReports();
                        break;
                    case 3:
                        searchReportById();
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Thank you for using Enhanced Grade Calculator!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please enter 1-4.");
                }
                
                if (!exit) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    System.out.println();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    // Create new grade report
    private void createNewReport() {
        System.out.println("\n=== CREATING NEW GRADE REPORT ===");
        System.out.println("=================================");
        
        try {
            // Get student information
            getStudentInfo();
            
            // Get subject information
            getSubjectInfo();
            
            // Calculate and display results
            calculateAndDisplayResults();
            
            // Generate and save performance report
            generateAndSavePerformanceReport();
            
            // Save to data file
            saveToDataFile();
            
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please try again.");
        }
    }
    
    // Get student information
    private void getStudentInfo() {
        System.out.println("\n=== STUDENT INFORMATION ===");
        System.out.println("===========================");
        
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();
        student.setName(name);
        
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();
        student.setId(id);
        
        System.out.print("Enter Class/Semester: ");
        String className = scanner.nextLine();
        student.setClassName(className);
        
        System.out.println();
    }
    
    // Get subject information
    private void getSubjectInfo() {
        System.out.println("=== SUBJECT INFORMATION ===");
        System.out.println("===========================");
        
        int numSubjects = getValidSubjectCount();
        
        System.out.println();
        System.out.println("Enter subject details (marks out of 100):");
        System.out.println("----------------------------------------");
        
        for (int i = 0; i < numSubjects; i++) {
            Subject subject = getSubjectDetails(i + 1);
            subjects.add(subject);
            System.out.println();
        }
    }
    
    // Get valid subject count
    private int getValidSubjectCount() {
        int numSubjects;
        do {
            System.out.print("Enter the number of subjects (1-10): ");
            try {
                numSubjects = Integer.parseInt(scanner.nextLine());
                if (numSubjects < 1 || numSubjects > 10) {
                    System.out.println("Please enter a number between 1 and 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                numSubjects = 0;
            }
        } while (numSubjects < 1 || numSubjects > 10);
        
        return numSubjects;
    }
    
    // Get individual subject details
    private Subject getSubjectDetails(int subjectNumber) {
        System.out.println("Subject " + subjectNumber + ":");
        
        System.out.print("  Subject Name: ");
        String subjectName = scanner.nextLine();
        
        int marks = getValidMarks(subjectName);
        
        return new Subject(subjectName, marks);
    }
    
    // Get valid marks for a subject
    private int getValidMarks(String subjectName) {
        int marks;
        do {
            System.out.print("  Marks for " + subjectName + " (0-100): ");
            try {
                marks = Integer.parseInt(scanner.nextLine());
                if (marks < 0 || marks > 100) {
                    System.out.println("  Invalid marks! Please enter marks between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input! Please enter a valid number.");
                marks = -1;
            }
        } while (marks < 0 || marks > 100);
        
        return marks;
    }
    
    // Calculate and display results
    private void calculateAndDisplayResults() {
        System.out.println("=== CALCULATION RESULTS ===");
        System.out.println("===========================");
        
        // Calculate total marks
        int totalMarks = subjects.stream().mapToInt(Subject::getMarks).sum();
        int maxPossibleMarks = subjects.size() * 100;
        
        // Calculate average percentage
        double averagePercentage = (double) totalMarks / subjects.size();
        
        // Calculate grade
        String grade = calculateGrade(averagePercentage);
        
        // Calculate GPA
        double gpa = calculateGPA(averagePercentage);
        
        // Display detailed results
        displayDetailedResults(totalMarks, maxPossibleMarks, averagePercentage, grade, gpa);
    }
    
    // Display detailed results
    private void displayDetailedResults(int totalMarks, int maxPossibleMarks, 
                                     double averagePercentage, String grade, double gpa) {
        System.out.println();
        System.out.println("Student: " + student.getName() + " (ID: " + student.getId() + ")");
        System.out.println("Class: " + student.getClassName());
        System.out.println();
        
        // Subject-wise breakdown
        System.out.println("Subject-wise Performance:");
        System.out.println("------------------------");
        for (Subject subject : subjects) {
            String subjectGrade = calculateGrade(subject.getMarks());
            System.out.printf("%-15s: %3d/100 (%s)%n", 
                subject.getName(), subject.getMarks(), subjectGrade);
        }
        
        System.out.println();
        System.out.println("Overall Performance:");
        System.out.println("-------------------");
        System.out.printf("Total Marks: %d/%d%n", totalMarks, maxPossibleMarks);
        System.out.printf("Average Percentage: %.2f%%%n", averagePercentage);
        System.out.printf("Grade: %s%n", grade);
        System.out.printf("GPA: %.2f%n", gpa);
        
        // Performance analysis
        System.out.println();
        System.out.println("Performance Analysis:");
        System.out.println("---------------------");
        analyzePerformance(averagePercentage, subjects);
    }
    
    // Analyze performance and provide feedback
    private void analyzePerformance(double averagePercentage, List<Subject> subjects) {
        // Overall performance feedback
        if (averagePercentage >= 90) {
            System.out.println("*** EXCELLENT PERFORMANCE! Outstanding work! ***");
        } else if (averagePercentage >= 80) {
            System.out.println("*** VERY GOOD! You're performing exceptionally well! ***");
        } else if (averagePercentage >= 70) {
            System.out.println("*** GOOD WORK! Keep up the momentum! ***");
        } else if (averagePercentage >= 60) {
            System.out.println("*** SATISFACTORY. Room for improvement! ***");
        } else if (averagePercentage >= 50) {
            System.out.println("*** NEEDS IMPROVEMENT. Focus on your studies! ***");
        } else {
            System.out.println("*** URGENT ATTENTION REQUIRED. Seek academic support! ***");
        }
        
        // Subject-specific analysis
        System.out.println();
        System.out.println("Subject-wise Analysis:");
        System.out.println("----------------------");
        
        for (Subject subject : subjects) {
            analyzeSubjectPerformance(subject);
        }
        
        // Improvement suggestions
        System.out.println();
        System.out.println("Improvement Suggestions:");
        System.out.println("------------------------");
        provideImprovementSuggestions(averagePercentage, subjects);
    }
    
    // Analyze individual subject performance
    private void analyzeSubjectPerformance(Subject subject) {
        int marks = subject.getMarks();
        String analysis;
        
        if (marks >= 90) {
            analysis = "Outstanding! Maintain this level.";
        } else if (marks >= 80) {
            analysis = "Very good! Minor improvements possible.";
        } else if (marks >= 70) {
            analysis = "Good! Focus on weak areas.";
        } else if (marks >= 60) {
            analysis = "Satisfactory. Needs more practice.";
        } else if (marks >= 50) {
            analysis = "Below average. Seek help immediately.";
        } else {
            analysis = "Critical! Requires urgent attention.";
        }
        
        System.out.printf("%-15s: %s%n", subject.getName(), analysis);
    }
    
    // Provide improvement suggestions
    private void provideImprovementSuggestions(double averagePercentage, List<Subject> subjects) {
        if (averagePercentage < 70) {
            System.out.println("• Attend all classes regularly");
            System.out.println("• Complete all assignments on time");
            System.out.println("• Seek help from teachers/tutors");
            System.out.println("• Form study groups with classmates");
            System.out.println("• Practice previous year questions");
        } else if (averagePercentage < 80) {
            System.out.println("• Focus on weak subjects");
            System.out.println("• Review notes regularly");
            System.out.println("• Solve practice problems");
            System.out.println("• Take mock tests");
        } else if (averagePercentage < 90) {
            System.out.println("• Aim for perfection in strong subjects");
            System.out.println("• Help classmates with difficult topics");
            System.out.println("• Participate in academic competitions");
            System.out.println("• Explore advanced topics");
        } else {
            System.out.println("• Maintain your excellent performance");
            System.out.println("• Mentor other students");
            System.out.println("• Challenge yourself with advanced topics");
            System.out.println("• Consider research opportunities");
        }
    }
    
    // Generate and save performance report
    private void generateAndSavePerformanceReport() {
        System.out.println();
        System.out.println("=== PERFORMANCE REPORT ===");
        System.out.println("==========================");
        
        // Calculate statistics
        int totalMarks = subjects.stream().mapToInt(Subject::getMarks).sum();
        double averagePercentage = (double) totalMarks / subjects.size();
        String grade = calculateGrade(averagePercentage);
        double gpa = calculateGPA(averagePercentage);
        
        // Find best and worst subjects
        Subject bestSubject = subjects.stream()
            .max((s1, s2) -> Integer.compare(s1.getMarks(), s2.getMarks()))
            .orElse(null);
        
        Subject worstSubject = subjects.stream()
            .min((s1, s2) -> Integer.compare(s1.getMarks(), s2.getMarks()))
            .orElse(null);
        
        // Display report
        System.out.println();
        System.out.println("*** PERFORMANCE SUMMARY ***");
        System.out.println("------------------------");
        System.out.printf("Student Name: %s%n", student.getName());
        System.out.printf("Student ID: %s%n", student.getId());
        System.out.printf("Class: %s%n", student.getClassName());
        System.out.printf("Total Subjects: %d%n", subjects.size());
        System.out.printf("Total Marks: %d/%d%n", totalMarks, subjects.size() * 100);
        System.out.printf("Average: %.2f%%%n", averagePercentage);
        System.out.printf("Grade: %s%n", grade);
        System.out.printf("GPA: %.2f%n", gpa);
        
        if (bestSubject != null && worstSubject != null) {
            System.out.println();
            System.out.println("*** Best Subject: " + bestSubject.getName() + 
                             " (" + bestSubject.getMarks() + "/100) ***");
            System.out.println("*** Needs Improvement: " + worstSubject.getName() + 
                             " (" + worstSubject.getMarks() + "/100) ***");
        }
        
        System.out.println();
        System.out.println("Report generated successfully!");
        
        // Save report to file
        saveReportToFile(totalMarks, averagePercentage, grade, gpa, bestSubject, worstSubject);
    }
    
    // Save report to text file
    private void saveReportToFile(int totalMarks, double averagePercentage, String grade, 
                                 double gpa, Subject bestSubject, Subject worstSubject) {
        try {
            // Create filename with timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);
            String filename = REPORTS_DIR + "/" + student.getId() + "_" + timestamp + ".txt";
            
            FileWriter writer = new FileWriter(filename);
            PrintWriter printWriter = new PrintWriter(writer);
            
            // Write report header
            printWriter.println("ENHANCED STUDENT GRADE CALCULATOR - PERFORMANCE REPORT");
            printWriter.println("=====================================================");
            printWriter.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            printWriter.println();
            
            // Student information
            printWriter.println("STUDENT INFORMATION:");
            printWriter.println("-------------------");
            printWriter.println("Name: " + student.getName());
            printWriter.println("ID: " + student.getId());
            printWriter.println("Class: " + student.getClassName());
            printWriter.println();
            
            // Subject-wise performance
            printWriter.println("SUBJECT-WISE PERFORMANCE:");
            printWriter.println("------------------------");
            for (Subject subject : subjects) {
                String subjectGrade = calculateGrade(subject.getMarks());
                printWriter.printf("%-15s: %3d/100 (%s)%n", 
                    subject.getName(), subject.getMarks(), subjectGrade);
            }
            printWriter.println();
            
            // Overall performance
            printWriter.println("OVERALL PERFORMANCE:");
            printWriter.println("-------------------");
            printWriter.printf("Total Marks: %d/%d%n", totalMarks, subjects.size() * 100);
            printWriter.printf("Average Percentage: %.2f%%%n", averagePercentage);
            printWriter.printf("Grade: %s%n", grade);
            printWriter.printf("GPA: %.2f%n", gpa);
            printWriter.println();
            
            // Performance analysis
            printWriter.println("PERFORMANCE ANALYSIS:");
            printWriter.println("--------------------");
            if (averagePercentage >= 90) {
                printWriter.println("*** EXCELLENT PERFORMANCE! Outstanding work! ***");
            } else if (averagePercentage >= 80) {
                printWriter.println("*** VERY GOOD! You're performing exceptionally well! ***");
            } else if (averagePercentage >= 70) {
                printWriter.println("*** GOOD WORK! Keep up the momentum! ***");
            } else if (averagePercentage >= 60) {
                printWriter.println("*** SATISFACTORY. Room for improvement! ***");
            } else if (averagePercentage >= 50) {
                printWriter.println("*** NEEDS IMPROVEMENT. Focus on your studies! ***");
            } else {
                printWriter.println("*** URGENT ATTENTION REQUIRED. Seek academic support! ***");
            }
            printWriter.println();
            
            // Subject analysis
            printWriter.println("SUBJECT-WISE ANALYSIS:");
            printWriter.println("----------------------");
            for (Subject subject : subjects) {
                int marks = subject.getMarks();
                String analysis;
                if (marks >= 90) {
                    analysis = "Outstanding! Maintain this level.";
                } else if (marks >= 80) {
                    analysis = "Very good! Minor improvements possible.";
                } else if (marks >= 70) {
                    analysis = "Good! Focus on weak areas.";
                } else if (marks >= 60) {
                    analysis = "Satisfactory. Needs more practice.";
                } else if (marks >= 50) {
                    analysis = "Below average. Seek help immediately.";
                } else {
                    analysis = "Critical! Requires urgent attention.";
                }
                printWriter.printf("%-15s: %s%n", subject.getName(), analysis);
            }
            printWriter.println();
            
            // Best and worst subjects
            if (bestSubject != null && worstSubject != null) {
                printWriter.println("PERFORMANCE HIGHLIGHTS:");
                printWriter.println("----------------------");
                printWriter.println("*** Best Subject: " + bestSubject.getName() + 
                                 " (" + bestSubject.getMarks() + "/100) ***");
                printWriter.println("*** Needs Improvement: " + worstSubject.getName() + 
                                 " (" + worstSubject.getMarks() + "/100) ***");
                printWriter.println();
            }
            
            // Improvement suggestions
            printWriter.println("IMPROVEMENT SUGGESTIONS:");
            printWriter.println("------------------------");
            if (averagePercentage < 70) {
                printWriter.println("• Attend all classes regularly");
                printWriter.println("• Complete all assignments on time");
                printWriter.println("• Seek help from teachers/tutors");
                printWriter.println("• Form study groups with classmates");
                printWriter.println("• Practice previous year questions");
            } else if (averagePercentage < 80) {
                printWriter.println("• Focus on weak subjects");
                printWriter.println("• Review notes regularly");
                printWriter.println("• Solve practice problems");
                printWriter.println("• Take mock tests");
            } else if (averagePercentage < 90) {
                printWriter.println("• Aim for perfection in strong subjects");
                printWriter.println("• Help classmates with difficult topics");
                printWriter.println("• Participate in academic competitions");
                printWriter.println("• Explore advanced topics");
            } else {
                printWriter.println("• Maintain your excellent performance");
                printWriter.println("• Mentor other students");
                printWriter.println("• Challenge yourself with advanced topics");
                printWriter.println("• Consider research opportunities");
            }
            printWriter.println();
            
            printWriter.println("Report generated successfully!");
            printWriter.println("Thank you for using Enhanced Grade Calculator!");
            
            printWriter.close();
            System.out.println("Report saved to: " + filename);
            
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
    
    // Save student data to data file
    private void saveToDataFile() {
        try {
            FileWriter writer = new FileWriter(DATA_FILE, true); // Append mode
            PrintWriter printWriter = new PrintWriter(writer);
            
            // Save in CSV format for easy parsing
            printWriter.printf("%s|%s|%s|%d|%.2f|%s|%.2f%n",
                student.getId(),
                student.getName(),
                student.getClassName(),
                subjects.size(),
                subjects.stream().mapToInt(Subject::getMarks).average().orElse(0.0),
                calculateGrade(subjects.stream().mapToInt(Subject::getMarks).average().orElse(0.0)),
                calculateGPA(subjects.stream().mapToInt(Subject::getMarks).average().orElse(0.0))
            );
            
            printWriter.close();
            
        } catch (IOException e) {
            System.out.println("Error saving to data file: " + e.getMessage());
        }
    }
    
    // View all reports
    private void viewAllReports() {
        System.out.println("\n=== VIEWING ALL REPORTS ===");
        System.out.println("===========================");
        
        try {
            File dataFile = new File(DATA_FILE);
            if (!dataFile.exists()) {
                System.out.println("No reports found. Please create a report first.");
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line;
            int count = 0;
            
            System.out.println("\n%-15s %-20s %-15s %-8s %-10s %-8s %-8s%n".formatted(
                "Student ID", "Name", "Class", "Subjects", "Average", "Grade", "GPA"));
            System.out.println("--------------------------------------------------------------------------------");
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    System.out.printf("%-15s %-20s %-15s %-8s %-10s %-8s %-8s%n",
                        parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                    count++;
                }
            }
            
            reader.close();
            System.out.println("\nTotal reports: " + count);
            
        } catch (IOException e) {
            System.out.println("Error reading reports: " + e.getMessage());
        }
    }
    
    // Search report by student ID
    private void searchReportById() {
        System.out.println("\n=== SEARCH REPORT BY STUDENT ID ===");
        System.out.println("===================================");
        
        System.out.print("Enter Student ID to search: ");
        String searchId = scanner.nextLine();
        
        try {
            File dataFile = new File(DATA_FILE);
            if (!dataFile.exists()) {
                System.out.println("No reports found. Please create a report first.");
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line;
            boolean found = false;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7 && parts[0].equalsIgnoreCase(searchId)) {
                    System.out.println("\n*** STUDENT REPORT FOUND ***");
                    System.out.println("----------------------------");
                    System.out.println("Student ID: " + parts[0]);
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Class: " + parts[2]);
                    System.out.println("Total Subjects: " + parts[3]);
                    System.out.println("Average: " + parts[4] + "%");
                    System.out.println("Grade: " + parts[5]);
                    System.out.println("GPA: " + parts[6]);
                    found = true;
                    break;
                }
            }
            
            reader.close();
            
            if (!found) {
                System.out.println("No report found for Student ID: " + searchId);
            }
            
        } catch (IOException e) {
            System.out.println("Error searching reports: " + e.getMessage());
        }
    }
    
    // Calculate grade based on percentage
    public static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B+";
        else if (percentage >= 60) return "B";
        else if (percentage >= 50) return "C+";
        else if (percentage >= 40) return "C";
        else if (percentage >= 35) return "D";
        else return "F";
    }
    
    // Calculate GPA based on percentage
    public static double calculateGPA(double percentage) {
        if (percentage >= 90) return 4.0;
        else if (percentage >= 80) return 3.5;
        else if (percentage >= 70) return 3.0;
        else if (percentage >= 60) return 2.5;
        else if (percentage >= 50) return 2.0;
        else if (percentage >= 40) return 1.5;
        else if (percentage >= 35) return 1.0;
        else return 0.0;
    }
}

// Subject class to store subject information
class Subject {
    private String name;
    private int marks;
    
    public Subject(String name, int marks) {
        this.name = name;
        this.marks = marks;
    }
    
    public String getName() { return name; }
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
}

// Student class to store student information
class Student {
    private String name;
    private String id;
    private String className;
    
    public Student() {}
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
