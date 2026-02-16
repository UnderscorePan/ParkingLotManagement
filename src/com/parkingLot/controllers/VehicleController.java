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
        String sql = "INSERT OR REPLACE INTO vehicles (plate_number, vehicle_type) VALUES (?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getType().toString());
            
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
        System.out.println("Vehicle " + licensePlate + " exit processed");
        return true;
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
        String sql = "SELECT * FROM vehicles";
        
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
        String sql = "SELECT plate_number, vehicle_type FROM vehicles ORDER BY plate_number";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.util.Map<String, Object> vehicle = new java.util.HashMap<>();
                vehicle.put("plate_number", rs.getString("plate_number"));
                vehicle.put("vehicle_type", rs.getString("vehicle_type"));
                vehicles.add(vehicle);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles for display: " + e.getMessage());
        }
        
        return vehicles;
    }

    public boolean registerVehicle(String licensePlate, String vehicleType) {
        // Use INSERT OR REPLACE to update vehicle type if it already exists
        String sql = "INSERT OR REPLACE INTO vehicles (plate_number, vehicle_type) VALUES (?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            pstmt.setString(2, vehicleType);

            pstmt.executeUpdate();
            System.out.println("Vehicle " + licensePlate + " registered/updated successfully");
            return true;

        } catch (SQLException e) {
            System.err.println("Error registering vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getVehicleType(String licensePlate) {
        String sql = "SELECT vehicle_type FROM vehicles WHERE plate_number = ?";
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("vehicle_type");
            }

        } catch (SQLException e) {
            System.err.println("Error getting vehicle type: " + e.getMessage());
        }

        return null;
    }
}
