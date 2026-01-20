package com.parkingLot.models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketId;
    private String licensePlate;
    private String spotId;
    private LocalDateTime entryTime;
    
    public Ticket(String ticketId, String licensePlate, String spotId, LocalDateTime entryTime) {
        this.ticketId = generateTicketId(licensePlate, entryTime);
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    private String generateTicketId(String licensePlate, LocalDateTime entryTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return licensePlate + "_" + entryTime.format(formatter);
    }

    @Override
    public String toString() {
        return String.format ("Ticket ID: %s, License Plate: %s, Spot ID: %s, Entry Time: %s",
            ticketId, licensePlate, spotId, entryTime.toString());
    }

    public String getTicketId() {
        return ticketId;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public String getSpotId() {
        return spotId;
    }
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}