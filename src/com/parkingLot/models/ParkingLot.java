package com.parkingLot.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.parkingLot.models.components.ParkingComponent;

/**
 * Root Composite class representing an entire parking lot.
 * Can contain Floors or other ParkingComponents, enabling flexible hierarchies.
 * Manages recursive operations across the entire component tree.
 */
public class ParkingLot implements ParkingComponent {
    private final String lotId;
    private final List<ParkingComponent> components; // Generic container for any ParkingComponent

    public ParkingLot(String lotId) {
        this.lotId = lotId;
        this.components = new ArrayList<>();
    }

    /**
     * Add any ParkingComponent (Floor, Zone, etc.) to this parking lot
     * @param component The component to add
     */
    public void addComponent(ParkingComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.add(component);
    }

    /**
     * Remove a component from this parking lot
     * @param component The component to remove
     */
    public void removeComponent(ParkingComponent component) {
        components.remove(component);
    }

    /**
     * Legacy method for backward compatibility - adds a Floor
     * @param floor The floor to add
     */
    public void addFloor(Floor floor) {
        addComponent(floor);
    }

    /**
     * Legacy method for backward compatibility - removes a Floor
     * @param floor The floor to remove
     */
    public void removeFloor(Floor floor) {
        removeComponent(floor);
    }

    /**
     * Legacy method for backward compatibility - finds a floor by number
     * @param floorNumber The floor number to search for
     * @return Optional containing the floor if found
     */
    public Optional<Floor> getFloor(int floorNumber) {
        return components.stream()
                .filter(c -> c instanceof Floor)
                .map(c -> (Floor) c)
                .filter(f -> f.getFloorNumber() == floorNumber)
                .findFirst();
    }

    // ============ COMPOSITE INTERFACE IMPLEMENTATIONS (RECURSIVE) ============
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
        int totalOccupied = getOccupiedSpots();
        return (totalOccupied * 100.0) / totalSpots;
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
        return false; // ParkingLot is a container, not a leaf
    }

    // ============ UTILITY METHODS ============
    public int getAvailableSpotCount() {
        return getAvailableSpots().size();
    }

    public List<ParkingComponent> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Legacy method for backward compatibility
     * @return List of Floors (only Floor components)
     */
    public List<Floor> getFloors() {
        List<Floor> floors = new ArrayList<>();
        for (ParkingComponent component : components) {
            if (component instanceof Floor) {
                floors.add((Floor) component);
            }
        }
        return floors;
    }

    /**
     * Legacy method for backward compatibility
     * @return Number of Floor components
     */
    public int getFloorCount() {
        return (int) components.stream()
                .filter(c -> c instanceof Floor)
                .count();
    }

    public String getLotId() {
        return lotId;
    }
}
