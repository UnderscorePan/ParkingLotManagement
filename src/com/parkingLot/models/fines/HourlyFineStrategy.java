package com.parkingLot.models.fines;

import java.time.Duration;
import java.util.Objects;

public class HourlyFineStrategy implements FineStrategy {
    private final double ratePerHour;
    private final long gracePeriodMinutes;

    public HourlyFineStrategy(double ratePerHour) {
        this(ratePerHour, 0);
    }

    public HourlyFineStrategy(double ratePerHour, long gracePeriodMinutes) {
        if (ratePerHour < 0){
            throw new IllegalArgumentException("Rate per hour cannot be 0 or less");
        }
        this.ratePerHour = ratePerHour;
        this.gracePeriodMinutes = Math.max(0, gracePeriodMinutes); // max prevents negative grace period
    }

    @Override
    public double calculateFine(Duration overstay){
     Objects.requireNonNull(overstay)   ;
     if (overstay.isZero() || overstay.isNegative()){
         return 0.0;
     }

     long minutes = overstay.toMinutes(){
         if(minutes <= gracePeriodMinutes){
             return 0.0;
         }
        }

     long billableMinutes = minutes - gracePeriodMinutes;
     long hours = (billableMinutes + 59) / 60;
     return hours * ratePerHour;
    }
}
