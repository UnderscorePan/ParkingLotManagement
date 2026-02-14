package com.parkingLot.models.fines;

import java.time.Duration;
import java.util.Objects;

public class ProgressiveFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(Duration overstay) {
        Objects.requireNonNull(overstay);

        long hours = overstay.toHours();

        if (hours <= 24) return 50.0;
        else if (hours <= 48) return 150.0;
        else if (hours <= 72) return 300.0;
        else return 500.0;
    }
}
