import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;

public class StudentManagementSystem extends Application {
    
    private AuthenticationManager authManager;
    private DatabaseManager dbManager;
    private UserSession currentSession;
    
    // UI Components
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;
    private TabPane mainTabPane;
    
    // Student management
    private ObservableList<Student> studentsList;
    private TableView<Student> studentsTable;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        try {
            // Initialize managers
            initializeManagers();
            
            // Create login scene
            createLoginScene();
            
            // Set up primary stage
            primaryStage.setTitle("Student Management System - Professional Edition");
            primaryStage.setScene(loginScene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
            
        } catch (Exception e) {
            showError("System Initialization Error", "Failed to initialize system: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void initializeManagers() {
        try {
            dbManager = DatabaseManager.getInstance();
            authManager = AuthenticationManager.getInstance();
            studentsList = FXCollections.observableArrayList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize managers", e);
        }
    }
    
    private void createLoginScene() {
        VBox loginLayout = new VBox(30);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));
        loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #3498db);");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Student Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        
        Label subtitleLabel = new Label("Professional Edition with Advanced Security");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Login form
        VBox loginForm = new VBox(20);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(400);
        loginForm.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10; -fx-padding: 30;");
        
        Label loginTitle = new Label("Login Required");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginTitle.setStyle("-fx-text-fill: #2c3e50;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 16;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 16;");
        
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        
        // Demo credentials info
        VBox demoInfo = new VBox(5);
        demoInfo.setAlignment(Pos.CENTER);
        demoInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label demoLabel = new Label("Demo Credentials:");
        demoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        demoLabel.setStyle("-fx-text-fill: #495057;");
        
        Label usernameLabel = new Label("Username: admin");
        usernameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        usernameLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Label passwordLabel = new Label("Password: admin123");
        passwordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        passwordLabel.setStyle("-fx-text-fill: #6c757d;");
        
        demoInfo.getChildren().addAll(demoLabel, usernameLabel, passwordLabel);
        
        // Login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showError("Login Error", "Please enter both username and password.");
                return;
            }
            
            // Simulate IP address and user agent (in real app, get from system)
            String ipAddress = "127.0.0.1";
            String userAgent = "JavaFX Application";
            
            LoginResult result = authManager.authenticateUser(username, password, ipAddress, userAgent);
            
            if (result.isSuccess()) {
                currentSession = result.getSession();
                showSuccess("Login Successful", "Welcome, " + currentSession.getUser().getUsername() + "!");
                createMainScene();
                primaryStage.setScene(mainScene);
            } else {
                showError("Login Failed", result.getMessage());
            }
        });
        
        loginForm.getChildren().addAll(loginTitle, usernameField, passwordField, loginButton, demoInfo);
        
        loginLayout.getChildren().addAll(header, loginForm);
        loginScene = new Scene(loginLayout, 1000, 700);
    }
    
    private void createMainScene() {
        BorderPane mainLayout = new BorderPane();
        
        // Create header with user info and logout
        VBox header = createMainHeader();
        mainLayout.setTop(header);
        
        // Create main content area
        mainTabPane = createMainTabPane();
        mainLayout.setCenter(mainTabPane);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);
        
        mainScene = new Scene(mainLayout, 1200, 800);
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        // Load initial data
        loadInitialData();
    }
    
    private VBox createMainHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db);");
        
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(20);
        
        Label titleLabel = new Label("Student Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        
        HBox userInfo = new HBox(10);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        
        Label userLabel = new Label("User: " + currentSession.getUser().getUsername());
        userLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        userLabel.setStyle("-fx-text-fill: Color.LIGHTGRAY;");
        
        Label roleLabel = new Label("Role: " + currentSession.getUser().getRole());
        roleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        roleLabel.setStyle("-fx-text-fill: Color.LIGHTGRAY;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> logout());
        
        userInfo.getChildren().addAll(userLabel, roleLabel, logoutButton);
        
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        topRow.getChildren().addAll(titleLabel, userInfo);
        
        header.getChildren().add(topRow);
        return header;
    }
    
    private TabPane createMainTabPane() {
        TabPane tabPane = new TabPane();
        
        // Dashboard tab
        Tab dashboardTab = new Tab("Dashboard", createDashboardTab());
        dashboardTab.setClosable(false);
        
        // Student Management tab
        Tab studentTab = new Tab("Student Management", createStudentManagementTab());
        studentTab.setClosable(false);
        
        // Search & Reports tab
        Tab searchTab = new Tab("Search & Reports", createSearchReportsTab());
        searchTab.setClosable(false);
        
        // System Administration tab (admin only)
        if (currentSession.getUser().getRole().equals("ADMIN")) {
            Tab adminTab = new Tab("System Administration", createAdminTab());
            adminTab.setClosable(false);
            tabPane.getTabs().addAll(dashboardTab, studentTab, searchTab, adminTab);
        } else {
            tabPane.getTabs().addAll(dashboardTab, studentTab, searchTab);
        }
        
        return tabPane;
    }
    
    private VBox createDashboardTab() {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setPadding(new Insets(30));
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        
        // Title
        Label titleLabel = new Label("System Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Statistics cards
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        
        VBox totalStudentsCard = createStatCard("Total Students", "0", "#3498db");
        VBox activeStudentsCard = createStatCard("Active Students", "0", "#27ae60");
        VBox averageGPACard = createStatCard("Average GPA", "0.00", "#f39c12");
        VBox systemHealthCard = createStatCard("System Health", "Good", "#e74c3c");
        
        statsRow.getChildren().addAll(totalStudentsCard, activeStudentsCard, averageGPACard, systemHealthCard);
        
        // Quick actions
        VBox quickActions = new VBox(15);
        quickActions.setAlignment(Pos.CENTER);
        
        Label actionsLabel = new Label("Quick Actions");
        actionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        actionsLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button addStudentBtn = new Button("Add New Student");
        addStudentBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addStudentBtn.setOnAction(e -> showAddStudentDialog());
        
        Button generateReportBtn = new Button("Generate Report");
        generateReportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        generateReportBtn.setOnAction(e -> generateSystemReport());
        
        Button viewStatisticsBtn = new Button("View Statistics");
        viewStatisticsBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        viewStatisticsBtn.setOnAction(e -> showStatisticsDialog());
        
        actionButtons.getChildren().addAll(addStudentBtn, generateReportBtn, viewStatisticsBtn);
        
        quickActions.getChildren().addAll(actionsLabel, actionButtons);
        
        dashboardLayout.getChildren().addAll(titleLabel, statsRow, quickActions);
        return dashboardLayout;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.WHITE);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createStudentManagementTab() {
        VBox studentLayout = new VBox(20);
        studentLayout.setPadding(new Insets(30));
        
        // Title and controls
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(20);
        
        Label titleLabel = new Label("Student Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Button addButton = new Button("Add Student");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> showAddStudentDialog());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshButton.setOnAction(e -> refreshStudentTable());
        
        titleRow.getChildren().addAll(titleLabel, addButton, refreshButton);
        
        // Student table
        studentsTable = new TableView<>();
        studentsTable.setItems(studentsList);
        studentsTable.setEditable(false);
        studentsTable.setPrefHeight(500);
        
        // Create table columns
        createStudentTableColumns();
        
        studentLayout.getChildren().addAll(titleRow, studentsTable);
        return studentLayout;
    }
    
    private void createStudentTableColumns() {
        // Clear existing columns
        studentsTable.getColumns().clear();
        
        // Create columns
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Student, String> rollCol = new TableColumn<>("Roll Number");
        rollCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        rollCol.setPrefWidth(120);
        
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Student, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(80);
        
        TableColumn<Student, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setPrefWidth(100);
        
        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCol.setPrefWidth(150);
        
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Student, String> gpaCol = new TableColumn<>("GPA");
        gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        gpaCol.setPrefWidth(80);
        
        studentsTable.getColumns().addAll(nameCol, rollCol, emailCol, ageCol, genderCol, courseCol, statusCol, gpaCol);
    }
    
    private VBox createSearchReportsTab() {
        VBox searchLayout = new VBox(20);
        searchLayout.setPadding(new Insets(30));
        searchLayout.setAlignment(Pos.TOP_CENTER);
        
        // Title
        Label titleLabel = new Label("Search & Reports");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Search section
        VBox searchSection = new VBox(15);
        searchSection.setAlignment(Pos.CENTER);
        
        Label searchLabel = new Label("Advanced Search");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        
        ComboBox<String> searchTypeCombo = new ComboBox<>();
        searchTypeCombo.getItems().addAll("Name", "Roll Number", "Email", "Course");
        searchTypeCombo.setValue("Name");
        searchTypeCombo.setPrefWidth(150);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");
        searchField.setPrefWidth(250);
        
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        searchButton.setOnAction(e -> performAdvancedSearch(searchTypeCombo.getValue(), searchField.getText()));
        
        searchBox.getChildren().addAll(searchTypeCombo, searchField, searchButton);
        
        // Reports section
        VBox reportsSection = new VBox(15);
        reportsSection.setAlignment(Pos.CENTER);
        
        Label reportsLabel = new Label("Generate Reports");
        reportsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        HBox reportsBox = new HBox(15);
        reportsBox.setAlignment(Pos.CENTER);
        
        Button studentReportBtn = new Button("Student Report");
        studentReportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        studentReportBtn.setOnAction(e -> generateStudentReport());
        
        Button statisticsReportBtn = new Button("Statistics Report");
        statisticsReportBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        statisticsReportBtn.setOnAction(e -> generateStatisticsReport());
        
        Button exportDataBtn = new Button("Export Data");
        exportDataBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        exportDataBtn.setOnAction(e -> exportStudentData());
        
        reportsBox.getChildren().addAll(studentReportBtn, statisticsReportBtn, exportDataBtn);
        
        searchSection.getChildren().addAll(searchLabel, searchBox);
        reportsSection.getChildren().addAll(reportsLabel, reportsBox);
        
        searchLayout.getChildren().addAll(titleLabel, searchSection, reportsSection);
        return searchLayout;
    }
    
    private VBox createAdminTab() {
        VBox adminLayout = new VBox(20);
        adminLayout.setPadding(new Insets(30));
        adminLayout.setAlignment(Pos.TOP_CENTER);
        
        // Title
        Label titleLabel = new Label("System Administration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Admin controls
        VBox adminControls = new VBox(15);
        adminControls.setAlignment(Pos.CENTER);
        
        Button userManagementBtn = new Button("User Management");
        userManagementBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        userManagementBtn.setPrefWidth(200);
        userManagementBtn.setOnAction(e -> showUserManagementDialog());
        
        Button systemSettingsBtn = new Button("System Settings");
        systemSettingsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        systemSettingsBtn.setPrefWidth(200);
        systemSettingsBtn.setOnAction(e -> showSystemSettingsDialog());
        
        Button auditLogsBtn = new Button("Audit Logs");
        auditLogsBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold;");
        auditLogsBtn.setPrefWidth(200);
        auditLogsBtn.setOnAction(e -> showAuditLogsDialog());
        
        Button backupDataBtn = new Button("Backup Data");
        backupDataBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        backupDataBtn.setPrefWidth(200);
        backupDataBtn.setOnAction(e -> backupSystemData());
        
        adminControls.getChildren().addAll(userManagementBtn, systemSettingsBtn, auditLogsBtn, backupDataBtn);
        
        adminLayout.getChildren().addAll(titleLabel, adminControls);
        return adminLayout;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Label sessionLabel = new Label("Session: " + currentSession.getUser().getUsername());
        sessionLabel.setStyle("-fx-text-fill: #6c757d;");
        
        Label timeLabel = new Label("Time: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setStyle("-fx-text-fill: #6c757d;");
        
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        statusBar.getChildren().addAll(statusLabel, sessionLabel, timeLabel);
        
        return statusBar;
    }
    
    // Action methods
    private void showAddStudentDialog() {
        showInfo("Feature", "Add Student dialog will be implemented here");
    }
    
    private void performAdvancedSearch(String searchType, String searchTerm) {
        showInfo("Feature", "Advanced search will be implemented here");
    }
    
    private void generateStudentReport() {
        showInfo("Feature", "Student report generation will be implemented here");
    }
    
    private void generateStatisticsReport() {
        showInfo("Feature", "Statistics report generation will be implemented here");
    }
    
    private void exportStudentData() {
        showInfo("Feature", "Data export will be implemented here");
    }
    
    private void showUserManagementDialog() {
        showInfo("Feature", "User management will be implemented here");
    }
    
    private void showSystemSettingsDialog() {
        showInfo("Feature", "System settings will be implemented here");
    }
    
    private void showAuditLogsDialog() {
        showInfo("Feature", "Audit logs will be implemented here");
    }
    
    private void backupSystemData() {
        showInfo("Feature", "Data backup will be implemented here");
    }
    
    private void generateSystemReport() {
        showInfo("Feature", "System report generation will be implemented here");
    }
    
    private void showStatisticsDialog() {
        showInfo("Feature", "Statistics dialog will be implemented here");
    }
    
    private void refreshStudentTable() {
        showInfo("Feature", "Table refresh will be implemented here");
    }
    
    private void loadInitialData() {
        // Implementation for loading initial data
        // This would typically load students from database
    }
    
    private void logout() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Logout user
            authManager.logoutUser(currentSession.getSessionToken(), "127.0.0.1");
            currentSession = null;
            
            // Return to login screen
            primaryStage.setScene(loginScene);
        }
    }
    
    // Utility methods
    private void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
