package com.parkingLot.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.parkingLot.models.components.ParkingComponent;

public class ParkingLot implements ParkingComponent {
    private final String lotId;
    private final List<ParkingComponent> components; 

    public ParkingLot(String lotId) {
        this.lotId = lotId;
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

    public void addFloor(Floor floor) {
        addComponent(floor);
    }

    public void removeFloor(Floor floor) {
        removeComponent(floor);
    }

    public Optional<Floor> getFloor(int floorNumber) {
        return components.stream()
                .filter(c -> c instanceof Floor)
                .map(c -> (Floor) c)
                .filter(f -> f.getFloorNumber() == floorNumber)
                .findFirst();
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
        int totalOccupied = getOccupiedSpots();
        return (totalOccupied * 100.0) / totalSpots;
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

    public int getAvailableSpotCount() {
        return getAvailableSpots().size();
    }

    public List<ParkingComponent> getComponents() {
        return new ArrayList<>(components);
    }

    public List<Floor> getFloors() {
        List<Floor> floors = new ArrayList<>();
        for (ParkingComponent component : components) {
            if (component instanceof Floor) {
                floors.add((Floor) component);
            }
        }
        return floors;
    }

    public int getFloorCount() {
        return (int) components.stream()
                .filter(c -> c instanceof Floor)
                .count();
    }

    public String getLotId() {
        return lotId;
    }
}
