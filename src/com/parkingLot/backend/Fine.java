package com.parkingLot.backend;
import java.time.LocalDateTime;


public class Fine {
    private String fineId;
    private String licensePlate;
    private double amount;
    private boolean isPaid;
    private LocalDateTime issueDate;
    private String reason;

    public Fine(String fineId, String licensePlate, double amount, LocalDateTime issueDate, String reason) {
        this.fineId = fineId;
        this.licensePlate = licensePlate;
        this.amount = amount;
        this.isPaid = false;
        this.issueDate = issueDate;
        this.reason = reason;
    }

    public void paidFine() {
        this.isPaid = true;
    }

    public String getFineId() {
        return fineId;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public double getAmount() {
        return amount;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public LocalDateTime getIssueDate() {
        return issueDate;
    }
    public String getReason() {
        return reason;
    }
}