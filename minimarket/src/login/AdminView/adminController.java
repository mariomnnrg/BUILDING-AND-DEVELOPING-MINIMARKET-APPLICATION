        package login.AdminView;

        import java.io.ByteArrayInputStream;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.stage.FileChooser;
        import javafx.stage.Stage;

        import java.io.File;
        import java.io.IOException;
        import java.nio.file.Files;
        import java.sql.*;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Optional;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.Alert.AlertType;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
        import login.User;

        public class adminController implements BarangService {

             @FXML
            private Label profileLabel;

              @FXML
            private TextField kodeField;
              
            @FXML
            private TextField namaField;

            @FXML
            private TextField hargaField;

            @FXML
            private TextField qtyField;

            @FXML
            private TableView<Barang> tableView;

            @FXML
            private Button logoutButton;


            @FXML
            private TableColumn<Barang, Image> gambarColumn;



            @FXML
            private TableColumn<Barang, Integer> idColumn;

             @FXML
            private TableColumn<Barang, String> kodeColumn;
             
            @FXML
            private TableColumn<Barang, String> namaColumn;

            @FXML
            private TableColumn<Barang, Double> hargaColumn;

            @FXML
            private TableColumn<Barang, Integer> qtyColumn;

              @FXML
            private ImageView itemImage; // Add this line

            @FXML
            private ImageView previewImage; // Add this line

            private ObservableList<Barang> data = FXCollections.observableArrayList();

            private Connection connection;
            private Statement statement;

            private Stage stage;

            @FXML
    public void initialize() {
        initializeDatabase();
        setupTable();
        refreshTable();

        // Menambahkan event listener untuk menanggapi pemilihan item di tabel
        tableView.setOnMouseClicked(event -> {
            Barang selectedBarang = tableView.getSelectionModel().getSelectedItem();
            if (selectedBarang != null) {
                // Set nilai field sesuai dengan data yang dipilih dari tabel
                 kodeField.setText(selectedBarang.getKode_barang());
                namaField.setText(selectedBarang.getNama());
                hargaField.setText(String.valueOf(selectedBarang.getHarga()));
                qtyField.setText(String.valueOf(selectedBarang.getQty()));
                // Tampilkan gambar di previewImage
                previewImage.setImage(selectedBarang.getGambar());
            }
        });
    }

           @FXML
public void handleSelectImage() {
    byte[] imageData = selectImage();
    if (imageData != null) {
        selectedImageData = imageData;
        
        // Tampilkan gambar di previewImage
        previewImage.setImage(new Image(new ByteArrayInputStream(imageData)));
    }
}



            public void setStage(Stage stage) {
                this.stage = stage;
            }


            private void initializeDatabase() {
                try {
                    String url = "jdbc:mysql://localhost:3306/minimarket";
                    String username = "root";
                    String password = "";

                    connection = DriverManager.getConnection(url, username, password);
                    statement = connection.createStatement();

                   
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            }

         
            private User user;

            private void setupTable() {
                idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
                kodeColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("kode_barang"));
                namaColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nama"));
                hargaColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("harga"));
                qtyColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("qty"));
                gambarColumn.setCellValueFactory(new PropertyValueFactory<>("gambar"));



            gambarColumn.setCellFactory(param -> new TableCell<>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(item);
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        setGraphic(imageView);
                    }
                }
            });
            tableView.setItems(data);
            }

          public void setUser(User user) {
        this.user = user;

        // Set the profile label text
        if (user != null) {
            profileLabel.setText(user.getName());
        }
    }

@FXML
public void tambahBarang() {
    String kode_barang = kodeField.getText();
    String nama = namaField.getText();
    String hargaText = hargaField.getText();
    String qtyText = qtyField.getText();

    // Check if any of the required fields is empty
    if (kode_barang.isEmpty() || nama.isEmpty() || hargaText.isEmpty() || qtyText.isEmpty()) {
        showErrorDialog("Please fill in all required fields.");
        return;
    }

    double harga;
    int qty;

    try {
        harga = Double.parseDouble(hargaText);
        qty = Integer.parseInt(qtyText);
    } catch (NumberFormatException e) {
        showErrorDialog("Invalid input for harga or qty. Please enter valid numbers.");
        return;
    }

    // Check if the nama barang is unique
    if (!isNamaBarangUnique(nama)) {
        // Display an error message and return
        showErrorDialog("Nama barang sudah ada. Harap masukkan nama yang berbeda.");
        return;
    }

    // Continue with adding the barang to the database
    tambahBarang(kode_barang, nama, harga, qty, selectedImageData);
    refreshTable();
    clearFormFields();
}


        
@FXML
public void updateBarang() {
    Barang selectedBarang = tableView.getSelectionModel().getSelectedItem();
    if (selectedBarang != null) {
        int id = selectedBarang.getId();
        String kode_barang = kodeField.getText();
        String nama = namaField.getText();
        double harga = Double.parseDouble(hargaField.getText());
        int qty = Integer.parseInt(qtyField.getText());
        byte[] imageData = selectedImageData; // Gunakan data gambar yang baru

        // Check if the nama barang is unique, excluding the current item
        if (!isNamaBarangUnique(nama, id)) {
            // Display an error message and return
            showErrorDialog("Nama barang sudah ada. Harap masukkan nama yang berbeda.");
            return;
        }

        // Continue with updating the barang in the database
        updateBarang(id, kode_barang, nama, harga, qty, imageData);

        // Perbarui koleksi data setelah operasi pembaruan
        refreshTable();

        clearFormFields();
    } else {
        System.out.println("No item selected.");
    }
}


           @FXML
    public void hapusBarang() {
        Barang selectedBarang = tableView.getSelectionModel().getSelectedItem();
        if (selectedBarang != null) {
            int id = selectedBarang.getId();
            hapusBarang(id);
            refreshTable();
        } else {
            // Handle the case where no item is selected
            // You can show an alert or perform any other action
        }
    }


            @Override
            public byte[] selectImage() {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Image File");

                // Set an initial directory or file extension filters if needed
                // fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        return Files.readAllBytes(file.toPath());
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
                return null;
            }
@FXML
private void logout() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
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


       @Override
public void tambahBarang(String kode_barang, String nama, double harga, int qty, byte[] imageData) {
    try {
        String insertQuery = "INSERT INTO barang (kode_barang, nama, harga, qty, image) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, kode_barang);
            preparedStatement.setString(2, nama);
            preparedStatement.setDouble(3, harga);
            preparedStatement.setInt(4, qty);

            if (imageData != null) {
                preparedStatement.setBytes(5, imageData);
            } else {
                preparedStatement.setNull(5, Types.BLOB); // Set to NULL if imageData is null
            }

            preparedStatement.executeUpdate();
        }
    } catch (SQLException e) {
        handleSQLException(e);
    }
}

@Override
public void updateBarang(int id, String kode_barang, String nama, double harga, int qty, byte[] imageData) {
    try {
        String updateQuery = "UPDATE barang SET kode_barang=?, nama=?, harga=?, qty=?, image=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, kode_barang);
            preparedStatement.setString(2, nama);
            preparedStatement.setDouble(3, harga);
            preparedStatement.setInt(4, qty);

            if (imageData != null) {
                preparedStatement.setBytes(5, imageData);
            } else {
                preparedStatement.setNull(5, Types.BLOB); // Set to NULL if imageData is null
            }

            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();
        }
    } catch (SQLException e) {
        handleSQLException(e);
    }
}

            @Override
        public void hapusBarang(int id) {
            try {
                String deleteQuery = "DELETE FROM barang WHERE id=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                handleSQLException(e);
            }
        }

private void refreshTable() {
    data.clear();

    try {
        String selectQuery = "SELECT * FROM barang";
        ResultSet resultSet = statement.executeQuery(selectQuery);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String kode_barang = resultSet.getString("kode_barang");
            String nama = resultSet.getString("nama");
            double harga = resultSet.getDouble("harga");
            int qty = resultSet.getInt("qty");
            byte[] imageData = resultSet.getBytes("image");
            data.add(new Barang(id,kode_barang, nama, harga, qty, imageData));
        }
    } catch (SQLException e) {
        handleSQLException(e);
    }

    // Pembaruan tampilan TableView
    tableView.refresh();

    // Pernyataan log untuk memeriksa apakah refreshTable dijalankan
    System.out.println("Table Refreshed");
}


            private void clearFormFields() {
                namaField.clear();
                hargaField.clear();
                qtyField.clear();
            }
            private byte[] selectedImageData;

            @Override
            public List<Barang> ambilSemuaBarang() {
                List<Barang> result = new ArrayList<>();

                try {
                    String selectQuery = "SELECT * FROM barang";
                    ResultSet resultSet = statement.executeQuery(selectQuery);

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String nama = resultSet.getString("nama");
                        String kode_barang = resultSet.getString("kode_barang");
                        double harga = resultSet.getDouble("harga");
                        int qty = resultSet.getInt("qty");
                        byte[] imageData = resultSet.getBytes("image");

                        result.add(new Barang(id,kode_barang, nama, harga, qty, imageData));
                    }
                } catch (SQLException e) {
                    handleSQLException(e);
                }

                return result;
            }

            private void handleSQLException(SQLException e) {
                // Handle the SQLException gracefully (e.g., show an error message)
                e.printStackTrace();
            }

            private void handleIOException(IOException e) {
                // Handle the IOException gracefully (e.g., show an error message)
                e.printStackTrace();
            }
            private boolean isNamaBarangUnique(String nama) {
    // Query database to check if nama barang is unique
    try {
        String selectQuery = "SELECT COUNT(*) FROM barang WHERE nama=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, nama);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if count is 0 (nama barang is unique)
            }
        }
    } catch (SQLException e) {
        handleSQLException(e);
    }
    return false; // Default to false in case of an error
}

// Metode untuk memeriksa apakah nama barang sudah ada di database, kecuali item dengan ID tertentu
private boolean isNamaBarangUnique(String nama, int excludeId) {
    // Query database to check if nama barang is unique, excluding the current item
    try {
        String selectQuery = "SELECT COUNT(*) FROM barang WHERE nama=? AND id<>?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, nama);
            preparedStatement.setInt(2, excludeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if count is 0 (nama barang is unique)
            }
        }
    } catch (SQLException e) {
        handleSQLException(e);
    }
    return false; // Default to false in case of an error
}
private void showErrorDialog(String errorMessage) {
     Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Warning");
    alert.setContentText(errorMessage);
    alert.showAndWait();
}


            
            public static class Barang {
            private final int id;
            private final String Kode_barang;
            private final String nama;
            private final double harga;
            private final int qty;
            private final byte[] imageData;
            private final Image gambar;

                 public Barang(int id, String Kode_barang,String nama, double harga, int qty, byte[] imageData) {
                this.id = id;
                this.Kode_barang = Kode_barang;
                this.nama = nama;
                this.harga = harga;
                this.qty = qty;
                this.imageData = imageData;
                this.gambar = convertToImage(imageData);
            }

              public Image getGambar() {
            return gambar;
            }
              public byte[] getImageData(){
                return imageData;
            }
                public double getHarga() {
            return harga;
        }
                   public int getQty() {
                return qty;
            }
                public String getNama() {
                return nama;
            }
                
 public String getKode_barang() {
        return Kode_barang;
 }
                public int getId() {
                return id;
            }

          private Image convertToImage(byte[] imageData) {
            if (imageData != null) {
                return new Image(new ByteArrayInputStream(imageData));
            } else {
                // Print a message or log that imageData is null
                System.out.println("Image data is null. Unable to create Image object.");
                // Return a default image or null
                return new Image("/path/to/default/image.jpg"); // Provide the correct default image path
            }
        }
            }
        }