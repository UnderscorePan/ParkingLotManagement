package com.parkingLot.models.components;

import java.util.List;

import com.parkingLot.models.ParkingSpot;


public interface ParkingComponent {

    List<ParkingSpot> getAvailableSpots();
    
    double getOccupancyRate();
    
    int getTotalSpots();
    
    int getOccupiedSpots();
    
    boolean isLeaf();
}
