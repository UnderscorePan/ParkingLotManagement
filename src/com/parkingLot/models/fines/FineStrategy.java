package com.parkingLot.models.fines;

import java.time.Duration;

public interface FineStrategy {

    double calculateFine(Duration overstay);
}
