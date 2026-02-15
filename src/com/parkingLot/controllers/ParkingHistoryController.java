package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkingLot.database.databaseConnection;

public class ParkingHistoryController {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public boolean createParkingEntry(String licensePlate, LocalDateTime entryTime, String spotId) {
        String sql = "INSERT INTO parking_history (plate_number, entry_time, spot_id) VALUES (?, ?, ?)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            pstmt.setString(2, entryTime.format(DATE_FORMATTER));
            pstmt.setString(3, spotId);
            
            pstmt.executeUpdate();
            System.out.println("Parking entry created for " + licensePlate);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating parking entry: " + e.getMessage());
            return false;
        }
    }
    
    public boolean completeParkingExit(String licensePlate, LocalDateTime exitTime, double feeCharged) {
        String sql = "UPDATE parking_history SET exit_time = ?, fee_charged = ? " +
                     "WHERE plate_number = ? AND exit_time IS NULL";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exitTime.format(DATE_FORMATTER));
            pstmt.setDouble(2, feeCharged);
            pstmt.setString(3, licensePlate);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Parking exit completed for " + licensePlate);
                return true;
            } else {
                System.err.println("No active parking session found for: " + licensePlate);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error completing parking exit: " + e.getMessage());
            return false;
        }
    }
    
    public Map<String, Object> getActiveParkingSession(String licensePlate) {
        String sql = "SELECT id, plate_number, entry_time, spot_id FROM parking_history " +
                     "WHERE plate_number = ? AND exit_time IS NULL";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> session = new HashMap<>();
                session.put("id", rs.getInt("id"));
                session.put("plate_number", rs.getString("plate_number"));
                session.put("entry_time", LocalDateTime.parse(rs.getString("entry_time"), DATE_FORMATTER));
                session.put("spot_id", rs.getString("spot_id"));
                return session;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active parking session: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Map<String, Object>> getAllActiveSessions() {
        List<Map<String, Object>> sessions = new ArrayList<>();
        String sql = "SELECT ph.id, ph.plate_number, ph.entry_time, ph.spot_id, v.vehicle_type " +
                     "FROM parking_history ph " +
                     "JOIN vehicles v ON ph.plate_number = v.plate_number " +
                     "WHERE ph.exit_time IS NULL " +
                     "ORDER BY ph.entry_time";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> session = new HashMap<>();
                session.put("id", rs.getInt("id"));
                session.put("plate_number", rs.getString("plate_number"));
                session.put("entry_time", LocalDateTime.parse(rs.getString("entry_time"), DATE_FORMATTER));
                session.put("spot_id", rs.getString("spot_id"));
                session.put("vehicle_type", rs.getString("vehicle_type"));
                sessions.add(session);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active sessions: " + e.getMessage());
        }
        
        return sessions;
    }
    
    public boolean isVehicleParked(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM parking_history WHERE plate_number = ? AND exit_time IS NULL";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if vehicle is parked: " + e.getMessage());
        }
        
        return false;
    }
}
