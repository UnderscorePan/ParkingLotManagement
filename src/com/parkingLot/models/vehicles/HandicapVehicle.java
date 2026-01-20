package com.parkingLot.models.vehicels;

public class HandicapVehicle extends Vehicle {
    public HandicapVehicle(String licensePlate) {
        super(licensePlate, VehicleType.HANDICAP_VEHICLE);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return true;
    }
}

