package com.parkingLot.models.vehicels;

public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.COMPACT;
    }
}