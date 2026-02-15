package com.parkingLot.views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.parkingLot.database.databaseConnection;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        setTitle("Parking Lot Management System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title Label
        JLabel titleLabel = new JLabel("Parking Lot Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Admin Button
        JButton adminButton = new JButton("Admin Panel");
        adminButton.setFont(new Font("Arial", Font.PLAIN, 16));
        adminButton.setPreferredSize(new Dimension(200, 50));
        adminButton.addActionListener(e -> openAdminGUI());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(adminButton, gbc);
        
        // Vehicle Button
        JButton vehicleButton = new JButton("Vehicle Panel");
        vehicleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        vehicleButton.setPreferredSize(new Dimension(200, 50));
        vehicleButton.addActionListener(e -> openVehicleGUI());
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(vehicleButton, gbc);
        
        setLocationRelativeTo(null);
    }
    
    private void openAdminGUI() {
        AdminGUI adminGUI = new AdminGUI();
        adminGUI.setVisible(true);
        this.dispose();
    }
    
    private void openVehicleGUI() {
        VehicleGUI vehicleGUI = new VehicleGUI();
        vehicleGUI.setVisible(true);
        this.dispose();
    }
    
    public static void main(String[] args) {
        // Initialize database first
        try {
            databaseConnection.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database!\n" + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Then show the main frame
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
