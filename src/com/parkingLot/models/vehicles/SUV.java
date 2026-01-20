package com.parkingLot.models.vehicles;

import com.parkingLot.models.spots.SpotType;

public class SUV extends Vehicle {
    public SUV(String licensePlate) {
        super(licensePlate, VehicleType.SUV);
    }

    @Override
    public boolean canParkInSpot(SpotType spotType) {
        return spotType == SpotType.REGULAR;
    }
}