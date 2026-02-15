package com.parkingLot.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.Floor;
import com.parkingLot.models.ParkingLot;
import com.parkingLot.models.ParkingSpot;
import com.parkingLot.models.spots.SpotStatus;
import com.parkingLot.models.spots.SpotType;

/**
 * Utility class to build ParkingLot object hierarchy from database.
 * Demonstrates the Composite Pattern by constructing the tree structure:
 * ParkingLot -> Floor -> ParkingSpot
 */
public class ParkingLotBuilder {
    
    /**
     * Builds a complete ParkingLot structure from the database.
     * This method demonstrates the Composite Pattern by creating a hierarchical structure
     * where ParkingLot contains Floors, and Floors contain ParkingSpots.
     * 
     * @param lotId The parking lot ID to build
     * @return ParkingLot object with all floors and spots loaded
     */
    public static ParkingLot buildFromDatabase(String lotId) {
        ParkingLot parkingLot = new ParkingLot(lotId);
        Map<Integer, Floor> floorMap = new HashMap<>();
        
        String sql = "SELECT * FROM parking_spots WHERE lot_id = ? ORDER BY floor_number, row_number, spot_number";
        
        try (Connection conn = databaseConnection.connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lotId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int floorNumber = rs.getInt("floor_number");
                
                // Get or create floor
                Floor floor = floorMap.get(floorNumber);
                if (floor == null) {
                    floor = new Floor(floorNumber);
                    floorMap.put(floorNumber, floor);
                    parkingLot.addFloor(floor);
                }
                
                // Create parking spot
                String spotId = rs.getString("spot_id");
                int row = Integer.parseInt(rs.getString("row_number"));
                int spotNumber = rs.getInt("spot_number");
                SpotType spotType = SpotType.valueOf(rs.getString("spot_type"));
                
                ParkingSpot spot = new ParkingSpot(spotId, floorNumber, row, spotNumber, spotType);
                
                // Set status from database
                int statusValue = rs.getInt("status");
                SpotStatus status = (statusValue == 1) ? SpotStatus.OCCUPIED : SpotStatus.AVAILABLE;
                spot.setStatus(status);
                
                floor.addParkingSpot(spot);
            }
            
            System.out.println("✅ ParkingLot built from database: " + floorMap.size() + " floors loaded");
            
        } catch (SQLException e) {
            System.err.println("❌ Error building parking lot from database: " + e.getMessage());
            e.printStackTrace();
        }
        
        return parkingLot;
    }
    
    /**
     * Builds a ParkingLot structure for all lots in the database.
     * Uses the first available lot_id or "LOT-001" as default.
     * 
     * @return ParkingLot object with all floors and spots loaded
     */
    public static ParkingLot buildFromDatabase() {
        // Get the first lot_id from database, or use default
        String lotId = getFirstLotId();
        return buildFromDatabase(lotId);
    }
    
    /**
     * Gets the first lot_id from the database, or returns default "LOT-001"
     */
    private static String getFirstLotId() {
        String sql = "SELECT DISTINCT lot_id FROM parking_spots LIMIT 1";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getString("lot_id");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting lot_id: " + e.getMessage());
        }
        
        return "LOT-001"; // Default lot ID
    }
}
