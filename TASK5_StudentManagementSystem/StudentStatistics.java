import java.math.BigDecimal;
import java.util.*;

public class StudentStatistics {
    private int totalStudents;
    private double averageGPA;
    private Map<String, Integer> courseDistribution;
    private Map<String, Integer> genderDistribution;
    private Map<String, Integer> statusDistribution;
    private Map<String, Integer> ageDistribution;
    private Map<String, Integer> academicStandingDistribution;
    private List<StudentPerformance> topPerformers;
    private List<StudentPerformance> atRiskStudents;
    private Map<String, Double> courseAverageGPAs;
    private Map<String, Integer> enrollmentTrends;
    
    public StudentStatistics() {
        this.courseDistribution = new HashMap<>();
        this.genderDistribution = new HashMap<>();
        this.statusDistribution = new HashMap<>();
        this.ageDistribution = new HashMap<>();
        this.academicStandingDistribution = new HashMap<>();
        this.topPerformers = new ArrayList<>();
        this.atRiskStudents = new ArrayList<>();
        this.courseAverageGPAs = new HashMap<>();
        this.enrollmentTrends = new HashMap<>();
    }
    
    // Getters and Setters
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    
    public double getAverageGPA() { return averageGPA; }
    public void setAverageGPA(double averageGPA) { this.averageGPA = averageGPA; }
    
    public Map<String, Integer> getCourseDistribution() { return courseDistribution; }
    public Map<String, Integer> getGenderDistribution() { return genderDistribution; }
    public Map<String, Integer> getStatusDistribution() { return statusDistribution; }
    public Map<String, Integer> getAgeDistribution() { return ageDistribution; }
    public Map<String, Integer> getAcademicStandingDistribution() { return academicStandingDistribution; }
    public List<StudentPerformance> getTopPerformers() { return topPerformers; }
    public List<StudentPerformance> getAtRiskStudents() { return atRiskStudents; }
    public Map<String, Double> getCourseAverageGPAs() { return courseAverageGPAs; }
    public Map<String, Integer> getEnrollmentTrends() { return enrollmentTrends; }
    
    // Add distribution data
    public void addCourseDistribution(String course, int count) {
        courseDistribution.put(course, count);
    }
    
    public void addGenderDistribution(String gender, int count) {
        genderDistribution.put(gender, count);
    }
    
    public void addStatusDistribution(String status, int count) {
        statusDistribution.put(status, count);
    }
    
    public void addAgeDistribution(String ageRange, int count) {
        ageDistribution.put(ageRange, count);
    }
    
    public void addAcademicStandingDistribution(String standing, int count) {
        academicStandingDistribution.put(standing, count);
    }
    
    public void addTopPerformer(StudentPerformance performance) {
        topPerformers.add(performance);
    }
    
    public void addAtRiskStudent(StudentPerformance performance) {
        atRiskStudents.add(performance);
    }
    
    public void addCourseAverageGPA(String course, double averageGPA) {
        courseAverageGPAs.put(course, averageGPA);
    }
    
    public void addEnrollmentTrend(String period, int count) {
        enrollmentTrends.put(period, count);
    }
    
    // Analysis methods
    public String getOverallPerformanceRating() {
        if (averageGPA >= 3.5) return "EXCELLENT";
        else if (averageGPA >= 3.0) return "GOOD";
        else if (averageGPA >= 2.5) return "AVERAGE";
        else if (averageGPA >= 2.0) return "BELOW_AVERAGE";
        else return "POOR";
    }
    
    public String getMostPopularCourse() {
        return courseDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public String getLeastPopularCourse() {
        return courseDistribution.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public double getGenderRatio() {
        int maleCount = genderDistribution.getOrDefault("Male", 0);
        int femaleCount = genderDistribution.getOrDefault("Female", 0);
        int total = maleCount + femaleCount;
        
        if (total == 0) return 0.0;
        return (double) maleCount / total;
    }
    
    public String getAcademicHealthStatus() {
        int atRiskCount = atRiskStudents.size();
        double atRiskPercentage = (double) atRiskCount / totalStudents * 100;
        
        if (atRiskPercentage <= 10) return "HEALTHY";
        else if (atRiskPercentage <= 20) return "MODERATE";
        else if (atRiskPercentage <= 30) return "CONCERNING";
        else return "CRITICAL";
    }
    
    public List<String> getRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        // GPA-based recommendations
        if (averageGPA < 2.5) {
            recommendations.add("Implement mandatory academic counseling for students with GPA below 2.0");
            recommendations.add("Consider additional tutoring programs for struggling students");
        }
        
        // Course distribution recommendations
        if (courseDistribution.size() > 0) {
            String leastPopular = getLeastPopularCourse();
            int leastPopularCount = courseDistribution.get(leastPopular);
            if (leastPopularCount < 5) {
                recommendations.add("Review course offerings for " + leastPopular + " - consider consolidation or redesign");
            }
        }
        
        // Gender balance recommendations
        double genderRatio = getGenderRatio();
        if (genderRatio < 0.3 || genderRatio > 0.7) {
            recommendations.add("Investigate gender imbalance and implement diversity initiatives");
        }
        
        // Academic health recommendations
        String academicHealth = getAcademicHealthStatus();
        if (academicHealth.equals("CRITICAL") || academicHealth.equals("CONCERNING")) {
            recommendations.add("Implement comprehensive academic intervention program");
            recommendations.add("Increase faculty-student interaction and mentoring");
        }
        
        return recommendations;
    }
    
    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Basic statistics
        report.put("totalStudents", totalStudents);
        report.put("averageGPA", averageGPA);
        report.put("overallPerformanceRating", getOverallPerformanceRating());
        report.put("academicHealthStatus", getAcademicHealthStatus());
        
        // Distributions
        report.put("courseDistribution", courseDistribution);
        report.put("genderDistribution", genderDistribution);
        report.put("statusDistribution", statusDistribution);
        report.put("academicStandingDistribution", academicStandingDistribution);
        
        // Analysis
        report.put("mostPopularCourse", getMostPopularCourse());
        report.put("leastPopularCourse", getLeastPopularCourse());
        report.put("genderRatio", getGenderRatio());
        report.put("topPerformersCount", topPerformers.size());
        report.put("atRiskStudentsCount", atRiskStudents.size());
        
        // Recommendations
        report.put("recommendations", getRecommendations());
        
        // Performance metrics
        report.put("courseAverageGPAs", courseAverageGPAs);
        report.put("enrollmentTrends", enrollmentTrends);
        
        return report;
    }
    
    public String generateTextReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("STUDENT MANAGEMENT SYSTEM - STATISTICS REPORT\n");
        report.append("============================================\n\n");
        
        // Overview
        report.append("OVERVIEW:\n");
        report.append("---------\n");
        report.append("Total Students: ").append(totalStudents).append("\n");
        report.append("Average GPA: ").append(String.format("%.2f", averageGPA)).append("\n");
        report.append("Overall Performance: ").append(getOverallPerformanceRating()).append("\n");
        report.append("Academic Health: ").append(getAcademicHealthStatus()).append("\n\n");
        
        // Course Distribution
        report.append("COURSE DISTRIBUTION:\n");
        report.append("--------------------\n");
        courseDistribution.forEach((course, count) -> {
            double percentage = (double) count / totalStudents * 100;
            report.append(String.format("%-25s: %3d students (%5.1f%%)%n", course, count, percentage));
        });
        report.append("\n");
        
        // Gender Distribution
        report.append("GENDER DISTRIBUTION:\n");
        report.append("-------------------\n");
        genderDistribution.forEach((gender, count) -> {
            double percentage = (double) count / totalStudents * 100;
            report.append(String.format("%-10s: %3d students (%5.1f%%)%n", gender, count, percentage));
        });
        report.append("\n");
        
        // Academic Standing
        report.append("ACADEMIC STANDING:\n");
        report.append("------------------\n");
        academicStandingDistribution.forEach((standing, count) -> {
            double percentage = (double) count / totalStudents * 100;
            report.append(String.format("%-20s: %3d students (%5.1f%%)%n", standing, count, percentage));
        });
        report.append("\n");
        
        // Top Performers
        if (!topPerformers.isEmpty()) {
            report.append("TOP PERFORMERS:\n");
            report.append("---------------\n");
            topPerformers.stream()
                .limit(10)
                .forEach(perf -> report.append(String.format("%-20s: GPA %.2f (%s)%n", 
                    perf.getStudentName(), perf.getGpa(), perf.getCourseName())));
            report.append("\n");
        }
        
        // At-Risk Students
        if (!atRiskStudents.isEmpty()) {
            report.append("AT-RISK STUDENTS:\n");
            report.append("-----------------\n");
            report.append("Total: ").append(atRiskStudents.size()).append(" students\n");
            report.append("Percentage: ").append(String.format("%.1f", 
                (double) atRiskStudents.size() / totalStudents * 100)).append("%\n\n");
        }
        
        // Recommendations
        List<String> recommendations = getRecommendations();
        if (!recommendations.isEmpty()) {
            report.append("RECOMMENDATIONS:\n");
            report.append("----------------\n");
            for (int i = 0; i < recommendations.size(); i++) {
                report.append(i + 1).append(". ").append(recommendations.get(i)).append("\n");
            }
            report.append("\n");
        }
        
        return report.toString();
    }
}

/**
 * Student performance class for tracking individual student performance
 */
class StudentPerformance {
    private String studentName;
    private String rollNumber;
    private String courseName;
    private BigDecimal gpa;
    private int totalCredits;
    private String academicStanding;
    private String status;
    
    public StudentPerformance(String studentName, String rollNumber, String courseName, 
                            BigDecimal gpa, int totalCredits, String academicStanding, String status) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.courseName = courseName;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.academicStanding = academicStanding;
        this.status = status;
    }
    
    // Getters
    public String getStudentName() { return studentName; }
    public String getRollNumber() { return rollNumber; }
    public String getCourseName() { return courseName; }
    public BigDecimal getGpa() { return gpa; }
    public int getTotalCredits() { return totalCredits; }
    public String getAcademicStanding() { return academicStanding; }
    public String getStatus() { return status; }
}
