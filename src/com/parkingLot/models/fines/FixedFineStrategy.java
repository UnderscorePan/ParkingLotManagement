package com.parkingLot.models.fines;

import java.time.Duration;
import java.util.Objects;

public class FixedFineStrategy implements FineStrategy {
    private static final double FIXED_FINE = 50.0;

    @Override
    public double calculateFine(Duration overstay) {
        Objects.requireNonNull(overstay);
    }
}
