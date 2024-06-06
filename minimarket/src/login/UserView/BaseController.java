/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login.UserView;

import Transaction.TransactionController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import login.AdminView.adminController;
import login.User;

/**
 *
 * @author Cristian Tambunan
 */
public class BaseController {
    
             @FXML
            private Label profileLabel;
        @FXML
        private TableView<adminController.Barang> availableItemsTableView;
        
         @FXML
            private Button logoutButton;
         
         @FXML
            private Button transaksiButton;
         
         @FXML
        private TableColumn<adminController.Barang, Image> gambarColumn;

        @FXML
        private TableColumn<adminController.Barang, String> kodeBarangColumn;

        @FXML
        private TableColumn<adminController.Barang, String> namaBarangColumn;

        @FXML
        private TableColumn<adminController.Barang, Double> hargaBarangColumn;

        @FXML
        private TableColumn<adminController.Barang, Integer> qtyBarangColumn;

        @FXML
    private TextField jumlahBarangTextField;

        @FXML
        private ListView<String> selectedItemsListView;

        @FXML
        private ImageView barangImageView;

        @FXML
        private Label namaBarangLabel;

        @FXML
        private Label hargaBarangLabel;

        @FXML
        private Label qtyBarangLabel;
        
        @FXML
private Label totalHargaLabel;

private double totalHarga = 0.0;

        @FXML
    private TableView<userController.CartItem> selectedItemsTableView;

    @FXML
    private TableColumn<userController.CartItem, String> cartKodeBarangColumn;

    @FXML
    private TableColumn<userController.CartItem, String> cartNamaBarangColumn;

    @FXML
    private TableColumn<userController.CartItem, Integer> cartQtyBarangColumn;

    @FXML
    private TableColumn<userController.CartItem, Double> cartHargaBarangColumn;
    
       private Connection connection;
        private PreparedStatement preparedStatement;
        private ResultSet resultSet;

    
        private void fetchDataFromDatabase() {
            try {
                System.out.println("Fetching data...");
                String query = "SELECT * FROM barang";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                      int id = resultSet.getInt("id");
                    String Kode_barang = resultSet.getString("Kode_barang");
                    String nama = resultSet.getString("nama");
                    double harga = resultSet.getDouble("harga");
                    int qty = resultSet.getInt("qty");
                     byte[] imageData = resultSet.getBytes("image");


                    // Add each row as an Item object to the TableView
                  availableItemsTableView.getItems().add(new adminController.Barang(id, Kode_barang, nama, harga, qty, imageData));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
          private User user;
          
     @FXML
    private void handleItemSelection(MouseEvent event) {
        Platform.runLater(() -> {
            adminController.Barang selectedBarang = availableItemsTableView.getSelectionModel().getSelectedItem();
            if (selectedBarang != null) {
                // Fetch details based on the selected item
                try {
                    String query = "SELECT * FROM barang WHERE nama = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, selectedBarang.getNama()); // Assuming getNama() returns the name
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String nama = resultSet.getString("nama");
                        double harga = resultSet.getDouble("harga");
                        int qty = resultSet.getInt("qty");
                        // Assuming you have a method to load the image from Blob data
                        Image image = loadImageFromBlob(resultSet.getBlob("image"));
                        // Update UI
                        namaBarangLabel.setText(nama);
                        hargaBarangLabel.setText("Harga: Rp " + harga);
                        qtyBarangLabel.setText("Stok: " + qty);
                        barangImageView.setImage(image);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
 @FXML
private void handleCompleteTransaction() {
    // Assuming this method is called when the user completes the transaction

    if (selectedItemsTableView.getItems().isEmpty()) {
        // Display an alert or take appropriate action, indicating that the cart is empty
                    showAlert("Keranjang Kosong Tidak Ada Yang Dibeli.");
        return;
    }

    try {
        // Retrieve the user_id based on the username
        String getUserIdQuery = "SELECT id FROM akun WHERE nama = ?";
        preparedStatement = connection.prepareStatement(getUserIdQuery);
        preparedStatement.setString(1, user.getName());  // Assuming getUsername() returns the username
        resultSet = preparedStatement.executeQuery();

        int userId = -1;  // Initialize with an invalid value
        if (resultSet.next()) {
            userId = resultSet.getInt("id");
        }

        if (userId != -1) {
            // Insert transaction data into the 'transaction' table
            String insertTransactionQuery = "INSERT INTO `transaction` (total_harga, timestamp, user_id,username) VALUES (?, ?, ?,?)";
            preparedStatement = connection.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, totalHarga);
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(3, userId);  // Use the retrieved user_id
             preparedStatement.setString(4, user.getName()); 
            preparedStatement.executeUpdate();

            // Get the generated transaction ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int transactionId;
            if (generatedKeys.next()) {
                transactionId = generatedKeys.getInt(1);

                // Insert cart items into the 'transaction_detail' table
                String insertTransactionDetailQuery = "INSERT INTO `transaction_detail` (transaction_id, kode_barang, nama_barang, qty, harga) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertTransactionDetailQuery);

                for (userController.CartItem cartItem : selectedItemsTableView.getItems()) {
                    preparedStatement.setInt(1, transactionId);
                    preparedStatement.setString(2, cartItem.getKodeBarang());
                    preparedStatement.setString(3, cartItem.getNamaBarang());
                    preparedStatement.setInt(4, cartItem.getQtyBarang());
                    preparedStatement.setDouble(5, cartItem.getHargaBarang());
                    preparedStatement.executeUpdate();
                }

                // Clear the cart after a successful transaction
                selectedItemsTableView.getItems().clear();
                totalHarga = 0.0;
                updateTotalHargaLabel();
            }
        } else {
            // Handle the case where the user_id couldn't be retrieved
            System.out.println("User ID not found for the given username.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception (e.g., display an error message)
    }
}



        // This is a placeholder method, replace it with your actual image loading logic
    private Image loadImageFromBlob(Blob blob) {
        try (InputStream inputStream = blob.getBinaryStream()) {
            if (inputStream != null) {
                return new Image(inputStream);
            } else {
                System.err.println("Input stream is null");
                return null;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }



        // Implement logic to add selected items to the list of selected items
        // For example, when a button is clicked


@FXML
private void transaksi() {
    try {
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
        stage.show();

        // Close the current window (user window)
        Stage currentStage = (Stage) transaksiButton.getScene().getWindow();
        currentStage.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
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


private void updateTotalHargaLabel() {
    totalHargaLabel.setText(String.format("%.2f", totalHarga));
}
      
private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Warning");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
   @FXML
private void handleRemoveFromCart() {
    userController.CartItem selectedItem = selectedItemsTableView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
        // Update total harga before removing the item
        totalHarga -= selectedItem.getHargaBarang();
        updateTotalHargaLabel();

        selectedItemsTableView.getItems().remove(selectedItem);
    }
}
public void setUser(User user) {
    this.user = user;  // Initialize the user variable
    UserSession.setCurrentUser(user);

    // Set the profile label text
    if (user != null) {
        profileLabel.setText(user.getName());
    }
}
}
