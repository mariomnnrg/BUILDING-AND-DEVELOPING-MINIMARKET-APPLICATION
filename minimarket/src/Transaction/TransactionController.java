package Transaction;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import login.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import login.UserView.UserSession;

public class TransactionController {

    @FXML
    private Label profileLabel;

    @FXML
    private TableView<Transaction> selectedItemsTableView;
    
       @FXML
            private Button logoutButton;
         
         @FXML
            private Button transaksiButton;
         
           @FXML
            private Button belanjaButton;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private User user;

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minimarket", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML
private void transaksi() {
    try {
        // Check if the transaction window is already open
        if (!isTransactionWindowOpen()) {
            System.out.println("Loading user.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaction/TransactionDocument.fxml"));
            Parent view = loader.load();
            System.out.println("FXMLDocument.fxml loaded successfully");

            // Access the controller and set the user
            TransactionController transactionController = loader.getController();
            transactionController.setUser(UserSession.getCurrentUser());

            Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setScene(scene);

            // Set a listener to handle window close event
            stage.setOnCloseRequest(event -> setTransactionWindowOpen(false));

            // Close the current window (user window)
            Stage currentStage = (Stage) transaksiButton.getScene().getWindow();
            currentStage.close();

            // Set the flag indicating that the transaction window is now open
            setTransactionWindowOpen(true);

            stage.showAndWait(); // Wait for the transaction window to close

            // After the transaction window is closed, set the flag to false
            setTransactionWindowOpen(false);
        } else {
            System.out.println("Transaction window is already open.");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
@FXML
private void barang() {
    try {
        // Check if the transaction window is already open
        if (!isTransactionWindowOpen()) {
            System.out.println("Loading user.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/UserView/FXMLDocument.fxml"));
            Parent view = loader.load();
            System.out.println("FXMLDocument.fxml loaded successfully");

            // Access the controller and set the user
            // Use the appropriate controller class for user.fxml
            login.UserView.userController userController = loader.getController();
            userController.setUser(UserSession.getCurrentUser());

          Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setScene(scene);

            // Set a listener to handle window close event
            stage.setOnCloseRequest(event -> setTransactionWindowOpen(false));

            // Close the current window (user window)
            Stage currentStage = (Stage) belanjaButton.getScene().getWindow();
            currentStage.close();

            // Set the flag indicating that the transaction window is now open
            setTransactionWindowOpen(true);

            stage.showAndWait(); // Wait for the transaction window to close

            // After the transaction window is closed, set the flag to false
            setTransactionWindowOpen(false);
        } else {
            System.out.println("Transaction window is already open.");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}



// Add these two methods to your TransactionController class
private static boolean isTransactionWindowOpen = false;

private static void setTransactionWindowOpen(boolean isOpen) {
    isTransactionWindowOpen = isOpen;
}

// Getter for checking if the transaction window is open
public static boolean isTransactionWindowOpen() {
    return isTransactionWindowOpen;
}



@FXML
private void logout() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Konfirmasi Logout");
    alert.setHeaderText("Anda yakin ingin logout?");
    alert.setContentText("Pilih OK untuk logout, atau Cancel untuk batal.");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            System.out.println("Loading user.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/FXMLDocument.fxml"));
            Parent view = loader.load();
            System.out.println("FXMLDocument.fxml loaded successfully");

            Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close the current window (admin window)
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            profileLabel.setText(user.getName());
            fetchDataFromDatabase();
        }
    }

    private void fetchDataFromDatabase() {
        try {
            String userIdQuery = "SELECT id FROM akun WHERE nama = ?";
            preparedStatement = connection.prepareStatement(userIdQuery);
            preparedStatement.setString(1, user.getName());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                resultSet.close();

                String transactionQuery = "SELECT * FROM `transaction` WHERE `user_id` = ?";
                preparedStatement = connection.prepareStatement(transactionQuery);
                preparedStatement.setInt(1, userId);
                resultSet = preparedStatement.executeQuery();

                TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Transaction, Double> totalHargaColumn = new TableColumn<>("Total Harga");
                totalHargaColumn.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));

                TableColumn<Transaction, String> timestampColumn = new TableColumn<>("Timestamp");
                timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

                TableColumn<Transaction, String> usernameColumn = new TableColumn<>("Username");
                usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

                selectedItemsTableView.getColumns().clear();
                selectedItemsTableView.getItems().clear();
                selectedItemsTableView.getColumns().addAll(idColumn, totalHargaColumn, timestampColumn, usernameColumn);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    double totalHarga = resultSet.getDouble("total_harga");
                    String timestampStr = resultSet.getString("timestamp");
                    String transactionUsername = resultSet.getString("username");
                    Timestamp timestamp = Timestamp.valueOf(timestampStr);

                    selectedItemsTableView.getItems().add(new Transaction(id, totalHarga, timestamp, userId, transactionUsername));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
