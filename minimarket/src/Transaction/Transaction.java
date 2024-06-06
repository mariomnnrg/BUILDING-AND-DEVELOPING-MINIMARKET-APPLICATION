/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Transaction;

/**
 *
 * @author Cristian Tambunan
 */
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private double totalHarga;
    private Timestamp timestamp;
    private int userId;
    private String username;

    public Transaction(int id, double totalHarga, Timestamp timestamp, int userId,String username) {
        this.id = id;
        this.totalHarga = totalHarga;
        this.timestamp = timestamp;
        this.userId = userId;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getUserId() {
        return userId;
    }
     public String getUsername() {
        return username;
    }
}