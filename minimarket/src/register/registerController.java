package register;


import java.io.IOException;
import java.net.URL;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class registerController implements Initializable {

    @FXML
    private TextField email;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordconfirmField;

    @FXML
    private Button signup;

    @FXML
    private TextField usernameField;

    @FXML
    void handleloginAction(ActionEvent event) {
    try {
        // Load the registration view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/FXMLDocument.fxml"));
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

    @FXML
    void handlesignupAction(ActionEvent event) {
        String username = usernameField.getText();
        String emailText = email.getText();
        String password = passwordField.getText();
        String confirmPassword = passwordconfirmField.getText();

        if (username.isEmpty() || emailText.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
        } else if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
        } else if (!isUsernameUnique(emailText)) {
            showAlert("Error", "Username already exists. Please choose another username.");
        } else {
            // Perform user registration logic here
            if (registerUser(username, emailText, password)) {
                showAlert("Signup Successful", "Login Agian");
                clearFields();
            } else {
                showAlert("Error", "An error occurred during registration.");
            }
        }
    }

    private boolean isUsernameUnique(String emailText) {
        // Check if the provided username already exists in the database
        String query = "SELECT COUNT(*) FROM akun WHERE akun = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minimarket", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, emailText);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0; // If count is 0, the username is unique
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String username, String email, String password) {
        // JDBC variables for database connection
        String url = "jdbc:mysql://localhost:3306/minimarket";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            // Insert user data into the database
            String query = "INSERT INTO akun (nama, password, akun, tipe_akun) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, "pelanggan"); // Assuming it's a regular user

                int rowsAffected = preparedStatement.executeUpdate();

                // If registration is successful, rowsAffected should be greater than 0
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQLException (show an alert, log the error, etc.)
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameField.clear();
        email.clear();
        passwordField.clear();
        passwordconfirmField.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code, if needed
    }
}
