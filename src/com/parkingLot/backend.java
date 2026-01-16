package com.parkingLot;
import java.time.LocalDateTime;

public enum VehicleType {
    MOTORCYCLE,
    CAR,
    SUV,
    HANDICAP
}

public enum SpotType {
    COMPACT(2.0),
    REGULAR(5.0),
    HANDICAP(2.0),
    RESERVED(10.0);

    private final double hourlyRate;

    SpotType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}

public enum SpotStatus {
    AVAILABLE,
    OCCUPIED
}

public enum PaymentType {
    CASH, 
    CARD,
}

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

public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.COMPACT;
    }
}

public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.REGULAR || spotType == SpotType.COMPACT;
    }
}

public class SUV extends Vehicle {
    public SUV(String licensePlate) {
        super(licensePlate, VehicleType.SUV);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.REGULAR;
    }
}

public class HandicapVehicle extends Vehicle {
    public HandicapVehicle(String licensePlate) {
        super(licensePlate, VehicleType.HANDICAP);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return true;
    }
}

