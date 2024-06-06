    package login.UserView;


import Transaction.TransactionController;
    import java.io.IOException;
    import java.io.InputStream;
    import javafx.fxml.FXML;
    import javafx.scene.control.Label;
    import javafx.scene.control.ListView;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.input.MouseEvent;

    import java.sql.*;
import java.util.Optional;
    import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
    import javafx.scene.control.TableCell;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import javafx.scene.control.TextField;
    import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
    import login.AdminView.adminController.Barang;
     import login.User;

    public class userController extends BaseController{
        
             @FXML
            private Label profileLabel;
        @FXML
        private TableView<Barang> availableItemsTableView;
        
         @FXML
            private Button logoutButton;
         
         @FXML
            private Button transaksiButton;
         
         @FXML
        private TableColumn<Barang, Image> gambarColumn;

        @FXML
        private TableColumn<Barang, String> kodeBarangColumn;

        @FXML
        private TableColumn<Barang, String> namaBarangColumn;

        @FXML
        private TableColumn<Barang, Double> hargaBarangColumn;

        @FXML
        private TableColumn<Barang, Integer> qtyBarangColumn;

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
    private TableView<CartItem> selectedItemsTableView;

    @FXML
    private TableColumn<CartItem, String> cartKodeBarangColumn;

    @FXML
    private TableColumn<CartItem, String> cartNamaBarangColumn;

    @FXML
    private TableColumn<CartItem, Integer> cartQtyBarangColumn;

    @FXML
    private TableColumn<CartItem, Double> cartHargaBarangColumn;



        private Connection connection;
        private PreparedStatement preparedStatement;
        private ResultSet resultSet;

    public void initialize() {
        // Initialize database connection
        try {
            System.out.println("Initializing...");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minimarket", "root", "");
            fetchDataFromDatabase();

            // Set cell value factories for each column
            kodeBarangColumn.setCellValueFactory(new PropertyValueFactory<>("Kode_barang"));
            namaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
            hargaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));
            qtyBarangColumn.setCellValueFactory(new PropertyValueFactory<>("qty"));
             gambarColumn.setCellValueFactory(new PropertyValueFactory<>("gambar"));

             cartKodeBarangColumn.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
    cartNamaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
    cartQtyBarangColumn.setCellValueFactory(new PropertyValueFactory<>("qtyBarang"));
    cartHargaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("hargaBarang"));

    // Add the columns to the selectedItemsTableView
    selectedItemsTableView.getColumns().setAll(cartKodeBarangColumn, cartNamaBarangColumn, cartQtyBarangColumn, cartHargaBarangColumn);




            // Set a custom cell factory for the gambarColumn
            gambarColumn.setCellFactory(param -> new TableCell<Barang, Image>() {
                private final ImageView imageView = new ImageView();
                @Override
                protected void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(item);
                        imageView.setFitWidth(50); // Adjust the width as needed
                        imageView.setFitHeight(50); // Adjust the height as needed
                        setGraphic(imageView);
                    }
                }
            });

            // Add the columns to the TableView
            availableItemsTableView.getColumns().setAll(gambarColumn, kodeBarangColumn, namaBarangColumn, hargaBarangColumn, qtyBarangColumn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


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
                  availableItemsTableView.getItems().add(new Barang(id, Kode_barang, nama, harga, qty, imageData));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
          private User user;
          
     @FXML
    private void handleItemSelection(MouseEvent event) {
        Platform.runLater(() -> {
            Barang selectedBarang = availableItemsTableView.getSelectionModel().getSelectedItem();
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

                for (CartItem cartItem : selectedItemsTableView.getItems()) {
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
private void handleAddToCart() {
    Barang selectedItem = availableItemsTableView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
        // Ambil data dari item yang dipilih
        String kodeBarang = selectedItem.getKode_barang();
        String namaBarang = selectedItem.getNama();

        // Ambil nilai dari TextField jumlahBarang
        int qtyBarang = Integer.parseInt(jumlahBarangTextField.getText());

        double hargaBarang = selectedItem.getHarga();
        int availableQty = selectedItem.getQty(); // Get the available quantity

        // Check if the selected qua    ntity is within the available stock
        if (qtyBarang > availableQty) {
            // Display an alert or take appropriate action
            showAlert("Tidak Bisa Membeli Barang Melebihi Stok");
            return;
        }

        // Check if the item is already in the cart
        boolean itemAlreadyInCart = false;
        for (CartItem cartItem : selectedItemsTableView.getItems()) {
            if (cartItem.getKodeBarang().equals(kodeBarang)) {
                itemAlreadyInCart = true;
                // Display a warning that the item is already in the cart
                showAlert("Barang Ini Sudah Ada Di Keranjang");
                break;
            }
        }

        if (!itemAlreadyInCart) {
            // Calculate the total price based on the quantity
            double totalHargaBarang = hargaBarang * qtyBarang;

            // Tambahkan data ke dalam tabel cart
            selectedItemsTableView.getItems().add(new CartItem(kodeBarang, namaBarang, qtyBarang, totalHargaBarang));

            // Update total harga only if the item is not already in the cart
            totalHarga += (hargaBarang * qtyBarang);
            updateTotalHargaLabel();
        }
    }
}

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
    CartItem selectedItem = selectedItemsTableView.getSelectionModel().getSelectedItem();
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

    public class CartItem {
        private final String kodeBarang;
        private final String namaBarang;
          private int qtyBarang;
    private double hargaBarang;


        public CartItem(String kodeBarang, String namaBarang, int qtyBarang, double hargaBarang) {
            this.kodeBarang = kodeBarang;
            this.namaBarang = namaBarang;
            this.qtyBarang = qtyBarang;
            this.hargaBarang = hargaBarang;
        }

        public String getKodeBarang() {
            return kodeBarang;
        }

        public String getNamaBarang() {
            return namaBarang;
        }

        public int getQtyBarang() {
            return qtyBarang;
        }

        public double getHargaBarang() {
            return hargaBarang;
        }
         public void setHargaBarang(double hargaBarang) {
        this.hargaBarang = hargaBarang;
    }
            public void setQtyBarang(int qtyBarang) {
        this.qtyBarang = qtyBarang;
    }

    }

    }




