package com.parkingLot.backend;

public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.REGULAR || spotType == SpotType.COMPACT;
    }
}