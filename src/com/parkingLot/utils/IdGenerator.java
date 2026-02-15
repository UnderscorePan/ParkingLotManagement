package com.parkingLot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateFineId(String licensePlate, LocalDateTime timestamp) {
        String cleanPlate = licensePlate.replaceAll("[^a-zA-Z0-9]", "");
        return String.format("FINE-%s-%s", cleanPlate, timestamp.format(TIMESTAMP_FORMATTER));
    }

    public static String generateTicketId(String licensePlate, LocalDateTime timestamp) {
        String cleanPlate = licensePlate.replaceAll("[^a-zA-Z0-9]", "");
        return String.format("TKT-%s-%s", cleanPlate, timestamp.format(TIMESTAMP_FORMATTER));
    }

    public static String generateSpotId(int floor, int row, int spotNumber) {
        return String.format("F%d-R%d-%02d", floor, row, spotNumber);
    }

    public static String generateReceiptId(LocalDateTime timestamp) {
        int random = (int) (Math.random() * 1000);
        return String.format("RCP-%s-%03d", timestamp.format(TIMESTAMP_FORMATTER), random);
    }
}
