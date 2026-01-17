package com.parkingLot.backend;
import java.time.LocalDateTime;

public abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;
    protected String assignedSpotId;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public abstract boolean canParkInSpot(SpotType spotType);

    public String getLicensePlate() {
        return licensePlate;
    }
    public VehicleType getType() {
        return type;
    }
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    public String getAssignedSpotId() {
        return assignedSpotId;
    }
    public void setAssignedSpotId(String assignedSpotId) {
        this.assignedSpotId = assignedSpotId;
    }
}