package com.parkingLot.controllers;

import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.fines.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FineController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private FineStrategy currentStrategy;

    public FineController() {
        this.currentStrategy = new FixedFineStrategy();
    }

    public void setFineScheme(FineScheme scheme) {
        switch (scheme) {
            case FIXED:
                this.currentStrategy = new FixedFineStrategy();
                break;
            case HOURLY:
                this.currentStrategy = new HourlyFineStrategy(20.0, 15);
                break;
            case PROGRESSIVE:
                this.currentStrategy = new ProgressiveFineStrategy();
                break;
            default:
                this.currentStrategy = new FixedFineStrategy();
        }
    }

    public FineStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public boolean saveFine(Fine fine) {
        String sql = "INSERT INTO fines (plate_number, fine_amount, fine_reason, is_paid, fine_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fine.getLicensePlate());
            pstmt.setDouble(2, fine.getAmount());
            pstmt.setString(3, fine.getReason());
            pstmt.setInt(4, fine.isPaid() ? 1 : 0);
            pstmt.setString(5, fine.getIssueDate().format(DATE_FORMATTER));
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving fine: " + e.getMessage());
            return false;
        }
    }

    public List<Fine> getUnpaidFines(String licensePlate) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines WHERE plate_number = ? AND is_paid = 0";

        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Fine fine = new Fine(
                    String.valueOf(rs.getInt("fine_id")),
                    rs.getString("plate_number"),
                    rs.getDouble("fine_amount"),
                    LocalDateTime.parse(rs.getString("fine_date"), DATE_FORMATTER),
                    rs.getString("fine_reason")
                );
                fines.add(fine);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving unpaid fines: " + e.getMessage());
        }

        return fines;
    }

    public double getTotalUnpaidFines(String licensePlate) {
        return getUnpaidFines(licensePlate).stream().mapToDouble(Fine::getAmount).sum();
    }

    public boolean markFineAsPaid(String fineId) {
        String sql = "UPDATE fines SET is_paid = 1 WHERE fine_id = ?";

        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(fineId));
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error marking fine as paid: " + e.getMessage());
            return false;
        }
    }

    public boolean markAllFinesAsPaid(String licensePlate) {
        String sql = "UPDATE fines SET is_paid = 1 WHERE plate_number = ? AND is_paid = 0";

        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error marking fines as paid: " + e.getMessage());
            return false;
        }
    }

    public Fine checkAndCreateOverstayFine(String licensePlate, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (!FineCalculations.hasOverstayed(entryTime, exitTime)) {
            return null;
        }

        Fine fine = FineCalculations.createFineForOverstay(
            licensePlate, currentStrategy, entryTime, exitTime, "Overstay violation (>24 hours)"
        );

        if (fine != null) {
            saveFine(fine);
        }
        return fine;
    }

    public List<Fine> getAllFines() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines ORDER BY fine_date DESC";

        try (Connection conn = databaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fine fine = new Fine(
                    String.valueOf(rs.getInt("fine_id")),
                    rs.getString("plate_number"),
                    rs.getDouble("fine_amount"),
                    LocalDateTime.parse(rs.getString("fine_date"), DATE_FORMATTER),
                    rs.getString("fine_reason")
                );
                if (rs.getInt("is_paid") == 1) {
                    fine.paidFine();
                }
                fines.add(fine);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all fines: " + e.getMessage());
        }

        return fines;
    }
}
