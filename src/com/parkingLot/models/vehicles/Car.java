package com.parkingLot.models.vehicles;

import com.parkingLot.models.spots.SpotType;

public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.REGULAR || spotType == SpotType.COMPACT;
    }
}