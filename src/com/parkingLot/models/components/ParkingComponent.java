package com.parkingLot.models.components;

import java.util.List;

import com.parkingLot.models.ParkingSpot;

/**
 * Component interface for the Composite Design Pattern.
 * Defines common operations for both leaf (ParkingSpot) and composite (Floor, ParkingLot, Zone) objects.
 * 
 * This generalized interface enables infinite nesting: any component can contain other components,
 * allowing flexible hierarchies like: ParkingLot -> Zone -> Floor -> ParkingSpot
 */
public interface ParkingComponent {
    /**
     * Get all available parking spots in this component and its children (recursive)
     * @return List of available ParkingSpot objects
     */
    List<ParkingSpot> getAvailableSpots();
    
    /**
     * Get occupancy rate (0.0 to 100.0) for this component and its children (recursive)
     * @return Occupancy percentage
     */
    double getOccupancyRate();
    
    /**
     * Get total number of parking spots in this component and its children (recursive)
     * @return Total spot count
     */
    int getTotalSpots();
    
    /**
     * Get total occupied spots in this component and its children (recursive)
     * @return Occupied spot count
     */
    int getOccupiedSpots();
    
    /**
     * Check if this component is a leaf node (ParkingSpot)
     * @return true if this is a leaf, false if it's a container
     */
    boolean isLeaf();
}
