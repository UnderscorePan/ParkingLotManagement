package com.parkingLot.models;
import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot spot) {
        parkingSpots.add(spot);
    }

    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isAvailable()) {
                availableSpots.add(spot);
            }
        }
        return availableSpots;
    }

    public double getOccupancyRate() {
        if (parkingSpots.isEmpty()) return 0.0;
        long occupied = parkingSpots.stream()
            .filter(spot -> spot.getStatus() == SpotStatus.OCCUPIED)
            .count();
        return (occupied * 100.0) / parkingSpots.size();
    }

    public int getFloorNumber() {
        return floorNumber;
    }
    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
}