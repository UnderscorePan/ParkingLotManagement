package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkingLot.database.databaseConnection;

public class TransactionController {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public boolean saveTransaction(String licensePlate, double amount, String paymentMethod, 
                                   String transactionType, LocalDateTime transactionDate,
                                   double parkingFee, double fineAmount) {
        String sql = "INSERT INTO transactions (plate_number, amount, payment_method, transaction_type, transaction_date, parking_fee, fine_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, transactionType);
            pstmt.setString(5, transactionDate.format(DATE_FORMATTER));
            pstmt.setDouble(6, parkingFee);
            pstmt.setDouble(7, fineAmount);
            
            pstmt.executeUpdate();
            System.out.println("Transaction saved: " + licensePlate + " - RM " + amount + " (" + transactionType + ")");
            System.out.println("   Parking: RM " + parkingFee + " | Fines: RM " + fineAmount);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Deprecated
    public boolean saveTransaction(String licensePlate, double amount, String paymentMethod, 
                                   String transactionType, LocalDateTime transactionDate) {
        return saveTransaction(licensePlate, amount, paymentMethod, transactionType, transactionDate, 0, 0);
    }
    
    public List<Map<String, Object>> getTransactionsByVehicle(String licensePlate) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE plate_number = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transaction_id", rs.getInt("transaction_id"));
                transaction.put("plate_number", rs.getString("plate_number"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("payment_method", rs.getString("payment_method"));
                transaction.put("transaction_type", rs.getString("transaction_type"));
                transaction.put("transaction_date", rs.getString("transaction_date"));
                transaction.put("parking_fee", rs.getDouble("parking_fee"));
                transaction.put("fine_amount", rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Map<String, Object>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate.format(DATE_FORMATTER));
            pstmt.setString(2, endDate.format(DATE_FORMATTER));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transaction_id", rs.getInt("transaction_id"));
                transaction.put("plate_number", rs.getString("plate_number"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("payment_method", rs.getString("payment_method"));
                transaction.put("transaction_type", rs.getString("transaction_type"));
                transaction.put("transaction_date", rs.getString("transaction_date"));
                transaction.put("parking_fee", rs.getDouble("parking_fee"));
                transaction.put("fine_amount", rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transactions by date range: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Map<String, Object>> getAllTransactions() {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transaction_id", rs.getInt("transaction_id"));
                transaction.put("plate_number", rs.getString("plate_number"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("payment_method", rs.getString("payment_method"));
                transaction.put("transaction_type", rs.getString("transaction_type"));
                transaction.put("transaction_date", rs.getString("transaction_date"));
                transaction.put("parking_fee", rs.getDouble("parking_fee"));
                transaction.put("fine_amount", rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public double getTotalRevenue() {
        String sql = "SELECT SUM(amount) as total FROM transactions";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    public Map<String, Double> getRevenueByPaymentMethod() {
        Map<String, Double> revenueMap = new HashMap<>();
        String sql = "SELECT payment_method, SUM(amount) as total FROM transactions GROUP BY payment_method";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                revenueMap.put(rs.getString("payment_method"), rs.getDouble("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting revenue by payment method: " + e.getMessage());
        }
        
        return revenueMap;
    }
    
    public Map<String, Double> getRevenueByTransactionType() {
        Map<String, Double> revenueMap = new HashMap<>();
        String sql = "SELECT transaction_type, SUM(amount) as total FROM transactions GROUP BY transaction_type";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                revenueMap.put(rs.getString("transaction_type"), rs.getDouble("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting revenue by transaction type: " + e.getMessage());
        }
        
        return revenueMap;
    }
}
