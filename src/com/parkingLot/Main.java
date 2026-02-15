package com.parkingLot;

import com.parkingLot.views.MainFrame;
import com.parkingLot.database.databaseConnection;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Initialize database first
        try {
            databaseConnection.initializeDatabase();
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Failed to initialize database!\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}