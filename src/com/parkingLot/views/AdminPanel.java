package com.parkingLot.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.parkingLot.controllers.*;
import com.parkingLot.models.ParkingSpot;
import com.parkingLot.models.spots.SpotType;

public class AdminPanel extends JPanel {
    
    private static final String LOT_ID = "LOT-001";

    // Controllers
    private ParkingSpotController spotController;
    private ParkingHistoryController historyController;
    private FineController fineController;

    private JTable spotsTable;
    private DefaultTableModel spotsTableModel;
    private JTable vehiclesTable;
    private DefaultTableModel vehiclesTableModel;
    private JComboBox<String> fineSchemeCombo;
    private JLabel occupancyLabel;
    private JLabel revenueLabel;
    private JLabel totalFinesLabel;
    private JButton refreshButton;
    
    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Initialize controllers
        spotController = new ParkingSpotController();
        historyController = new ParkingHistoryController();
        fineController = FineController.getInstance(); // Use singleton

        initializeComponents();
        loadAllData();
    }
    
    private void initializeComponents() {
        // Create main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.5);
        
        // Top section - Statistics and Controls
        JPanel topPanel = createTopPanel();
        
        // Bottom section - Tables
        JPanel bottomPanel = createBottomPanel();
        
        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Control Panel at the top
        JPanel controlPanel = createControlPanel();
        
        // Statistics Panel below controls
        JPanel statsPanel = createStatsPanel();

        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        return topPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBorder(createTitledBorder("Statistics Dashboard"));

        // Occupancy
        JPanel occupancyPanel = createStatCard("Occupancy Rate", "0%", new Color(46, 204, 113));
        occupancyLabel = (JLabel) ((JPanel) occupancyPanel.getComponent(1)).getComponent(0);
        
        // Revenue
        JPanel revenuePanel = createStatCard("Total Revenue", "RM 0.00", new Color(52, 152, 219));
        revenueLabel = (JLabel) ((JPanel) revenuePanel.getComponent(1)).getComponent(0);
        
        // Fines
        JPanel finesPanel = createStatCard("Unpaid Fines", "RM 0.00", new Color(231, 76, 60));
        totalFinesLabel = (JLabel) ((JPanel) finesPanel.getComponent(1)).getComponent(0);
        
        statsPanel.add(occupancyPanel);
        statsPanel.add(revenuePanel);
        statsPanel.add(finesPanel);

        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setPreferredSize(new Dimension(220, 100));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        cardPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valuePanel, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(createTitledBorder("Admin Controls"));

        // Fine Scheme Section
        JLabel schemeLabel = new JLabel("Fine Scheme:");
        schemeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        fineSchemeCombo = new JComboBox<>(new String[]{
            "Option A: Fixed Fine (RM 50)",
            "Option B: Progressive Fine",
            "Option C: Hourly Fine (RM 20/hour)"
        });
        fineSchemeCombo.setPreferredSize(new Dimension(250, 30));

        JButton applyButton = new JButton("Apply Scheme");
        applyButton.setBackground(new Color(52, 152, 219));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> applyFineScheme());
        
        // Parking Spot Management Section
        JButton addSpotButton = new JButton("Add Spot");
        addSpotButton.setPreferredSize(new Dimension(120, 30));
        addSpotButton.setBackground(new Color(46, 204, 113));
        addSpotButton.setForeground(Color.WHITE);
        addSpotButton.setFocusPainted(false);
        addSpotButton.addActionListener(e -> showAddSpotDialog());

        JButton removeSpotButton = new JButton("Remove Spot");
        removeSpotButton.setPreferredSize(new Dimension(120, 30));
        removeSpotButton.setBackground(new Color(231, 76, 60));
        removeSpotButton.setForeground(Color.WHITE);
        removeSpotButton.setFocusPainted(false);
        removeSpotButton.addActionListener(e -> removeSelectedSpot());

        refreshButton = new JButton("Refresh Data");
        refreshButton.setPreferredSize(new Dimension(120, 30));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadAllData());

        controlPanel.add(schemeLabel);
        controlPanel.add(fineSchemeCombo);
        controlPanel.add(applyButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(addSpotButton);
        controlPanel.add(removeSpotButton);
        controlPanel.add(refreshButton);
        
        return controlPanel;
    }
    

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Spots Table
        JPanel spotsPanel = new JPanel(new BorderLayout());
        spotsPanel.setBorder(createTitledBorder("All Parking Spots"));
        
        String[] spotsColumns = {"Spot ID", "Floor", "Type", "Status", "Hourly Rate", "Current Vehicle"};
        spotsTableModel = new DefaultTableModel(spotsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        spotsTable = new JTable(spotsTableModel);
        spotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spotsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane spotsScrollPane = new JScrollPane(spotsTable);
        spotsPanel.add(spotsScrollPane, BorderLayout.CENTER);
        
        // Add button panel for spots
        JPanel spotsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewSpotButton = new JButton("View Details");
        viewSpotButton.addActionListener(e -> viewSelectedSpot());
        spotsButtonPanel.add(viewSpotButton);
        spotsPanel.add(spotsButtonPanel, BorderLayout.SOUTH);

        // Vehicles Table
        JPanel vehiclesPanel = new JPanel(new BorderLayout());
        vehiclesPanel.setBorder(createTitledBorder("Currently Parked Vehicles"));
        
        String[] vehiclesColumns = {"License Plate", "Vehicle Type", "Spot ID", "Entry Time", "Duration", "Unpaid Fines"};
        vehiclesTableModel = new DefaultTableModel(vehiclesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehiclesTable = new JTable(vehiclesTableModel);
        vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehiclesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane vehiclesScrollPane = new JScrollPane(vehiclesTable);
        vehiclesPanel.add(vehiclesScrollPane, BorderLayout.CENTER);
        
        bottomPanel.add(spotsPanel);
        bottomPanel.add(vehiclesPanel);
        
        return bottomPanel;
    }
    
    private Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(44, 62, 80)
        );
    }
    
    private void applyFineScheme() {
        String selectedScheme = (String) fineSchemeCombo.getSelectedItem();

        // Map the selection to FineScheme enum
        com.parkingLot.models.fines.FineScheme scheme;
        if (selectedScheme.contains("Fixed")) {
            scheme = com.parkingLot.models.fines.FineScheme.FIXED;
        } else if (selectedScheme.contains("Progressive")) {
            scheme = com.parkingLot.models.fines.FineScheme.PROGRESSIVE;
        } else {
            scheme = com.parkingLot.models.fines.FineScheme.HOURLY;
        }

        // Apply the scheme to the fine controller
        fineController.setFineScheme(scheme);

        JOptionPane.showMessageDialog(this,
            "Fine scheme updated to: " + selectedScheme + "\n" +
            "This will apply to all new parking violations.",
            "Scheme Applied",
            JOptionPane.INFORMATION_MESSAGE);

        System.out.println("✅ Fine scheme changed to: " + scheme);
    }

    private void showAddSpotDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Parking Spot", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Floor
        JLabel floorLabel = new JLabel("Floor Number:");
        JSpinner floorSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Row
        JLabel rowLabel = new JLabel("Row (A-Z):");
        JTextField rowField = new JTextField("A");

        // Spot Number
        JLabel spotNumLabel = new JLabel("Spot Number:");
        JSpinner spotNumSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Spot Type
        JLabel typeLabel = new JLabel("Spot Type:");
        JComboBox<SpotType> typeCombo = new JComboBox<>(SpotType.values());

        // Reserved For (optional)
        JLabel reservedLabel = new JLabel("Reserved For Plate:");
        JTextField reservedPlateField = new JTextField();
        reservedPlateField.setToolTipText("Optional: Enter license plate for RESERVED spots");

        formPanel.add(floorLabel);
        formPanel.add(floorSpinner);
        formPanel.add(rowLabel);
        formPanel.add(rowField);
        formPanel.add(spotNumLabel);
        formPanel.add(spotNumSpinner);
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(reservedLabel);
        formPanel.add(reservedPlateField);

        // Info label
        JLabel infoLabel = new JLabel("<html><i>Note: Reserved plate only applies to RESERVED type spots</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Spot");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            try {
                int floor = (Integer) floorSpinner.getValue();
                String row = rowField.getText().trim().toUpperCase();
                int spotNum = (Integer) spotNumSpinner.getValue();
                SpotType type = (SpotType) typeCombo.getSelectedItem();
                String reservedPlate = reservedPlateField.getText().trim().toUpperCase();

                if (!reservedPlate.isEmpty() && type != SpotType.RESERVED) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Reserved plate is only for RESERVED type spots.\nContinue without reserving?",
                        "Note",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                    reservedPlate = null;
                }

                if (row.length() != 1 || !Character.isLetter(row.charAt(0))) {
                    JOptionPane.showMessageDialog(dialog,
                        "Row must be a single letter (A-Z)",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate spot ID
                String spotId = String.format("F%d-%s-%s%d", floor, type.toString(), row, spotNum);

                // Check if spot already exists
                if (spotController.spotExists(spotId)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Spot " + spotId + " already exists!",
                        "Duplicate Spot",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create and save spot
                ParkingSpot newSpot = new ParkingSpot(spotId, floor, row.charAt(0), spotNum, type);
                boolean success = spotController.saveParkingSpot(
                    newSpot,
                    LOT_ID,
                    (type == SpotType.RESERVED && !reservedPlate.isEmpty()) ? reservedPlate : null
                );

                if (success) {
                    String msg = "Spot " + spotId + " added successfully!";
                    if (type == SpotType.RESERVED && !reservedPlate.isEmpty()) {
                        msg += "\nReserved for: " + reservedPlate;
                    }
                    JOptionPane.showMessageDialog(dialog, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to add spot. Check console for errors.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        dialog.add(topPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void removeSelectedSpot() {
        int selectedRow = spotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a spot to remove",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String spotId = (String) spotsTableModel.getValueAt(selectedRow, 0);
        String status = (String) spotsTableModel.getValueAt(selectedRow, 3);

        // Check if spot is occupied
        if ("OCCUPIED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Cannot remove occupied spot! Please wait for vehicle to exit.",
                "Spot Occupied",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove spot " + spotId + "?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = spotController.deleteParkingSpot(spotId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Spot " + spotId + " removed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to remove spot. Check console for errors.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelectedSpot() {
        int selectedRow = spotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a spot to view",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String spotId = (String) spotsTableModel.getValueAt(selectedRow, 0);
        String floor = spotsTableModel.getValueAt(selectedRow, 1).toString();
        String type = (String) spotsTableModel.getValueAt(selectedRow, 2);
        String status = (String) spotsTableModel.getValueAt(selectedRow, 3);
        String rate = (String) spotsTableModel.getValueAt(selectedRow, 4);
        String vehicle = (String) spotsTableModel.getValueAt(selectedRow, 5);

        String details = String.format(
            "Spot ID: %s\nFloor: %s\nType: %s\nStatus: %s\nHourly Rate: %s\nCurrent Vehicle: %s",
            spotId, floor, type, status, rate, vehicle
        );

        JOptionPane.showMessageDialog(this, details, "Spot Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadAllData() {
        loadSpotsData();
        loadVehiclesData();
        updateStatistics();
    }

    private void loadSpotsData() {
        spotsTableModel.setRowCount(0);

        List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
        for (Map<String, Object> spot : spots) {
            String spotId = (String) spot.get("spot_id");
            int floor = (Integer) spot.get("floor_number");
            String type = (String) spot.get("spot_type");
            int status = (Integer) spot.get("status");
            String statusStr = (status == 0) ? "AVAILABLE" : "OCCUPIED";
            String vehicle = (String) spot.get("current_vehicle_plate");
            if (vehicle == null) vehicle = "-";

            // Get rate based on type
            String rate = getRateForType(type);

            spotsTableModel.addRow(new Object[]{
                spotId, floor, type, statusStr, rate, vehicle
            });
        }
    }

    private void loadVehiclesData() {
        vehiclesTableModel.setRowCount(0);

        try {
            List<Map<String, Object>> vehicles = historyController.getCurrentlyParkedVehicles();
            for (Map<String, Object> vehicle : vehicles) {
                String plate = (String) vehicle.get("plate_number");
                String type = (String) vehicle.get("vehicle_type");
                String spotId = (String) vehicle.get("spot_id");
                String entryTime = (String) vehicle.get("entry_time");
                String duration = calculateDuration(entryTime);

                // Get unpaid fines
                double totalFines = fineController.getTotalUnpaidFines(plate);
                String finesStr = totalFines > 0 ? String.format("RM %.2f", totalFines) : "-";

                vehiclesTableModel.addRow(new Object[]{
                    plate, type, spotId, entryTime, duration, finesStr
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            // Calculate occupancy
            List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
            int totalSpots = spots.size();
            long occupiedSpots = spots.stream()
                .filter(s -> (Integer) s.get("status") == 1)
                .count();
            double occupancyRate = totalSpots > 0 ? (occupiedSpots * 100.0 / totalSpots) : 0;
            occupancyLabel.setText(String.format("%.1f%%", occupancyRate));

            // Calculate revenue
            double totalRevenue = historyController.getTotalRevenue();
            revenueLabel.setText(String.format("RM %.2f", totalRevenue));

            // Calculate unpaid fines
            double totalUnpaidFines = fineController.getAllFines().stream()
                .filter(f -> !f.isPaid())
                .mapToDouble(f -> f.getAmount())
                .sum();
            totalFinesLabel.setText(String.format("RM %.2f", totalUnpaidFines));

        } catch (Exception e) {
            System.err.println("Error updating statistics: " + e.getMessage());
        }
    }

    private String getRateForType(String type) {
        switch (type) {
            case "COMPACT": return "RM 2.00/hr";
            case "REGULAR": return "RM 5.00/hr";
            case "HANDICAP": return "RM 2.00/hr";
            case "RESERVED": return "RM 10.00/hr";
            default: return "RM 5.00/hr";
        }
    }

    private String calculateDuration(String entryTimeStr) {
        try {
            java.time.LocalDateTime entryTime = java.time.LocalDateTime.parse(entryTimeStr);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            long hours = java.time.temporal.ChronoUnit.HOURS.between(entryTime, now);
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(entryTime, now) % 60;
            return String.format("%dh %dm", hours, minutes);
        } catch (Exception e) {
            return "N/A";
        }
    }
}
