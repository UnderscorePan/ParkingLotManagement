package com.parkingLot.models;

import java.util.ArrayList;
import java.util.List;

import com.parkingLot.models.components.ParkingComponent;
import com.parkingLot.models.spots.SpotStatus;
import com.parkingLot.models.spots.SpotType;
import com.parkingLot.models.vehicles.Vehicle;
import com.parkingLot.models.vehicles.VehicleType;

public class ParkingSpot implements ParkingComponent {
    private String spotId;
    private int floor;
    private int row;
    private int spotNumber;
    private SpotType spotType;
    private SpotStatus status;
    private Vehicle currentVehicle;
    private double hourlyRate;

    public ParkingSpot(String spotId, int floor, int row, int spotNumber, SpotType spotType) {
        this.spotId = spotId;
        this.floor = floor;
        this.row = row;
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.status = SpotStatus.AVAILABLE;
        this.hourlyRate = spotType.getHourlyRate();
    }

    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public boolean canAccommodate(Vehicle vehicle) {
        return isAvailable() && vehicle.canParkInSpot(spotType);
    }

    public void parkVehicle(Vehicle vehicle) {
        if (!canAccommodate(vehicle)) {
            throw new IllegalArgumentException("Vehicle cannot be parked in this spot.");
        }
        this.currentVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
    }

    public void removeVehicle() {
        this.currentVehicle = null;
        this.status = SpotStatus.AVAILABLE;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public double getEffectiveRate(Vehicle vehicle) {
        if (vehicle.getType() == VehicleType.HANDICAP_VEHICLE && this.spotType == SpotType.HANDICAP) {
            return 0.0;
        }
        if (vehicle.getType() == VehicleType.HANDICAP_VEHICLE) {
            return 2.0;
        }
        return this.hourlyRate;
    }

    public String getSpotId() {
        return spotId;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public int getFloor() {
        return floor;
    }

    public int getRow() {
        return row;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    // ============ COMPOSITE INTERFACE IMPLEMENTATIONS ============
    @Override
    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> result = new ArrayList<>();
        if (this.isAvailable()) {
            result.add(this);
        }
        return result;
    }

    @Override
    public double getOccupancyRate() {
        // Leaf node: either 0% (available) or 100% (occupied)
        return this.isAvailable() ? 0.0 : 100.0;
    }

    @Override
    public int getTotalSpots() {
        return 1; // Leaf node always has exactly 1 spot
    }

    @Override
    public int getOccupiedSpots() {
        return this.isAvailable() ? 0 : 1;
    }

    @Override
    public boolean isLeaf() {
        return true; // ParkingSpot is always a leaf node
    }
}