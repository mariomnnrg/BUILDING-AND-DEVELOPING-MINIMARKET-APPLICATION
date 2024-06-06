package login;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.AdminView.adminController;
import login.UserView.userController;

public class FXMLDocumentController implements Initializable {
    
    public class DatabaseConnection {
     public static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/minimarket";
        String user = "root";
        String password = "";

        Connection connection = DriverManager.getConnection(url, user, password);

        if (connection != null) {
            System.out.println("Connected to the database!");
        } else {
            System.err.println("Failed to connect to the database.");
        }

        return connection;
    }
}
    @FXML
    private Label label;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    private User loggedInUser; 
    
    

    @FXML
private void handleLoginAction() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Perform authentication against the database
    String userType = authenticateUser(username, password);

    if (userType != null) {

        // Fetch user data including the name
        User loggedInUser = fetchUserFromDatabase(username);
                
              loggedInUser = fetchUserFromDatabase(username);
              
        // Check if the user is not null before navigating
        if (loggedInUser != null) {
            // Navigate to the appropriate view based on user type
            if ("pelanggan".equals(userType)) {
                navigateToUserView();
            } else if ("kasir".equals(userType)) {
                // Pass the loggedInUser to the adminController
                navigateToAdminView(loggedInUser);
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    } else {
        showAlert("Login Failed", "Invalid username or password.");
    }
}
@FXML
private void handleRegisterAction() {
    try {
        // Load the registration view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/register/FXML.fxml"));
        Parent view = loader.load();

        // Create a new stage for the registration view
        Stage registerStage = new Stage();
        registerStage.setScene(new Scene(view));
        registerStage.show();

        // Close the login window
        ((Stage) usernameField.getScene().getWindow()).close();

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error Loading Registration View", "An error occurred while loading the registration view.");
    }
}


 private User fetchUserFromDatabase(String username) {
    // Initialize the User object
    User user = null;

    // JDBC variables for database connection
    String url = "jdbc:mysql://localhost:3306/minimarket";
    String dbUsername = "root";
    String dbPassword = "";

    try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
        // Query to fetch user data based on username
        String query = "SELECT * FROM akun WHERE akun = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Create a User object with retrieved data
                user = new User(resultSet.getString("akun"), resultSet.getString("nama"));

                // Set other fields of the User object if needed
                // user.setPassword(resultSet.getString("password"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the SQLException (show an alert, log the error, etc.)
    }

    return user;
}


private void  navigateToUserView() {
    try {
        System.out.println("Loading user.fxml...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserView/FXMLDocument.fxml"));
        Parent view = loader.load();
        System.out.println("user.fxml loaded successfully");

        Scene scene = new Scene(view);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        
           userController userController = loader.getController();

        // Pass the loggedInUser to the userController
        userController.setUser(loggedInUser);
        // Close the login window if needed
        ((Stage) usernameField.getScene().getWindow()).close();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error Loading User View", "An error occurred while loading the user view.");
    }
}
private void navigateToAdminView(User loggedInUser) {
    try {
        System.out.println("Loading admin.fxml...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminView/user.fxml"));
        Parent view = loader.load();
        System.out.println("admin.fxml loaded successfully");

        // Access the controller for the AdminView
        adminController adminController = loader.getController();
        
        // Pass the loggedInUser to the adminController
        adminController.setUser(loggedInUser);

        Scene scene = new Scene(view);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // Close the login window if needed
        ((Stage) usernameField.getScene().getWindow()).close();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error Loading Admin View", "An error occurred while loading the admin view.");
    }
}

 private String authenticateUser(String username, String password) {
    try (Connection connection = DatabaseConnection.connect()) {
        String query = "SELECT tipe_akun, nama,id FROM akun WHERE akun = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
             
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Set the username and name to the loggedInUser
                    loggedInUser = new User(username, resultSet.getString("nama"));
                    return resultSet.getString("tipe_akun");
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null; // Return null if authentication fails
}


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
