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

public class ParkingLotBuilder {
    
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
                
                Floor floor = floorMap.get(floorNumber);
                if (floor == null) {
                    floor = new Floor(floorNumber);
                    floorMap.put(floorNumber, floor);
                    parkingLot.addFloor(floor);
                }
                
                String spotId = rs.getString("spot_id");
                int row = Integer.parseInt(rs.getString("row_number"));
                int spotNumber = rs.getInt("spot_number");
                SpotType spotType = SpotType.valueOf(rs.getString("spot_type"));
                
                ParkingSpot spot = new ParkingSpot(spotId, floorNumber, row, spotNumber, spotType);
                
                int statusValue = rs.getInt("status");
                SpotStatus status = (statusValue == 1) ? SpotStatus.OCCUPIED : SpotStatus.AVAILABLE;
                spot.setStatus(status);
                
                floor.addParkingSpot(spot);
            }
            
            System.out.println("ParkingLot built from database: " + floorMap.size() + " floors loaded");
            
        } catch (SQLException e) {
            System.err.println("Error building parking lot from database: " + e.getMessage());
            e.printStackTrace();
        }
        
        return parkingLot;
    }
    
    public static ParkingLot buildFromDatabase() {
        String lotId = getFirstLotId();
        return buildFromDatabase(lotId);
    }
    
    private static String getFirstLotId() {
        String sql = "SELECT DISTINCT lot_id FROM parking_spots LIMIT 1";
        
        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getString("lot_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting lot_id: " + e.getMessage());
        }
        
        return "LOT-001"; 
    }
}
