package com.parkingLot.models;

import java.util.ArrayList;
import java.util.List;

import com.parkingLot.models.components.ParkingComponent;


public class Floor implements ParkingComponent {
    private final int floorNumber;
    private final List<ParkingComponent> components; 

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.components = new ArrayList<>();
    }

    public void addComponent(ParkingComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.add(component);
    }

    public void removeComponent(ParkingComponent component) {
        components.remove(component);
    }

    public void addParkingSpot(ParkingSpot spot) {
        addComponent(spot);
    }

    public void removeParkingSpot(ParkingSpot spot) {
        removeComponent(spot);
    }

    @Override
    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingComponent component : components) {
            availableSpots.addAll(component.getAvailableSpots());
        }
        return availableSpots;
    }

    @Override
    public double getOccupancyRate() {
        if (components.isEmpty()) return 0.0;
        int totalSpots = getTotalSpots();
        if (totalSpots == 0) return 0.0;
        int occupiedSpots = getOccupiedSpots();
        return (occupiedSpots * 100.0) / totalSpots;
    }

    @Override
    public int getTotalSpots() {
        return components.stream()
                .mapToInt(ParkingComponent::getTotalSpots)
                .sum();
    }

    @Override
    public int getOccupiedSpots() {
        return components.stream()
                .mapToInt(ParkingComponent::getOccupiedSpots)
                .sum();
    }

    @Override
    public boolean isLeaf() {
        return false; 
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingComponent> getComponents() {
        return new ArrayList<>(components);
    }

    public List<ParkingSpot> getParkingSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        for (ParkingComponent component : components) {
            if (component.isLeaf()) {
                spots.add((ParkingSpot) component);
            }
        }
        return spots;
    }
}