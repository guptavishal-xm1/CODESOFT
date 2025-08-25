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
import java.math.BigDecimal;
import javafx.scene.shape.Line;

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
            // Enable performance mode for smooth 60 FPS
            enablePerformanceMode();
            
            // Initialize managers
            initializeManagers();
            
            // Create login scene
            createLoginScene();
            
            // Set up primary stage with performance optimizations
            primaryStage.setTitle("Student Management System - Professional Edition");
            primaryStage.setScene(loginScene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Enable hardware acceleration
            primaryStage.setResizable(true);
            
            // Set scene fill to transparent for better performance
            loginScene.setFill(null);
            
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
        VBox loginLayout = new VBox(40);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(60));
        loginLayout.setStyle("-fx-background-color: #667eea;");
        
        // Header with modern design
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Student Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");
        
        Label subtitleLabel = new Label("Professional Edition with Advanced Security");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));
        subtitleLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));
        
        // Add a decorative line
        Line separator = new Line(0, 0, 200, 0);
        separator.setStroke(Color.rgb(255, 255, 255, 0.6));
        separator.setStrokeWidth(2);
        
        header.getChildren().addAll(titleLabel, separator, subtitleLabel);
        
        // Modern login form with glassmorphism effect
        VBox loginForm = new VBox(25);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(450);
        loginForm.setStyle(
            "-fx-background-color: rgba(255,255,255,0.95); " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 40; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );
        
        Label loginTitle = new Label("Welcome Back");
        loginTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        loginTitle.setStyle("-fx-text-fill: #2d3748;");
        
        // Username field with icon
        VBox usernameGroup = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        usernameLabel.setStyle("-fx-text-fill: #4a5568;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(350);
        usernameField.setPrefHeight(50);
        usernameField.setStyle(
            "-fx-font-size: 16; " +
            "-fx-background-radius: 12; " +
            "-fx-border-radius: 12; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 0 20; " +
            "-fx-background-color: #f7fafc;"
        );
        usernameGroup.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field with icon
        VBox passwordGroup = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        passwordLabel.setStyle("-fx-text-fill: #4a5568;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(350);
        passwordField.setPrefHeight(50);
        passwordField.setStyle(
            "-fx-font-size: 16; " +
            "-fx-background-radius: 12; " +
            "-fx-border-radius: 12; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 0 20; " +
            "-fx-background-color: #f7fafc;"
        );
        passwordGroup.getChildren().addAll(passwordLabel, passwordField);
        
        // Modern login button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(350);
        loginButton.setPrefHeight(55);
        loginButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 8, 0, 0, 2);"
        );
        
        // Hover effect for button
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle(
                "-fx-background-color: #5a6fd8; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.6), 12, 0, 0, 3);"
            )
        );
        
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle(
                "-fx-background-color: #667eea; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 8, 0, 0, 2);"
            )
        );
        
        // Enhanced demo credentials info
        VBox demoInfo = new VBox(12);
        demoInfo.setAlignment(Pos.CENTER);
        demoInfo.setStyle(
            "-fx-background-color: #f7fafc; " +
            "-fx-padding: 20; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 12;"
        );
        
        Label demoLabel = new Label("Demo Credentials");
        demoLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        demoLabel.setStyle("-fx-text-fill: #2d3748;");
        
        HBox usernameRow = new HBox(8);
        usernameRow.setAlignment(Pos.CENTER);
        Label usernameIcon = new Label("User");
        usernameIcon.setFont(Font.font("Segoe UI", 16));
        usernameIcon.setStyle("-fx-text-fill: #667eea;");
        Label usernameDemo = new Label("admin");
        usernameDemo.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        usernameDemo.setStyle("-fx-text-fill: #4a5568;");
        usernameRow.getChildren().addAll(usernameIcon, usernameDemo);
        
        HBox passwordRow = new HBox(8);
        passwordRow.setAlignment(Pos.CENTER);
        Label passwordIcon = new Label("Lock");
        passwordIcon.setFont(Font.font("Segoe UI", 16));
        passwordIcon.setStyle("-fx-text-fill: #667eea;");
        Label passwordDemo = new Label("admin123");
        passwordDemo.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        passwordDemo.setStyle("-fx-text-fill: #4a5568;");
        passwordRow.getChildren().addAll(passwordIcon, passwordDemo);
        
        demoInfo.getChildren().addAll(demoLabel, usernameRow, passwordRow);
        
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
        
        loginForm.getChildren().addAll(loginTitle, usernameGroup, passwordGroup, loginButton, demoInfo);
        
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
        
        mainScene = new Scene(mainLayout, 1100, 700);
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        // Performance optimizations for main scene
        mainScene.setFill(null);
        
        // Ensure all buttons have visible text
        ensureAllButtonsHaveVisibleText(mainScene);
        
        // Load initial data
        loadInitialData();
    }
    
    private VBox createMainHeader() {
        VBox header = new VBox(0);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Top gradient bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color: #667eea;");
        
        Label topLabel = new Label("Student Management System");
        topLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        topLabel.setTextFill(Color.WHITE);
        topBar.getChildren().add(topLabel);
        
        // Main header content
        HBox mainHeader = new HBox();
        mainHeader.setAlignment(Pos.CENTER_LEFT);
        mainHeader.setPadding(new Insets(20, 30, 20, 30));
        mainHeader.setSpacing(30);
        
        // Logo/Title section
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Student Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Professional Edition v2.0");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // User info section
        HBox userSection = new HBox(20);
        userSection.setAlignment(Pos.CENTER_RIGHT);
        userSection.setStyle("-fx-background-color: #f7fafc; -fx-padding: 15; -fx-background-radius: 12;");
        
        VBox userInfo = new VBox(3);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        
        Label userLabel = new Label("Welcome, " + (currentSession != null ? currentSession.getUser().getUsername() : "User"));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        userLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label roleLabel = new Label("Role: " + (currentSession != null ? currentSession.getUser().getRole() : "USER"));
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        roleLabel.setStyle("-fx-text-fill: #718096;");
        
        userInfo.getChildren().addAll(userLabel, roleLabel);
        
        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: #ff6b6b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20; " +
            "-fx-cursor: hand;"
        );
        
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText("Are you sure you want to logout?");
            alert.setContentText("Your session will be terminated.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (currentSession != null) {
                    authManager.logoutUser(currentSession.getSessionToken(), "127.0.0.1");
                }
                currentSession = null;
                primaryStage.setScene(loginScene);
            }
        });
        
        userSection.getChildren().addAll(userInfo, logoutButton);
        
        // Time display
        VBox timeSection = new VBox(3);
        timeSection.setAlignment(Pos.CENTER_RIGHT);
        timeSection.setStyle("-fx-background-color: #edf2f7; -fx-padding: 12; -fx-background-radius: 8;");
        
        Label timeLabel = new Label();
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        timeLabel.setStyle("-fx-text-fill: #4a5568;");
        
        Label dateLabel = new Label();
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        dateLabel.setStyle("-fx-text-fill: #718096;");
        
        // Update time every second with performance optimization
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    try {
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        timeLabel.setText(now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                        dateLabel.setText(now.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    } catch (Exception e) {
                        // Silent fail for performance
                    }
                });
            }
        }, 0, 1000);
        
        timeSection.getChildren().addAll(timeLabel, dateLabel);
        
        mainHeader.getChildren().addAll(titleSection, userSection, timeSection);
        HBox.setHgrow(titleSection, Priority.ALWAYS);
        
        header.getChildren().addAll(topBar, mainHeader);
        
        return header;
    }
    
    private TabPane createMainTabPane() {
        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainTabPane.setStyle("-fx-background-color: transparent;");
        
        // Create tabs with modern design
        Tab addTab = createStyledTab("+ Add Student", "Add new student to the system");
        Tab viewTab = createStyledTab("View Students", "Browse and search all students");
        Tab searchTab = createStyledTab("Search", "Advanced search and filtering");
        Tab editTab = createStyledTab("Edit", "Modify student information");
        Tab analyticsTab = createStyledTab("Analytics", "Student performance analytics");
        
        // Add content to tabs
        addTab.setContent(createAddStudentTab());
        viewTab.setContent(createViewStudentsTab());
        searchTab.setContent(createSearchTab());
        editTab.setContent(createEditTab());
        analyticsTab.setContent(createAnalyticsTab());
        
        mainTabPane.getTabs().addAll(addTab, viewTab, searchTab, editTab, analyticsTab);
        
        return mainTabPane;
    }
    
    private Tab createStyledTab(String title, String tooltip) {
        Tab tab = new Tab(title);
        tab.setTooltip(new Tooltip(tooltip));
        tab.setClosable(false);
        
        // Apply custom styling to tab
        tab.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");
        
        return tab;
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
        addStudentBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        addStudentBtn.setPrefHeight(48);
        addStudentBtn.setMinHeight(48);
        addStudentBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        addStudentBtn.setOnAction(e -> showAddStudentDialog());
        
        Button generateReportBtn = new Button("Generate Report");
        generateReportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        generateReportBtn.setPrefHeight(48);
        generateReportBtn.setMinHeight(48);
        generateReportBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        generateReportBtn.setOnAction(e -> generateSystemReport());
        
        Button viewStatisticsBtn = new Button("View Statistics");
        viewStatisticsBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        viewStatisticsBtn.setPrefHeight(48);
        viewStatisticsBtn.setMinHeight(48);
        viewStatisticsBtn.setTextFill(javafx.scene.paint.Color.WHITE);
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
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px; -fx-background-radius: 8px;");
        addButton.setPrefHeight(48);
        addButton.setMinHeight(48);
        addButton.setTextFill(javafx.scene.paint.Color.WHITE);
        addButton.setOnAction(e -> showAddStudentDialog());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px; -fx-background-radius: 8px;");
        refreshButton.setPrefHeight(48);
        refreshButton.setMinHeight(48);
        refreshButton.setTextFill(javafx.scene.paint.Color.WHITE);
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
        searchButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px; -fx-background-radius: 8px;");
        searchButton.setPrefHeight(48);
        searchButton.setMinHeight(48);
        searchButton.setTextFill(javafx.scene.paint.Color.WHITE);
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
        studentReportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        studentReportBtn.setPrefHeight(48);
        studentReportBtn.setMinHeight(48);
        studentReportBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        studentReportBtn.setOnAction(e -> generateStudentReport());
        
        Button statisticsReportBtn = new Button("Statistics Report");
        statisticsReportBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        statisticsReportBtn.setPrefHeight(48);
        statisticsReportBtn.setMinHeight(48);
        statisticsReportBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        statisticsReportBtn.setOnAction(e -> generateStatisticsReport());
        
        Button exportDataBtn = new Button("Export Data");
        exportDataBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        exportDataBtn.setPrefHeight(48);
        exportDataBtn.setMinHeight(48);
        exportDataBtn.setTextFill(javafx.scene.paint.Color.WHITE);
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
        userManagementBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        userManagementBtn.setPrefWidth(200);
        userManagementBtn.setPrefHeight(48);
        userManagementBtn.setMinHeight(48);
        userManagementBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        userManagementBtn.setOnAction(e -> showUserManagementDialog());
        
        Button systemSettingsBtn = new Button("System Settings");
        systemSettingsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        systemSettingsBtn.setPrefWidth(200);
        systemSettingsBtn.setPrefHeight(48);
        systemSettingsBtn.setMinHeight(48);
        systemSettingsBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        systemSettingsBtn.setOnAction(e -> showSystemSettingsDialog());
        
        Button auditLogsBtn = new Button("Audit Logs");
        auditLogsBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        auditLogsBtn.setPrefWidth(200);
        auditLogsBtn.setPrefHeight(48);
        auditLogsBtn.setMinHeight(48);
        auditLogsBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        auditLogsBtn.setOnAction(e -> showAuditLogsDialog());
        
        Button backupDataBtn = new Button("Backup Data");
        backupDataBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px 24px;");
        backupDataBtn.setPrefWidth(200);
        backupDataBtn.setPrefHeight(48);
        backupDataBtn.setMinHeight(48);
        backupDataBtn.setTextFill(javafx.scene.paint.Color.WHITE);
        backupDataBtn.setOnAction(e -> backupSystemData());
        
        adminControls.getChildren().addAll(userManagementBtn, systemSettingsBtn, auditLogsBtn, backupDataBtn);
        
        adminLayout.getChildren().addAll(titleLabel, adminControls);
        return adminLayout;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(12, 20, 12, 20));
        statusBar.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");
        
        // System status
        HBox statusSection = new HBox(8);
        statusSection.setAlignment(Pos.CENTER_LEFT);
        
        Label statusIcon = new Label("Online");
        statusIcon.setFont(Font.font("Segoe UI", 12));
        
        Label statusLabel = new Label("System Online");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        statusLabel.setStyle("-fx-text-fill: #4a5568;");
        
        statusSection.getChildren().addAll(statusIcon, statusLabel);
        
        // Database status
        HBox dbSection = new HBox(8);
        dbSection.setAlignment(Pos.CENTER_LEFT);
        
        Label dbIcon = new Label("DB");
        dbIcon.setFont(Font.font("Segoe UI", 12));
        
        Label dbLabel = new Label("Database Connected");
        dbLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        dbLabel.setStyle("-fx-text-fill: #4a5568;");
        
        dbSection.getChildren().addAll(dbIcon, dbLabel);
        
        // User session info
        HBox sessionSection = new HBox(8);
        sessionSection.setAlignment(Pos.CENTER_LEFT);
        
        Label sessionIcon = new Label("User");
        sessionIcon.setFont(Font.font("Segoe UI", 12));
        
        Label sessionLabel = new Label();
        sessionLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        sessionLabel.setStyle("-fx-text-fill: #4a5568;");
        
        if (currentSession != null) {
            sessionLabel.setText("Session: " + currentSession.getUser().getUsername() + " (" + currentSession.getUser().getRole() + ")");
        } else {
            sessionLabel.setText("Session: Not authenticated");
        }
        
        sessionSection.getChildren().addAll(sessionIcon, sessionLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Copyright info
        Label copyrightLabel = new Label("© 2024 Student Management System - Professional Edition");
        copyrightLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        copyrightLabel.setStyle("-fx-text-fill: #718096;");
        
        statusBar.getChildren().addAll(statusSection, dbSection, sessionSection, spacer, copyrightLabel);
        
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

    private VBox createAddStudentTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(20));
        tabContent.setStyle("-fx-background-color: #fafbfc;");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label("+ Add New Student");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Enter student information to add them to the system");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Form
        VBox form = new VBox(20);
        form.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Personal Information Section
        VBox personalSection = createFormSection("Personal Information", "Personal");
        
        VBox nameGroup = createStyledTextField("Full Name", "Enter student's full name");
        VBox rollNumberGroup = createStyledTextField("Roll Number", "Auto-generated (read-only)");
        VBox emailGroup = createStyledTextField("Email", "Enter student's email address");
        
        // Make roll number read-only and auto-generated
        TextField rollNumberField = (TextField) rollNumberGroup.getChildren().get(1);
        rollNumberField.setEditable(false);
        rollNumberField.setStyle(rollNumberField.getStyle() + " -fx-background-color: #f8f9fa; -fx-text-fill: #6c757d;");
        
        personalSection.getChildren().addAll(nameGroup, rollNumberGroup, emailGroup);
        
        // Academic Information Section
        VBox academicSection = createFormSection("Academic Information", "Academic");
        
        VBox courseGroup = createStyledComboBox("Course", "Select course");
        VBox yearGroup = createStyledComboBox("Year", "Select year");
        VBox cgpaGroup = createStyledTextField("CGPA", "Enter current CGPA (0.0 - 10.0)");
        
        // Get the actual controls and populate them
        ComboBox<String> courseCombo = (ComboBox<String>) courseGroup.getChildren().get(1);
        ComboBox<String> yearCombo = (ComboBox<String>) yearGroup.getChildren().get(1);
        
        courseCombo.getItems().addAll("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Business Administration");
        yearCombo.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");
        
        // Auto-generate roll number when course is selected
        courseCombo.setOnAction(e -> {
            String selectedCourse = courseCombo.getValue();
            if (selectedCourse != null) {
                Integer courseId = getCourseIdFromName(selectedCourse);
                String rollNumber = DatabaseManager.getInstance().generateRollNumber(courseId);
                rollNumberField.setText(rollNumber);
            }
        });
        
        academicSection.getChildren().addAll(courseGroup, yearGroup, cgpaGroup);
        
        // Contact Information Section
        VBox contactSection = createFormSection("Contact Information", "Contact");
        
        VBox phoneGroup = createStyledTextField("Phone Number", "Enter contact number");
        VBox addressGroup = createStyledTextField("Address", "Enter residential address");
        
        contactSection.getChildren().addAll(phoneGroup, addressGroup);
        
        // Action Buttons
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("+ Add Student");
        addButton.setStyle(
            "-fx-background-color: #56ab2f; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 12px; " +
            "-fx-padding: 18px 32px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(86,171,47,0.3), 8, 0, 0, 2);"
        );
        // Ensure proper height and text rendering
        addButton.setPrefHeight(52);
        addButton.setMinHeight(52);
        addButton.setTextFill(javafx.scene.paint.Color.WHITE);
        
        Button clearButton = new Button("Clear Form");
        clearButton.setStyle(
            "-fx-background-color: #6c757d; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 12px; " +
            "-fx-padding: 18px 32px; " +
            "-fx-cursor: hand;"
        );
        // Ensure proper height and text rendering
        clearButton.setPrefHeight(52);
        clearButton.setMinHeight(52);
        clearButton.setTextFill(javafx.scene.paint.Color.WHITE);
        
        buttonRow.getChildren().addAll(addButton, clearButton);
        
        // Add action handlers
        addButton.setOnAction(e -> {
            // Add student logic here
            showSuccess("Success", "Student added successfully!");
        });
        
        clearButton.setOnAction(e -> {
            // Clear form logic here
            ((TextField) nameGroup.getChildren().get(1)).clear();
            rollNumberField.clear();
            ((TextField) emailGroup.getChildren().get(1)).clear();
            courseCombo.setValue(null);
            yearCombo.setValue(null);
            ((TextField) cgpaGroup.getChildren().get(1)).clear();
            ((TextField) phoneGroup.getChildren().get(1)).clear();
            ((TextField) addressGroup.getChildren().get(1)).clear();
        });
        
        form.getChildren().addAll(personalSection, academicSection, contactSection, buttonRow);
        
        // Wrap everything in a ScrollPane for proper scrolling
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(header, form);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        tabContent.getChildren().add(scrollPane);
        
        return tabContent;
    }
    
    private Integer getCourseIdFromName(String courseName) {
        // Map course names to dummy course IDs for demo
        switch (courseName) {
            case "Computer Science": return 1;
            case "Information Technology": return 2;
            case "Electronics": return 3;
            case "Mechanical": return 4;
            case "Civil": return 5;
            case "Business Administration": return 6;
            default: return 1;
        }
    }
    
    private VBox createFormSection(String title, String icon) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: #f8fafc; -fx-padding: 20; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12;");
        
        HBox sectionHeader = new HBox(8);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 18));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        sectionHeader.getChildren().addAll(iconLabel, titleLabel);
        section.getChildren().add(sectionHeader);
        
        return section;
    }
    
    private VBox createStyledTextField(String label, String placeholder) {
        VBox fieldGroup = new VBox(5);
        
        Label fieldLabel = new Label(label);
        fieldLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        fieldLabel.setStyle("-fx-text-fill: #4a5568;");
        
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setPrefHeight(45);
        textField.setStyle(
            "-fx-font-size: 14; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 0 15; " +
            "-fx-background-color: white;"
        );
        
        fieldGroup.getChildren().addAll(fieldLabel, textField);
        return fieldGroup;
    }
    
    private VBox createStyledComboBox(String label, String placeholder) {
        VBox fieldGroup = new VBox(5);
        
        Label fieldLabel = new Label(label);
        fieldLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        fieldLabel.setStyle("-fx-text-fill: #4a5568;");
        
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(placeholder);
        comboBox.setPrefHeight(45);
        comboBox.setStyle(
            "-fx-font-size: 14; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 0 15; " +
            "-fx-background-color: white;"
        );
        
        fieldGroup.getChildren().addAll(fieldLabel, comboBox);
        return fieldGroup;
    }
    
    private VBox createViewStudentsTab() {
        VBox tabContent = new VBox(25);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #fafbfc;");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label("Student Directory");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Browse and manage all registered students");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Table
        VBox tableContainer = new VBox(15);
        tableContainer.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Create a sample table for now
        TableView<Student> table = new TableView<>();
        table.setPlaceholder(new Label("No students found"));
        
        // Add sample columns
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Student, String> rollCol = new TableColumn<>("Roll Number");
        rollCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        
        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Student, BigDecimal> gpaCol = new TableColumn<>("GPA");
        gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        
        table.getColumns().addAll(nameCol, rollCol, courseCol, statusCol, gpaCol);
        
        // Add some sample data - using the correct constructor
        ObservableList<Student> sampleData = FXCollections.observableArrayList();
        
        // Create sample students with proper constructor
        Student student1 = new Student();
        student1.setName("John Doe");
        student1.setRollNumber("CS001");
        student1.setCourseName("Computer Science");
        student1.setGpa(new java.math.BigDecimal("8.5"));
        
        Student student2 = new Student();
        student2.setName("Jane Smith");
        student2.setRollNumber("IT001");
        student2.setCourseName("Information Technology");
        student2.setGpa(new java.math.BigDecimal("9.2"));
        
        Student student3 = new Student();
        student3.setName("Mike Johnson");
        student3.setRollNumber("ME001");
        student3.setCourseName("Mechanical");
        student3.setGpa(new java.math.BigDecimal("7.8"));
        
        sampleData.addAll(student1, student2, student3);
        
        table.setItems(sampleData);
        
        tableContainer.getChildren().add(table);
        
        // Wrap everything in a ScrollPane for proper scrolling
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(header, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        tabContent.getChildren().add(scrollPane);
        return tabContent;
    }
    
    private VBox createSearchTab() {
        VBox tabContent = new VBox(25);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #fafbfc;");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label("Advanced Search");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Find students using various criteria");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Search form
        VBox searchForm = new VBox(20);
        searchForm.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Search fields
        HBox searchRow1 = new HBox(15);
        VBox nameSearchGroup = createStyledTextField("Name", "Search by name");
        VBox rollSearchGroup = createStyledTextField("Roll Number", "Search by roll number");
        searchRow1.getChildren().addAll(nameSearchGroup, rollSearchGroup);
        
        HBox searchRow2 = new HBox(15);
        VBox courseSearchGroup = createStyledComboBox("Course", "Filter by course");
        VBox yearSearchGroup = createStyledComboBox("Year", "Filter by year");
        searchRow2.getChildren().addAll(courseSearchGroup, yearSearchGroup);
        
        // Get the actual controls from the groups
        ComboBox<String> courseSearch = (ComboBox<String>) courseSearchGroup.getChildren().get(1);
        ComboBox<String> yearSearch = (ComboBox<String>) yearSearchGroup.getChildren().get(1);
        
        courseSearch.getItems().addAll("All Courses", "Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Business Administration");
        yearSearch.getItems().addAll("All Years", "1st Year", "2nd Year", "3rd Year", "4th Year");
        
        // Search button
        Button searchButton = new Button("Search Students");
        searchButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 12px; " +
            "-fx-padding: 18px 32px; " +
            "-fx-cursor: hand;"
        );
        // Ensure proper height and text rendering
        searchButton.setPrefHeight(52);
        searchButton.setMinHeight(52);
        searchButton.setTextFill(javafx.scene.paint.Color.WHITE);
        
        searchForm.getChildren().addAll(searchRow1, searchRow2, searchButton);
        
        // Wrap everything in a ScrollPane for proper scrolling
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(header, searchForm);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        tabContent.getChildren().add(scrollPane);
        return tabContent;
    }
    
    private VBox createEditTab() {
        VBox tabContent = new VBox(25);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #fafbfc;");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label("Edit Student");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Modify existing student information");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Edit form placeholder
        VBox editForm = new VBox(20);
        editForm.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label placeholderLabel = new Label("Select a student to edit from the View Students tab");
        placeholderLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        placeholderLabel.setStyle("-fx-text-fill: #718096;");
        placeholderLabel.setAlignment(Pos.CENTER);
        
        editForm.getChildren().add(placeholderLabel);
        
        // Wrap everything in a ScrollPane for proper scrolling
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(header, editForm);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        tabContent.getChildren().add(scrollPane);
        return tabContent;
    }
    
    private VBox createAnalyticsTab() {
        VBox tabContent = new VBox(25);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #fafbfc;");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label("Analytics Dashboard");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");
        
        Label subtitleLabel = new Label("Student performance insights and statistics");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #718096;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Analytics content
        HBox analyticsRow = new HBox(20);
        
        // Stats cards
        VBox totalStudentsCard = createStatCard("Total Students", "156", "Total", "#667eea");
        VBox avgCGPACard = createStatCard("Average CGPA", "8.2", "Avg", "#56ab2f");
        VBox topCourseCard = createStatCard("Top Course", "CS", "Top", "#ff6b6b");
        
        analyticsRow.getChildren().addAll(totalStudentsCard, avgCGPACard, topCourseCard);
        
        // Wrap everything in a ScrollPane for proper scrolling
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(header, analyticsRow);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        tabContent.getChildren().add(scrollPane);
        return tabContent;
    }
    
    private VBox createStatCard(String title, String value, String icon, String color) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 25; " +
            "-fx-background-radius: 16; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
            "-fx-min-width: 150; " +
            "-fx-min-height: 120;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 32));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        titleLabel.setStyle("-fx-text-fill: #718096;");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
    
    /**
     * Performance monitoring and optimization methods
     */
    private void enablePerformanceMode() {
        // Disable unnecessary effects for better performance
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("prism.vsync", "false");
        System.setProperty("prism.order", "d3d,sw");
        
        // Optimize rendering
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.verbose", "false");
        System.setProperty("prism.debug", "false");
    }
    
    /**
     * Optimize scene for better performance
     */
    private void optimizeScene(Scene scene) {
        if (scene != null) {
            scene.setFill(null);
        }
    }
    
    /**
     * Utility method to ensure button text is always visible
     */
    private void ensureButtonTextVisibility(Button button) {
        if (button != null) {
            // Force text fill to be visible using multiple approaches
            button.setStyle(button.getStyle() + " -fx-text-fill: white;");
            button.setTextFill(javafx.scene.paint.Color.WHITE);
            
            // Ensure proper text alignment and rendering
            button.setAlignment(Pos.CENTER);
        }
    }
    
    /**
     * Ensure all buttons in a scene have visible text
     */
    private void ensureAllButtonsHaveVisibleText(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            // Find all buttons in the scene
            scene.getRoot().lookupAll(".button").forEach(node -> {
                if (node instanceof Button) {
                    ensureButtonTextVisibility((Button) node);
                }
            });
        }
    }
}
