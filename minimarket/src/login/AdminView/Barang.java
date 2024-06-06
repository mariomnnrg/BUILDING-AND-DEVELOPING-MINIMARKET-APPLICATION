/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login.AdminView;

import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Cristian Tambunan
 */

public class Barang {
    private final int id;
    private final String Kode_barang;
    private final String nama;
    private final double harga;
    private final int qty;
    private final byte[] imageData;
    private final Image gambar;
    
 
    public Barang(int id,String Kode_barang, String nama, double harga, int qty , byte[] imageData) {
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
            // Return a default image or null
            return null;
        }
    }


}
