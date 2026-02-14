package com.parkingLot.tests;

import com.parkingLot.controllers.FineController;
import com.parkingLot.models.fines.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FineTester {

    public static void main(String[] args) {
        System.out.println("=== Fine System Testing ===\n");
        testFineStrategies();
        testFineCalculationsUtility();
        testFineController();
    }

    private static void testFineStrategies() {
        System.out.println("--- Test 1: Fine Strategy Calculations ---");

        Duration[] durations = {
            Duration.ofHours(2), Duration.ofHours(30),
            Duration.ofHours(50), Duration.ofHours(80)
        };

        FineStrategy[] strategies = {
            new FixedFineStrategy(),
            new HourlyFineStrategy(20.0),
            new ProgressiveFineStrategy()
        };
        String[] names = {"Fixed (RM 50 flat)", "Hourly (RM 20/hr)", "Progressive"};

        for (int i = 0; i < strategies.length; i++) {
            System.out.println("\n" + (i + 1) + ". " + names[i] + ":");
            for (Duration d : durations) {
                if (i == 2 || d.toHours() <= 50) {
                    System.out.printf("   %d hrs overstay: RM %.1f%n",
                        d.toHours(), strategies[i].calculateFine(d));
                }
            }
        }
        System.out.println();
    }

    private static void testFineCalculationsUtility() {
        System.out.println("--- Test 2: FineCalculations Utility ---");

        LocalDateTime entryTime = LocalDateTime.now().minusHours(30);
        LocalDateTime exitTime = LocalDateTime.now();

        boolean hasOverstayed = FineCalculations.hasOverstayed(entryTime, exitTime);
        Duration overstayDuration = FineCalculations.calculateOverstayDuration(entryTime, exitTime);

        System.out.println("\n1. Has vehicle overstayed (>24hrs)? " + hasOverstayed);
        System.out.println("2. Overstay duration: " + overstayDuration.toHours() + " hours");

        FineStrategy strategy = new HourlyFineStrategy(20.0);
        double fineAmount = FineCalculations.calculateFineAmount(strategy, entryTime, exitTime);
        System.out.println("3. Fine amount (Hourly): RM " + fineAmount);

        Fine fine = FineCalculations.createFineForOverstay(
            "ABC123", strategy, entryTime, exitTime, "Overstay violation (>24 hours)"
        );

        if (fine != null) {
            System.out.println("4. Fine created:");
            System.out.println("   - License: " + fine.getLicensePlate() + ", Amount: RM " + fine.getAmount());
            System.out.println("   - Reason: " + fine.getReason() + ", Paid: " + fine.isPaid());
        }

        System.out.println();
    }

    private static void testFineController() {
        System.out.println("--- Test 3: Fine Controller (Database) ---");

        FineController controller = new FineController();
        String testPlate = "TEST-" + System.currentTimeMillis();

        System.out.println("\n1. Setting fine scheme to PROGRESSIVE...");
        controller.setFineScheme(FineScheme.PROGRESSIVE);

        LocalDateTime entryTime = LocalDateTime.now().minusHours(28);
        LocalDateTime exitTime = LocalDateTime.now();

        System.out.println("2. Checking for overstay fine...");
        Fine newFine = controller.checkAndCreateOverstayFine(testPlate, entryTime, exitTime);

        if (newFine != null) {
            System.out.println("   ✓ Fine created: RM " + newFine.getAmount());
        }

        System.out.println("\n3. Retrieving unpaid fines for " + testPlate + "...");
        List<Fine> unpaidFines = controller.getUnpaidFines(testPlate);
        System.out.println("   Found " + unpaidFines.size() + " unpaid fine(s)");

        for (Fine fine : unpaidFines) {
            System.out.printf("   - ID: %s, Amount: RM %.1f, Paid: %b%n",
                fine.getFineId(), fine.getAmount(), fine.isPaid());
        }

        double totalUnpaid = controller.getTotalUnpaidFines(testPlate);
        System.out.println("\n4. Total unpaid fines: RM " + totalUnpaid);

        if (!unpaidFines.isEmpty()) {
            System.out.println("\n5. Marking all fines as paid...");
            boolean success = controller.markAllFinesAsPaid(testPlate);
            System.out.println("   " + (success ? "✓" : "✗") + " Fines marked as paid");

            List<Fine> afterPayment = controller.getUnpaidFines(testPlate);
            System.out.println("   Unpaid fines after payment: " + afterPayment.size());
        }

        System.out.println("\n=== Testing Complete ===");
    }
}
