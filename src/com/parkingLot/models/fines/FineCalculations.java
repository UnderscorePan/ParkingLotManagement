package com.parkingLot.models.fines;

import java.time.Duration;
import java.time.LocalDateTime;

public class FineCalculations {

    private static final long OVERSTAY_THRESHOLD_HOURS = 24;

    public static boolean hasOverstayed(LocalDateTime entryTime, LocalDateTime currentTime) {
        if (entryTime == null || currentTime == null) {
            return false;
        }
        return Duration.between(entryTime, currentTime).toHours() >= OVERSTAY_THRESHOLD_HOURS;
    }

    public static Duration calculateOverstayDuration(LocalDateTime entryTime, LocalDateTime currentTime) {
        if (entryTime == null || currentTime == null) {
            return Duration.ZERO;
        }

        long totalHours = Duration.between(entryTime, currentTime).toHours();
        if (totalHours < OVERSTAY_THRESHOLD_HOURS) {
            return Duration.ZERO;
        }

        return Duration.ofHours(totalHours - OVERSTAY_THRESHOLD_HOURS);
    }

    public static double calculateFineAmount(FineStrategy strategy, LocalDateTime entryTime, LocalDateTime currentTime) {
        if (strategy == null) {
            throw new IllegalArgumentException("Fine strategy cannot be null");
        }

        Duration overstay = calculateOverstayDuration(entryTime, currentTime);
        return overstay.isZero() ? 0.0 : strategy.calculateFine(overstay);
    }

    public static String generateFineId(String licensePlate, LocalDateTime issueDate) {
        return "FINE-" + licensePlate + "-" + issueDate.toString().replace(":", "").replace("-", "");
    }

    public static Fine createFineForOverstay(String licensePlate, FineStrategy strategy,
                                             LocalDateTime entryTime, LocalDateTime exitTime,
                                             String reason) {
        double amount = calculateFineAmount(strategy, entryTime, exitTime);
        if (amount <= 0) {
            return null;
        }

        String fineId = generateFineId(licensePlate, exitTime);
        return new Fine(fineId, licensePlate, amount, exitTime, reason);
    }
}
