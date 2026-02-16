package com.parkingLot.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.fines.Fine;
import com.parkingLot.models.fines.FineCalculations;
import com.parkingLot.models.fines.FineScheme;
import com.parkingLot.models.fines.FineStrategy;
import com.parkingLot.models.fines.FixedFineStrategy;
import com.parkingLot.models.fines.HourlyFineStrategy;
import com.parkingLot.models.fines.ProgressiveFineStrategy;

public class FineController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static FineController instance;
    private FineStrategy currentStrategy;

    public FineController() {
        this.currentStrategy = new FixedFineStrategy();
    }

    public static FineController getInstance() {
        if (instance == null) {
            instance = new FineController();
        }
        return instance;
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
        return saveFine(fine, getFineTypeName(currentStrategy));
    }
    
    public boolean saveFine(Fine fine, String fineType) {
        String sql = "INSERT INTO fines (plate_number, fine_amount, fine_type, is_paid, fine_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fine.getLicensePlate());
            pstmt.setDouble(2, fine.getAmount());
            pstmt.setString(3, fineType);
            pstmt.setInt(4, fine.isPaid() ? 1 : 0);
            pstmt.setString(5, fine.getIssueDate().format(DATE_FORMATTER));
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving fine: " + e.getMessage());
            return false;
        }
    }
    
    private String getFineTypeName(FineStrategy strategy) {
        if (strategy instanceof FixedFineStrategy) {
            return "Fixed Fine Scheme";
        } else if (strategy instanceof HourlyFineStrategy) {
            return "Hourly Fine Scheme";
        } else if (strategy instanceof ProgressiveFineStrategy) {
            return "Progressive Fine Scheme";
        }
        return "Unknown";
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
                    LocalDateTime.parse(rs.getString("fine_date"), DATE_FORMATTER)
                );
                fines.add(fine);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving unpaid fines: " + e.getMessage());
        }

        return fines;
    }


    public double getTotalUnpaidFines(String licensePlate) {
        List<Fine> fines = getUnpaidFines(licensePlate);
        double total = fines.stream().mapToDouble(Fine::getAmount).sum();
        System.out.println("Checking unpaid fines for: " + licensePlate + " → Found " + fines.size() + " fine(s), Total: RM " + total);
        return total;
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
            licensePlate, currentStrategy, entryTime, exitTime
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
                    LocalDateTime.parse(rs.getString("fine_date"), DATE_FORMATTER)
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
