package com.parkingLot.backend;

public class ParkingSpot {
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
}