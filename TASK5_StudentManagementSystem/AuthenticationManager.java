import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationManager {
    private static AuthenticationManager instance;
    private final DatabaseManager dbManager;
    private final Map<String, UserSession> activeSessions;
    private final SecureRandom secureRandom;
    
    // Session configuration
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    // Role constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_STAFF = "STAFF";
    
    private AuthenticationManager() {
        this.dbManager = DatabaseManager.getInstance();
        this.activeSessions = new ConcurrentHashMap<>();
        this.secureRandom = new SecureRandom();
    }
    
    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }
    
    /**
     * Authenticate user login
     */
    public LoginResult authenticateUser(String username, String password, String ipAddress, String userAgent) {
        try {
            // Check if user is locked out
            if (isUserLockedOut(username)) {
                return new LoginResult(false, "Account is temporarily locked due to multiple failed attempts. Please try again later.", null);
            }
            
            // Verify credentials
            User user = verifyCredentials(username, password);
            if (user == null) {
                // Increment failed login attempts
                incrementFailedLoginAttempts(username);
                return new LoginResult(false, "Invalid username or password.", null);
            }
            
            // Check if user account is active
            if (!user.isActive()) {
                return new LoginResult(false, "Account is deactivated. Please contact administrator.", null);
            }
            
            // Reset failed login attempts on successful login
            resetFailedLoginAttempts(username);
            
            // Create session
            String sessionToken = generateSessionToken();
            UserSession session = new UserSession(user, sessionToken, ipAddress, userAgent);
            activeSessions.put(sessionToken, session);
            
            // Update last login time
            updateLastLoginTime(user.getId());
            
            // Log successful login
            logLoginActivity(user.getId(), "LOGIN_SUCCESS", ipAddress, userAgent);
            
            return new LoginResult(true, "Login successful!", session);
            
        } catch (SQLException e) {
            logLoginActivity(0, "LOGIN_ERROR", ipAddress, userAgent);
            return new LoginResult(false, "System error occurred. Please try again later.", null);
        }
    }
    
    /**
     * Verify user credentials against database
     */
    private User verifyCredentials(String username, String password) throws SQLException {
        String query = """
            SELECT id, username, password_hash, email, role, is_active, last_login
            FROM users 
            WHERE username = ? AND is_active = TRUE
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // Verify password hash
                    if (PasswordHasher.verifyPassword(password, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        user.setActive(rs.getBoolean("is_active"));
                        user.setLastLogin(rs.getTimestamp("last_login"));
                        return user;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Check if user account is locked out
     */
    private boolean isUserLockedOut(String username) {
        // This would typically check a database table for failed login attempts
        // For now, we'll use a simple in-memory approach
        return false; // Placeholder implementation
    }
    
    /**
     * Increment failed login attempts
     */
    private void incrementFailedLoginAttempts(String username) {
        // This would typically update a database table
        // For now, we'll use a simple in-memory approach
    }
    
    /**
     * Reset failed login attempts
     */
    private void resetFailedLoginAttempts(String username) {
        // This would typically update a database table
        // For now, we'll use a simple in-memory approach
    }
    
    /**
     * Generate secure session token
     */
    private String generateSessionToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }
    
    /**
     * Update user's last login time
     */
    private void updateLastLoginTime(int userId) throws SQLException {
        String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Log login activity for audit trail
     */
    private void logLoginActivity(int userId, String action, String ipAddress, String userAgent) {
        try {
            dbManager.logAuditTrail(userId, action, "users", userId, null, null, ipAddress, userAgent);
        } catch (SQLException e) {
            System.err.println("Failed to log login activity: " + e.getMessage());
        }
    }
    
    /**
     * Validate session token
     */
    public UserSession validateSession(String sessionToken) {
        UserSession session = activeSessions.get(sessionToken);
        
        if (session == null) {
            return null;
        }
        
        // Check if session has expired
        if (session.isExpired()) {
            activeSessions.remove(sessionToken);
            return null;
        }
        
        // Extend session if needed
        session.extendSession();
        return session;
    }
    
    /**
     * Logout user and invalidate session
     */
    public boolean logoutUser(String sessionToken, String ipAddress) {
        UserSession session = activeSessions.remove(sessionToken);
        
        if (session != null) {
            try {
                logLoginActivity(session.getUser().getId(), "LOGOUT", ipAddress, null);
                return true;
            } catch (Exception e) {
                System.err.println("Failed to log logout activity: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Check if user has required permission
     */
    public boolean hasPermission(String sessionToken, String requiredPermission) {
        UserSession session = validateSession(sessionToken);
        if (session == null) {
            return false;
        }
        
        User user = session.getUser();
        return checkPermission(user.getRole(), requiredPermission);
    }
    
    /**
     * Check role-based permissions
     */
    private boolean checkPermission(String userRole, String requiredPermission) {
        Map<String, Set<String>> rolePermissions = getRolePermissions();
        
        Set<String> userPermissions = rolePermissions.get(userRole);
        if (userPermissions == null) {
            return false;
        }
        
        return userPermissions.contains(requiredPermission) || userPermissions.contains("ALL");
    }
    
    /**
     * Get role-based permissions mapping
     */
    private Map<String, Set<String>> getRolePermissions() {
        Map<String, Set<String>> permissions = new HashMap<>();
        
        // Admin permissions
        Set<String> adminPermissions = new HashSet<>(Arrays.asList(
            "ALL", "USER_MANAGEMENT", "STUDENT_MANAGEMENT", "COURSE_MANAGEMENT",
            "GRADE_MANAGEMENT", "SYSTEM_CONFIGURATION", "AUDIT_LOGS", "REPORTS"
        ));
        permissions.put(ROLE_ADMIN, adminPermissions);
        
        // Teacher permissions
        Set<String> teacherPermissions = new HashSet<>(Arrays.asList(
            "STUDENT_VIEW", "GRADE_MANAGEMENT", "COURSE_VIEW", "REPORTS"
        ));
        permissions.put(ROLE_TEACHER, teacherPermissions);
        
        // Student permissions
        Set<String> studentPermissions = new HashSet<>(Arrays.asList(
            "PROFILE_VIEW", "GRADE_VIEW", "COURSE_VIEW"
        ));
        permissions.put(ROLE_STUDENT, studentPermissions);
        
        // Staff permissions
        Set<String> staffPermissions = new HashSet<>(Arrays.asList(
            "STUDENT_VIEW", "STUDENT_EDIT", "COURSE_VIEW", "REPORTS"
        ));
        permissions.put(ROLE_STAFF, staffPermissions);
        
        return permissions;
    }
    
    /**
     * Get all active sessions (admin only)
     */
    public List<UserSession> getActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }
    
    /**
     * Force logout user by session token (admin only)
     */
    public boolean forceLogoutUser(String sessionToken) {
        return activeSessions.remove(sessionToken) != null;
    }
    
    /**
     * Clean up expired sessions
     */
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(String sessionToken, String currentPassword, String newPassword) {
        UserSession session = validateSession(sessionToken);
        if (session == null) {
            return false;
        }
        
        try {
            // Verify current password
            User user = verifyCredentials(session.getUser().getUsername(), currentPassword);
            if (user == null) {
                return false;
            }
            
            // Hash new password
            String newPasswordHash = PasswordHasher.hashPassword(newPassword);
            
            // Update password in database
            String query = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, newPasswordHash);
                pstmt.setInt(2, user.getId());
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    dbManager.commitTransaction();
                    
                    // Log password change
                    logLoginActivity(user.getId(), "PASSWORD_CHANGE", null, null);
                    return true;
                }
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollbackTransaction();
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Failed to change password: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create new user account (admin only)
     */
    public boolean createUser(String sessionToken, String username, String password, String email, String role) {
        if (!hasPermission(sessionToken, "USER_MANAGEMENT")) {
            return false;
        }
        
        try {
            String passwordHash = PasswordHasher.hashPassword(password);
            
            String query = """
                INSERT INTO users (username, password_hash, email, role, is_active, created_at, updated_at)
                VALUES (?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;
            
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, username);
                pstmt.setString(2, passwordHash);
                pstmt.setString(3, email);
                pstmt.setString(4, role);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    dbManager.commitTransaction();
                    
                    // Log user creation
                    UserSession session = validateSession(sessionToken);
                    if (session != null) {
                        logLoginActivity(session.getUser().getId(), "USER_CREATED", null, null);
                    }
                    return true;
                }
            }
            
        } catch (SQLException e) {
            try {
                dbManager.rollbackTransaction();
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Failed to create user: " + e.getMessage());
        }
        
        return false;
    }
}

/**
 * User class representing authenticated user
 */
class User {
    private int id;
    private String username;
    private String email;
    private String role;
    private boolean isActive;
    private Timestamp lastLogin;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
}

/**
 * User session class for managing active sessions
 */
class UserSession {
    private final User user;
    private final String sessionToken;
    private final String ipAddress;
    private final String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    
    public UserSession(User user, String sessionToken, String ipAddress, String userAgent) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        long minutesSinceLastActivity = ChronoUnit.MINUTES.between(lastActivity, now);
        return minutesSinceLastActivity > AuthenticationManager.SESSION_TIMEOUT_MINUTES;
    }
    
    public void extendSession() {
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters
    public User getUser() { return user; }
    public String getSessionToken() { return sessionToken; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActivity() { return lastActivity; }
}

/**
 * Login result class for authentication responses
 */
class LoginResult {
    private final boolean success;
    private final String message;
    private final UserSession session;
    
    public LoginResult(boolean success, String message, UserSession session) {
        this.success = success;
        this.message = message;
        this.session = session;
    }
    
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserSession getSession() { return session; }
}
