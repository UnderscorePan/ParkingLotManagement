package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.vehicles.Vehicle;

public class VehicleController {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public boolean saveVehicleEntry(Vehicle vehicle, String spotId) {
        String sql = "INSERT INTO vehicles (plate_number, vehicle_type, entry_time, exit_time, has_handicapped_card) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getType().toString());
            pstmt.setString(3, vehicle.getEntryTime().format(DATE_FORMATTER));
            pstmt.setString(4, null);
            pstmt.setInt(5, 0);
            
            pstmt.executeUpdate();
            System.out.println("Vehicle " + vehicle.getLicensePlate() + " saved to database");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVehicleExit(String licensePlate, LocalDateTime exitTime) {
        String sql = "UPDATE vehicles SET exit_time = ? WHERE plate_number = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exitTime.format(DATE_FORMATTER));
            pstmt.setString(2, licensePlate);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vehicle " + licensePlate + " exit time updated");
                return true;
            } else {
                System.err.println("No vehicle found with plate: " + licensePlate);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating vehicle exit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getVehicle(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE plate_number = ?";
        
        try {
            Connection conn = databaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, licensePlate);
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicle: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getAllActiveVehicles() {
        String sql = "SELECT * FROM vehicles WHERE exit_time IS NULL";
        
        try {
            Connection conn = databaseConnection.connect();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
            
        } catch (SQLException e) {
            System.err.println("Error retrieving active vehicles: " + e.getMessage());
            return null;
        }
    }

    public boolean vehicleExists(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE plate_number = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking vehicle existence: " + e.getMessage());
            return false;
        }
    }
    
    public java.util.List<java.util.Map<String, Object>> getAllVehiclesForDisplay() {
        java.util.List<java.util.Map<String, Object>> vehicles = new java.util.ArrayList<>();
        String sql = "SELECT plate_number, vehicle_type, isVip FROM vehicles ORDER BY plate_number";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.util.Map<String, Object> vehicle = new java.util.HashMap<>();
                vehicle.put("plate_number", rs.getString("plate_number"));
                vehicle.put("vehicle_type", rs.getString("vehicle_type"));
                vehicle.put("isVip", rs.getInt("isVip"));
                vehicles.add(vehicle);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles for display: " + e.getMessage());
        }
        
        return vehicles;
    }
    
    public boolean updateVipStatus(String licensePlate, int isVip) {
        String sql = "UPDATE vehicles SET isVip = ? WHERE plate_number = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, isVip);
            pstmt.setString(2, licensePlate);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                String status = (isVip == 1) ? "VIP" : "Regular";
                System.out.println("Vehicle " + licensePlate + " updated to " + status);
                return true;
            } else {
                System.err.println("No vehicle found with plate: " + licensePlate);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating VIP status: " + e.getMessage());
            return false;
        }
    }
}
