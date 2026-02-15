package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.ParkingSpot;

public class ParkingSpotController {
    
    public boolean saveParkingSpot(ParkingSpot spot, String lotId) {
        String sql = "INSERT INTO parking_spots (spot_id, lot_id, floor_number, row_number, spot_number, spot_type, status, current_vehicle_plate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, spot.getSpotId());
            pstmt.setString(2, lotId);
            pstmt.setInt(3, spot.getFloor());
            pstmt.setString(4, String.valueOf(spot.getRow()));
            pstmt.setInt(5, spot.getSpotNumber());
            pstmt.setString(6, spot.getSpotType().toString());
            pstmt.setInt(7, spot.getStatus().ordinal());
            pstmt.setString(8, null);
            
            pstmt.executeUpdate();
            System.out.println("✅ Parking spot " + spot.getSpotId() + " saved to database");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving parking spot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSpotStatus(String spotId, int status, String vehiclePlate) {
        String sql = "UPDATE parking_spots SET status = ?, current_vehicle_plate = ? WHERE spot_id = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, status);
            pstmt.setString(2, vehiclePlate);
            pstmt.setString(3, spotId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                String statusStr = (status == 0) ? "AVAILABLE" : "OCCUPIED";
                System.out.println("✅ Spot " + spotId + " updated to " + statusStr);
                return true;
            } else {
                System.err.println("⚠️ No spot found with ID: " + spotId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating spot status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public ResultSet getParkingSpot(String spotId) {
        String sql = "SELECT * FROM parking_spots WHERE spot_id = ?";
        
        try {
            Connection conn = databaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spotId);
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving parking spot: " + e.getMessage());
            return null;
        }
    }
    
    public ResultSet getAllSpots() {
        String sql = "SELECT * FROM parking_spots ORDER BY floor_number, row_number, spot_number";
        
        try {
            Connection conn = databaseConnection.connect();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all spots: " + e.getMessage());
            return null;
        }
    }
    
    public boolean deleteParkingSpot(String spotId) {
        String sql = "DELETE FROM parking_spots WHERE spot_id = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, spotId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Parking spot " + spotId + " deleted from database");
                return true;
            } else {
                System.err.println("⚠️ No spot found with ID: " + spotId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting parking spot: " + e.getMessage());
            return false;
        }
    }
    
    public boolean spotExists(String spotId) {
        String sql = "SELECT COUNT(*) FROM parking_spots WHERE spot_id = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, spotId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking spot existence: " + e.getMessage());
            return false;
        }
    }
    
    public java.util.List<java.util.Map<String, Object>> getAllSpotsForDisplay() {
        java.util.List<java.util.Map<String, Object>> spots = new java.util.ArrayList<>();
        String sql = "SELECT spot_id, floor_number, row_number, spot_number, spot_type, status, current_vehicle_plate " +
                     "FROM parking_spots ORDER BY floor_number, row_number, spot_number";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.util.Map<String, Object> spot = new java.util.HashMap<>();
                spot.put("spot_id", rs.getString("spot_id"));
                spot.put("floor_number", rs.getInt("floor_number"));
                spot.put("row_number", rs.getString("row_number"));
                spot.put("spot_number", rs.getInt("spot_number"));
                spot.put("spot_type", rs.getString("spot_type"));
                spot.put("status", rs.getInt("status"));
                spot.put("current_vehicle_plate", rs.getString("current_vehicle_plate"));
                spots.add(spot);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving spots for display: " + e.getMessage());
        }
        
        return spots;
    }
    
    public boolean isSpotOccupied(String spotId) {
        String sql = "SELECT status FROM parking_spots WHERE spot_id = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, spotId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("status") == 1;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking spot status: " + e.getMessage());
        }
        
        return false;
    }
}
