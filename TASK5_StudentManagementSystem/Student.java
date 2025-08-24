import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Student {
    // Primary identification
    private Integer id;
    private String rollNumber;
    private String name;
    
    // Contact information
    private String email;
    private String phone;
    
    // Personal information
    private Date dateOfBirth;
    private String gender;
    private Integer age;
    
    // Address information
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    
    // Academic information
    private Integer courseId;
    private String courseName;
    private Date enrollmentDate;
    private Date graduationDate;
    private String status;
    private BigDecimal gpa;
    private Integer totalCredits;
    
    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    
    // System fields
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
    
    // Validation constants
    private static final int MIN_AGE = 15;
    private static final int MAX_AGE = 100;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ADDRESS_LENGTH = 500;
    private static final int MAX_PHONE_LENGTH = 15;
    private static final int MAX_EMAIL_LENGTH = 100;
    
    // Status constants
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_GRADUATED = "GRADUATED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_WITHDRAWN = "WITHDRAWN";
    
    // Gender constants
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_OTHER = "Other";
    
    // Default constructor
    public Student() {
        this.status = STATUS_ACTIVE;
        this.country = "India";
        this.gpa = BigDecimal.ZERO;
        this.totalCredits = 0;
        this.enrollmentDate = Date.valueOf(LocalDate.now());
    }
    
    // Parameterized constructor
    public Student(String rollNumber, String name, String email, Date dateOfBirth, 
                  String gender, Integer courseId) {
        this();
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.courseId = courseId;
        this.calculateAge();
    }
    
    // Business logic methods
    
    /**
     * Calculate student's age based on date of birth
     */
    public void calculateAge() {
        if (this.dateOfBirth != null) {
            LocalDate birthDate = this.dateOfBirth.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthDate, currentDate);
            this.age = period.getYears();
        }
    }
    
    /**
     * Check if student is eligible for graduation
     */
    public boolean isEligibleForGraduation() {
        if (this.status.equals(STATUS_ACTIVE) && this.totalCredits != null && this.gpa != null) {
            // Assuming 120 credits required for graduation and minimum GPA of 2.0
            return this.totalCredits >= 120 && this.gpa.compareTo(new BigDecimal("2.0")) >= 0;
        }
        return false;
    }
    
    /**
     * Calculate academic standing based on GPA
     */
    public String getAcademicStanding() {
        if (this.gpa == null) return "Not Available";
        
        if (this.gpa.compareTo(new BigDecimal("3.5")) >= 0) {
            return "Dean's List";
        } else if (this.gpa.compareTo(new BigDecimal("3.0")) >= 0) {
            return "Good Standing";
        } else if (this.gpa.compareTo(new BigDecimal("2.0")) >= 0) {
            return "Academic Warning";
        } else {
            return "Academic Probation";
        }
    }
    
    /**
     * Check if student is on academic probation
     */
    public boolean isOnAcademicProbation() {
        return this.gpa != null && this.gpa.compareTo(new BigDecimal("2.0")) < 0;
    }
    
    /**
     * Get student's enrollment duration in months
     */
    public int getEnrollmentDurationMonths() {
        if (this.enrollmentDate != null) {
            LocalDate enrollment = this.enrollmentDate.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(enrollment, currentDate);
            return period.getYears() * 12 + period.getMonths();
        }
        return 0;
    }
    
    /**
     * Check if student can enroll in new semester
     */
    public boolean canEnrollInNewSemester() {
        return this.status.equals(STATUS_ACTIVE) && 
               !this.isOnAcademicProbation() && 
               this.getEnrollmentDurationMonths() < 60; // 5 years max
    }
    
    /**
     * Validate student data
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        // Roll number validation
        if (this.rollNumber == null || this.rollNumber.trim().isEmpty()) {
            result.addError("Roll number is required");
        } else if (this.rollNumber.length() < 3 || this.rollNumber.length() > 20) {
            result.addError("Roll number must be between 3 and 20 characters");
        }
        
        // Name validation
        if (this.name == null || this.name.trim().isEmpty()) {
            result.addError("Name is required");
        } else if (this.name.length() < MIN_NAME_LENGTH || this.name.length() > MAX_NAME_LENGTH) {
            result.addError("Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
        } else if (!this.name.matches("^[a-zA-Z\\s\\.'-]+$")) {
            result.addError("Name contains invalid characters");
        }
        
        // Email validation
        if (this.email == null || this.email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (this.email.length() > MAX_EMAIL_LENGTH) {
            result.addError("Email is too long (max " + MAX_EMAIL_LENGTH + " characters)");
        } else if (!this.email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.addError("Invalid email format");
        }
        
        // Date of birth validation
        if (this.dateOfBirth == null) {
            result.addError("Date of birth is required");
        } else {
            LocalDate birthDate = this.dateOfBirth.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            
            if (birthDate.isAfter(currentDate)) {
                result.addError("Date of birth cannot be in the future");
            } else {
                Period period = Period.between(birthDate, currentDate);
                if (period.getYears() < MIN_AGE || period.getYears() > MAX_AGE) {
                    result.addError("Age must be between " + MIN_AGE + " and " + MAX_AGE + " years");
                }
            }
        }
        
        // Gender validation
        if (this.gender == null || this.gender.trim().isEmpty()) {
            result.addError("Gender is required");
        } else if (!this.gender.equals(GENDER_MALE) && 
                   !this.gender.equals(GENDER_FEMALE) && 
                   !this.gender.equals(GENDER_OTHER)) {
            result.addError("Invalid gender selection");
        }
        
        // Phone validation
        if (this.phone != null && !this.phone.trim().isEmpty()) {
            if (this.phone.length() > MAX_PHONE_LENGTH) {
                result.addError("Phone number is too long (max " + MAX_PHONE_LENGTH + " characters)");
            } else if (!this.phone.matches("^[+]?[0-9\\s\\-\\(\\)]+$")) {
                result.addError("Invalid phone number format");
            }
        }
        
        // Address validation
        if (this.address != null && this.address.length() > MAX_ADDRESS_LENGTH) {
            result.addError("Address is too long (max " + MAX_ADDRESS_LENGTH + " characters)");
        }
        
        // GPA validation
        if (this.gpa != null) {
            if (this.gpa.compareTo(BigDecimal.ZERO) < 0 || this.gpa.compareTo(new BigDecimal("4.0")) > 0) {
                result.addError("GPA must be between 0.0 and 4.0");
            }
        }
        
        // Credits validation
        if (this.totalCredits != null && this.totalCredits < 0) {
            result.addError("Total credits cannot be negative");
        }
        
        return result;
    }
    
    /**
     * Check if student data is complete for enrollment
     */
    public boolean isEnrollmentComplete() {
        return this.rollNumber != null && !this.rollNumber.trim().isEmpty() &&
               this.name != null && !this.name.trim().isEmpty() &&
               this.email != null && !this.email.trim().isEmpty() &&
               this.dateOfBirth != null &&
               this.gender != null && !this.gender.trim().isEmpty() &&
               this.courseId != null &&
               this.emergencyContactName != null && !this.emergencyContactName.trim().isEmpty() &&
               this.emergencyContactPhone != null && !this.emergencyContactPhone.trim().isEmpty();
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { 
        this.dateOfBirth = dateOfBirth; 
        this.calculateAge();
    }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public Date getGraduationDate() { return graduationDate; }
    public void setGraduationDate(Date graduationDate) { this.graduationDate = graduationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getGpa() { return gpa; }
    public void setGpa(BigDecimal gpa) { this.gpa = gpa; }
    
    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    
    // Override methods
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", graduationDate=" + graduationDate +
                ", status='" + status + '\'' +
                ", gpa=" + gpa +
                ", totalCredits=" + totalCredits +
                ", emergencyContactName='" + emergencyContactName + '\'' +
                ", emergencyContactPhone='" + emergencyContactPhone + '\'' +
                ", emergencyContactRelationship='" + emergencyContactRelationship + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return Objects.equals(rollNumber, student.rollNumber) &&
               Objects.equals(email, student.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(rollNumber, email);
    }
}

/**
 * Validation result class for student data validation
 */
class ValidationResult {
    private boolean isValid = true;
    private java.util.List<String> errors = new java.util.ArrayList<>();
    
    public void addError(String error) {
        this.isValid = false;
        this.errors.add(error);
    }
    
    public boolean isValid() { return isValid; }
    
    public java.util.List<String> getErrors() { return errors; }
    
    public String getErrorMessage() {
        return String.join("; ", errors);
    }
}
