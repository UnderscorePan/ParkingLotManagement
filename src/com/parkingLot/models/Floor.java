package com.parkingLot.models;

import java.util.ArrayList;
import java.util.List;

import com.parkingLot.models.components.ParkingComponent;

/**
 * Composite container representing a floor in the parking lot.
 * Can contain ParkingSpots or other ParkingComponents (e.g., Zones), enabling flexible hierarchies.
 */
public class Floor implements ParkingComponent {
    private final int floorNumber;
    private final List<ParkingComponent> components; // Generic container for any ParkingComponent

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.components = new ArrayList<>();
    }

    /**
     * Add any ParkingComponent (ParkingSpot, Zone, etc.) to this floor
     * @param component The component to add
     */
    public void addComponent(ParkingComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.add(component);
    }

    /**
     * Remove a component from this floor
     * @param component The component to remove
     */
    public void removeComponent(ParkingComponent component) {
        components.remove(component);
    }

    /**
     * Legacy method for backward compatibility - adds a ParkingSpot
     * @param spot The parking spot to add
     */
    public void addParkingSpot(ParkingSpot spot) {
        addComponent(spot);
    }

    /**
     * Legacy method for backward compatibility - removes a ParkingSpot
     * @param spot The parking spot to remove
     */
    public void removeParkingSpot(ParkingSpot spot) {
        removeComponent(spot);
    }

    @Override
    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        // Recursively collect available spots from all child components
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
        // Recursively calculate occupancy from all children
        int occupiedSpots = getOccupiedSpots();
        return (occupiedSpots * 100.0) / totalSpots;
    }

    @Override
    public int getTotalSpots() {
        // Recursively sum total spots from all child components
        return components.stream()
                .mapToInt(ParkingComponent::getTotalSpots)
                .sum();
    }

    @Override
    public int getOccupiedSpots() {
        // Recursively sum occupied spots from all child components
        return components.stream()
                .mapToInt(ParkingComponent::getOccupiedSpots)
                .sum();
    }

    @Override
    public boolean isLeaf() {
        return false; // Floor is a container, not a leaf
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingComponent> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Legacy method for backward compatibility
     * @return List of ParkingSpots (only leaf components)
     */
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