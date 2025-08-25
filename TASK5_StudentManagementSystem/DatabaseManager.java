import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:student_management.db";
    private static final String CONFIG_FILE = "database.properties";
    private static DatabaseManager instance;
    private Connection connection;
    
    // Database configuration
    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECTION_TIMEOUT = 30;
    private static final int QUERY_TIMEOUT = 15;
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try {
            // Load database properties
            Properties props = loadDatabaseProperties();
            
            // Create database and tables
            Connection conn = getConnection();
            try {
                createTables(conn);
                
                // Initialize admin user if not exists
                initializeAdminUser(conn);
                
                // Commit all changes
                conn.commit();
                System.out.println("Database initialized successfully!");
                
            } catch (SQLException e) {
                // Rollback on error
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back: " + rollbackEx.getMessage());
                }
                throw e;
            } finally {
                // Don't close the connection here, just reset autoCommit
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException e) {
                    System.err.println("Error resetting autoCommit: " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            fis.close();
        } catch (IOException e) {
            // Use default properties if config file not found
            props.setProperty("db.url", DB_URL);
            props.setProperty("db.max_connections", String.valueOf(MAX_CONNECTIONS));
            props.setProperty("db.connection_timeout", String.valueOf(CONNECTION_TIMEOUT));
            props.setProperty("db.query_timeout", String.valueOf(QUERY_TIMEOUT));
        }
        return props;
    }
    
    private void createTables(Connection conn) throws SQLException {
        System.out.println("Starting table creation...");
        
        // Users table for authentication
        String createUsersTable = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password_hash VARCHAR(255) NOT NULL," +
            "email VARCHAR(100) UNIQUE NOT NULL," +
            "role VARCHAR(20) NOT NULL DEFAULT 'USER'," +
            "is_active BOOLEAN DEFAULT TRUE," +
            "last_login TIMESTAMP," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        // Students table with advanced features
        String createStudentsTable = 
            "CREATE TABLE IF NOT EXISTS students (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "roll_number VARCHAR(20) UNIQUE NOT NULL," +
            "name VARCHAR(100) NOT NULL," +
            "email VARCHAR(100) UNIQUE NOT NULL," +
            "phone VARCHAR(15)," +
            "date_of_birth DATE NOT NULL," +
            "gender VARCHAR(10) NOT NULL," +
            "address TEXT," +
            "city VARCHAR(50)," +
            "state VARCHAR(50)," +
            "country VARCHAR(50) DEFAULT 'India'," +
            "postal_code VARCHAR(10)," +
            "course_id INTEGER," +
            "enrollment_date DATE DEFAULT CURRENT_DATE," +
            "graduation_date DATE," +
            "status VARCHAR(20) DEFAULT 'ACTIVE'," +
            "gpa DECIMAL(3,2) DEFAULT 0.00," +
            "total_credits INTEGER DEFAULT 0," +
            "emergency_contact_name VARCHAR(100)," +
            "emergency_contact_phone VARCHAR(15)," +
            "emergency_contact_relationship VARCHAR(50)," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "created_by INTEGER," +
            "updated_by INTEGER" +
            ")";
        
        // Courses table
        String createCoursesTable = 
            "CREATE TABLE IF NOT EXISTS courses (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "code VARCHAR(20) UNIQUE NOT NULL," +
            "name VARCHAR(100) NOT NULL," +
            "description TEXT," +
            "duration_months INTEGER NOT NULL," +
            "total_credits INTEGER NOT NULL," +
            "department VARCHAR(50) NOT NULL," +
            "is_active BOOLEAN DEFAULT TRUE," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        // Subjects table
        String createSubjectsTable = 
            "CREATE TABLE IF NOT EXISTS subjects (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "code VARCHAR(20) UNIQUE NOT NULL," +
            "name VARCHAR(100) NOT NULL," +
            "description TEXT," +
            "credits INTEGER NOT NULL," +
            "course_id INTEGER NOT NULL," +
            "semester INTEGER NOT NULL," +
            "is_core BOOLEAN DEFAULT TRUE," +
            "is_active BOOLEAN DEFAULT TRUE," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        // Student grades table
        String createGradesTable = 
            "CREATE TABLE IF NOT EXISTS student_grades (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "student_id INTEGER NOT NULL," +
            "subject_id INTEGER NOT NULL," +
            "semester INTEGER NOT NULL," +
            "academic_year VARCHAR(9) NOT NULL," +
            "marks_obtained DECIMAL(5,2) NOT NULL," +
            "max_marks DECIMAL(5,2) DEFAULT 100.00," +
            "grade VARCHAR(2)," +
            "grade_points DECIMAL(3,2)," +
            "attendance_percentage DECIMAL(5,2)," +
            "remarks TEXT," +
            "graded_by INTEGER," +
            "graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        // Audit log table
        String createAuditTable = 
            "CREATE TABLE IF NOT EXISTS audit_log (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "action VARCHAR(100) NOT NULL," +
            "table_name VARCHAR(50) NOT NULL," +
            "record_id INTEGER," +
            "old_values TEXT," +
            "new_values TEXT," +
            "ip_address VARCHAR(45)," +
            "user_agent TEXT," +
            "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        // Execute table creation
        try (Statement stmt = conn.createStatement()) {
            System.out.println("Creating users table...");
            stmt.execute(createUsersTable);
            System.out.println("Creating courses table...");
            stmt.execute(createCoursesTable);
            System.out.println("Creating subjects table...");
            stmt.execute(createSubjectsTable);
            System.out.println("Creating students table...");
            stmt.execute(createStudentsTable);
            System.out.println("Creating grades table...");
            stmt.execute(createGradesTable);
            System.out.println("Creating audit table...");
            stmt.execute(createAuditTable);
            System.out.println("All tables created successfully!");
        }
        
        // Create indexes for better performance
        createIndexes(conn);
    }
    
    private void createIndexes(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Performance indexes
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_students_roll_number ON students(roll_number)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_students_email ON students(email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_students_course ON students(course_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_grades_student ON student_grades(student_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_grades_subject ON student_grades(subject_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_log(timestamp)");
        }
    }
    
    private void initializeAdminUser(Connection conn) throws SQLException {
        // Check if admin user exists
        String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (PreparedStatement pstmt = conn.prepareStatement(checkAdmin)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Create admin user with hashed password
                String createAdmin = """
                    INSERT INTO users (username, password_hash, email, role, is_active)
                    VALUES (?, ?, ?, 'ADMIN', TRUE)
                """;
                
                try (PreparedStatement adminStmt = conn.prepareStatement(createAdmin)) {
                    adminStmt.setString(1, "admin");
                    adminStmt.setString(2, PasswordHasher.hashPassword("admin123"));
                    adminStmt.setString(3, "admin@studentmanagement.com");
                    adminStmt.executeUpdate();
                    
                    System.out.println("Admin user created successfully!");
                    System.out.println("Username: admin");
                    System.out.println("Password: admin123");
                }
            }
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(false); // Enable transaction support
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    public void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
        }
    }
    
    public void rollbackTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }
    
    // Advanced query methods
    public List<Student> searchStudentsAdvanced(String searchTerm, String searchType, 
                                              String courseFilter, String statusFilter) throws SQLException {
        List<Student> students = new ArrayList<>();
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT s.*, c.name as course_name, c.code as course_code ");
        query.append("FROM students s ");
        query.append("LEFT JOIN courses c ON s.course_id = c.id ");
        query.append("WHERE 1=1 ");
        
        List<Object> parameters = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            switch (searchType.toLowerCase()) {
                case "name":
                    query.append("AND s.name LIKE ? ");
                    parameters.add("%" + searchTerm + "%");
                    break;
                case "roll_number":
                    query.append("AND s.roll_number LIKE ? ");
                    parameters.add("%" + searchTerm + "%");
                    break;
                case "email":
                    query.append("AND s.email LIKE ? ");
                    parameters.add("%" + searchTerm + "%");
                    break;
                case "phone":
                    query.append("AND s.phone LIKE ? ");
                    parameters.add("%" + searchTerm + "%");
                    break;
            }
        }
        
        if (courseFilter != null && !courseFilter.trim().isEmpty()) {
            query.append("AND c.name = ? ");
            parameters.add(courseFilter);
        }
        
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            query.append("AND s.status = ? ");
            parameters.add(statusFilter);
        }
        
        query.append("ORDER BY s.name ASC");
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = mapResultSetToStudent(rs);
                    students.add(student);
                }
            }
        }
        
        return students;
    }
    
    public StudentStatistics getStudentStatistics() throws SQLException {
        StudentStatistics stats = new StudentStatistics();
        
        try (Connection conn = getConnection()) {
            // Total students
            String totalQuery = "SELECT COUNT(*) FROM students WHERE status = 'ACTIVE'";
            try (PreparedStatement pstmt = conn.prepareStatement(totalQuery)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.setTotalStudents(rs.getInt(1));
                }
            }
            
            // Students by course
            String courseQuery = """
                SELECT c.name, COUNT(s.id) as student_count
                FROM courses c
                LEFT JOIN students s ON c.id = s.course_id AND s.status = 'ACTIVE'
                WHERE c.is_active = TRUE
                GROUP BY c.id, c.name
                ORDER BY student_count DESC
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(courseQuery)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    stats.addCourseDistribution(rs.getString("name"), rs.getInt("student_count"));
                }
            }
            
            // Average GPA
            String gpaQuery = "SELECT AVG(gpa) FROM students WHERE status = 'ACTIVE' AND gpa > 0";
            try (PreparedStatement pstmt = conn.prepareStatement(gpaQuery)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.setAverageGPA(rs.getDouble(1));
                }
            }
            
            // Gender distribution
            String genderQuery = """
                SELECT gender, COUNT(*) as count
                FROM students
                WHERE status = 'ACTIVE'
                GROUP BY gender
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(genderQuery)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    stats.addGenderDistribution(rs.getString("gender"), rs.getInt("count"));
                }
            }
        }
        
        return stats;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setCity(rs.getString("city"));
        student.setState(rs.getString("state"));
        student.setCountry(rs.getString("country"));
        student.setPostalCode(rs.getString("postal_code"));
        student.setCourseId(rs.getInt("course_id"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setGraduationDate(rs.getDate("graduation_date"));
        student.setStatus(rs.getString("status"));
        student.setGpa(rs.getBigDecimal("gpa"));
        student.setTotalCredits(rs.getInt("total_credits"));
        student.setEmergencyContactName(rs.getString("emergency_contact_name"));
        student.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        student.setEmergencyContactRelationship(rs.getString("emergency_contact_relationship"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        student.setCreatedBy(rs.getInt("created_by"));
        student.setUpdatedBy(rs.getInt("updated_by"));
        
        // Set course name if available
        try {
            student.setCourseName(rs.getString("course_name"));
        } catch (SQLException e) {
            // Course name column might not exist in some queries
        }
        
        return student;
    }
    
    public void logAuditTrail(int userId, String action, String tableName, 
                             Integer recordId, String oldValues, String newValues, 
                             String ipAddress, String userAgent) throws SQLException {
        String auditQuery = """
            INSERT INTO audit_log (user_id, action, table_name, record_id, old_values, new_values, ip_address, user_agent)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(auditQuery)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, tableName);
            pstmt.setObject(4, recordId);
            pstmt.setString(5, oldValues);
            pstmt.setString(6, newValues);
            pstmt.setString(7, ipAddress);
            pstmt.setString(8, userAgent);
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Generate a unique roll number for a new student
     * Format: [COURSE_CODE][YEAR][SEQUENTIAL_NUMBER]
     * Example: CS2024001, IT2024001, ME2024001
     */
    public String generateRollNumber(Integer courseId) {
        try {
            // Get course code
            String courseCode = getCourseCode(courseId);
            if (courseCode == null) {
                courseCode = "GEN"; // Default for unknown courses
            }
            
            // Get current year (last 2 digits)
            String currentYear = String.valueOf(java.time.LocalDate.now().getYear()).substring(2);
            
            // Get next sequential number for this course and year
            String nextNumber = getNextRollNumberSequence(courseCode, currentYear);
            
            return courseCode + currentYear + nextNumber;
            
        } catch (SQLException e) {
            System.err.println("Error generating roll number: " + e.getMessage());
            // Fallback: timestamp-based roll number
            return "STU" + System.currentTimeMillis() % 10000;
        }
    }
    
    /**
     * Get the course code for a given course ID
     */
    private String getCourseCode(Integer courseId) throws SQLException {
        if (courseId == null) return "GEN";
        
        String sql = "SELECT code FROM courses WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("code");
                }
            }
        }
        return "GEN";
    }
    
    /**
     * Get the next sequential number for roll number generation
     */
    private String getNextRollNumberSequence(String courseCode, String year) throws SQLException {
        String pattern = courseCode + year + "%";
        String sql = "SELECT roll_number FROM students WHERE roll_number LIKE ? ORDER BY roll_number DESC LIMIT 1";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastRollNumber = rs.getString("roll_number");
                    // Extract the sequential number part
                    String seqPart = lastRollNumber.substring(courseCode.length() + year.length());
                    try {
                        int nextSeq = Integer.parseInt(seqPart) + 1;
                        return String.format("%03d", nextSeq); // 3-digit format with leading zeros
                    } catch (NumberFormatException e) {
                        // If parsing fails, start with 001
                        return "001";
                    }
                }
            }
        }
        return "001"; // First student for this course and year
    }
    
    /**
     * Check if a roll number already exists
     */
    public boolean rollNumberExists(String rollNumber) {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, rollNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking roll number existence: " + e.getMessage());
        }
        return false;
    }
}
