/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package login.AdminView;
import java.util.List;

/**
 *
 * @author Cristian Tambunan
 */
public interface BarangService {
    List<adminController.Barang> ambilSemuaBarang();
    void tambahBarang(String Kode_barang,String nama, double harga, int qty,byte[] imageData);
   void updateBarang(int id, String Kode_barang,String nama, double harga, int qty,byte[] imageData);
    void hapusBarang(int id);
     byte[] selectImage();
}
