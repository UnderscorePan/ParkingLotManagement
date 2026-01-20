package com.parkingLot.tests;

import com.parkingLot.models.spots.SpotType;
import com.parkingLot.models.vehicles.*;


public class VehicleTester {
    public static void main(String[] args) {
        System.out.println("Vehicle Tester Running");

        Vehicle[] vehicles = {
                new Car("EG6-1"),
                new Motorcycle("Lalazai-1"),
                new SUV("RangeRover-1"),
                new HandicapVehicle("Tesla-1")
        };

        System.out.println("Vehicle parking capability: ");
        for (Vehicle v : vehicles) {
            System.out.println("\n" + v.getClass().getSimpleName() + " (" + v.getLicensePlate() + "):");
            for (SpotType st : SpotType.values()) {
                System.out.printf("  -> %b%n", st, v.canParkInSpot(st)); //null would print false for %b

            }

        }
    }
}
