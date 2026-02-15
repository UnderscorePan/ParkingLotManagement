package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkingLot.database.databaseConnection;

public class FloorController {
    
    public List<Map<String, Object>> getFloorStatistics() {
        List<Map<String, Object>> floorStats = new ArrayList<>();
        
        String sql = "SELECT floor_number, " +
                     "COUNT(*) as total_spots, " +
                     "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as occupied_spots, " +
                     "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as available_spots " +
                     "FROM parking_spots " +
                     "GROUP BY floor_number " +
                     "ORDER BY floor_number";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> floor = new HashMap<>();
                floor.put("floor_number", rs.getInt("floor_number"));
                floor.put("total_spots", rs.getInt("total_spots"));
                floor.put("occupied_spots", rs.getInt("occupied_spots"));
                floor.put("available_spots", rs.getInt("available_spots"));
                floorStats.add(floor);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting floor statistics: " + e.getMessage());
        }
        
        return floorStats;
    }
    
    public boolean floorHasSpots(int floorNumber) {
        String sql = "SELECT COUNT(*) FROM parking_spots WHERE floor_number = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, floorNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking floor spots: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean floorHasOccupiedSpots(int floorNumber) {
        String sql = "SELECT COUNT(*) FROM parking_spots WHERE floor_number = ? AND status = 1";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, floorNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking occupied spots: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean deleteFloorSpots(int floorNumber) {
        String sql = "DELETE FROM parking_spots WHERE floor_number = ?";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, floorNumber);
            pstmt.executeUpdate();
            System.out.println("✅ All spots on floor " + floorNumber + " deleted");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting floor spots: " + e.getMessage());
            return false;
        }
    }
    
    public List<Integer> getAllFloorNumbers() {
        List<Integer> floors = new ArrayList<>();
        String sql = "SELECT DISTINCT floor_number FROM parking_spots ORDER BY floor_number";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                floors.add(rs.getInt("floor_number"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting floor numbers: " + e.getMessage());
        }
        
        return floors;
    }
}
