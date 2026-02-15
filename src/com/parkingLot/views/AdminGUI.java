package com.parkingLot.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.parkingLot.controllers.FloorController;
import com.parkingLot.controllers.ParkingSpotController;
import com.parkingLot.controllers.VehicleController;
import com.parkingLot.models.ParkingSpot;
import com.parkingLot.models.spots.SpotType;
import com.parkingLot.utils.IdGenerator;

public class AdminGUI extends JFrame {
    private static final String LOT_ID = "LOT-001";
    
    // Controllers
    private FloorController floorController;
    private ParkingSpotController spotController;
    private VehicleController vehicleController;
    
    // UI Components
    private JTextArea outputArea;
    private JTable spotsTable;
    private DefaultTableModel spotsTableModel;
    private JTable floorsTable;
    private DefaultTableModel floorsTableModel;
    private JTable vehiclesTable;
    private DefaultTableModel vehiclesTableModel;
    private JComboBox<SpotType> spotTypeCombo;
    
    public AdminGUI() {

        floorController = new FloorController();
        spotController = new ParkingSpotController();
        vehicleController = new VehicleController();
        
        setTitle("Admin Panel - Parking Lot Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        
        refreshAllData();
    }

    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel spotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        spotPanel.setBorder(BorderFactory.createTitledBorder("Parking Spot Management"));
        
        spotPanel.add(new JLabel("Floor:"));
        JTextField floorField = new JTextField(3);
        spotPanel.add(floorField);
        
        spotPanel.add(new JLabel("Spot ID:"));
        JTextField spotIdField = new JTextField(10);
        spotPanel.add(spotIdField);
        
        spotPanel.add(new JLabel("Row:"));
        JTextField rowField = new JTextField(3);
        spotPanel.add(rowField);
        
        spotPanel.add(new JLabel("Spot Number:"));
        JTextField spotNumField = new JTextField(3);
        spotPanel.add(spotNumField);
        
        spotPanel.add(new JLabel("Type:"));
        spotTypeCombo = new JComboBox<>(SpotType.values());
        spotPanel.add(spotTypeCombo);
        
        JButton addSpotButton = new JButton("Add Spot");
        addSpotButton.addActionListener(e -> {
            String floorStr = floorField.getText().trim();
            String spotId = spotIdField.getText().trim();
            String rowStr = rowField.getText().trim();
            String spotNumStr = spotNumField.getText().trim();
            
            if (!floorStr.isEmpty() && !spotId.isEmpty() && !rowStr.isEmpty() && !spotNumStr.isEmpty()) {
                try {
                    int floorNum = Integer.parseInt(floorStr);
                    int row = Integer.parseInt(rowStr);
                    int spotNum = Integer.parseInt(spotNumStr);
                    SpotType type = (SpotType) spotTypeCombo.getSelectedItem();
                    
                    addParkingSpot(floorNum, spotId, row, spotNum, type);
                    spotIdField.setText("");
                    rowField.setText("");
                    spotNumField.setText("");
                } catch (NumberFormatException ex) {
                    showMessage("ERROR: Invalid input values!");
                }
            } else {
                showMessage("ERROR: All fields are required!");
            }
        });
        spotPanel.add(addSpotButton);
        
        JButton removeSpotButton = new JButton("Remove Selected Spot");
        removeSpotButton.addActionListener(e -> removeSelectedSpot());
        spotPanel.add(removeSpotButton);
        
        JButton removeFloorButton = new JButton("Remove Floor");
        removeFloorButton.addActionListener(e -> removeSelectedFloor());
        spotPanel.add(removeFloorButton);
        
        panel.add(spotPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        
        String[] floorColumns = {"Floor", "Total Spots", "Occupied", "Available"};
        floorsTableModel = new DefaultTableModel(floorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        floorsTable = new JTable(floorsTableModel);
        JScrollPane floorsScroll = new JScrollPane(floorsTable);
        floorsScroll.setBorder(BorderFactory.createTitledBorder("Floors Summary"));
        panel.add(floorsScroll);
        
        String[] spotColumns = {"Spot ID", "Floor", "Row", "Spot #", "Type", "Status"};
        spotsTableModel = new DefaultTableModel(spotColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        spotsTable = new JTable(spotsTableModel);
        JScrollPane spotsScroll = new JScrollPane(spotsTable);
        spotsScroll.setBorder(BorderFactory.createTitledBorder("Parking Spots"));
        panel.add(spotsScroll);
        
        String[] vehicleColumns = {"License Plate", "Vehicle Type", "VIP Status"};
        vehiclesTableModel = new DefaultTableModel(vehicleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehiclesTable = new JTable(vehiclesTableModel);
        JScrollPane vehiclesScroll = new JScrollPane(vehiclesTable);
        vehiclesScroll.setBorder(BorderFactory.createTitledBorder("Registered Vehicles"));
        panel.add(vehiclesScroll);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        outputArea = new JTextArea(8, 80);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("System Messages"));
        panel.add(outputScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton refreshButton = new JButton("Refresh All Data");
        refreshButton.addActionListener(e -> refreshAllData());
        buttonPanel.add(refreshButton);
        
        JButton toggleVipButton = new JButton("Toggle VIP Status");
        toggleVipButton.addActionListener(e -> toggleVipStatus());
        buttonPanel.add(toggleVipButton);
        
        JButton clearButton = new JButton("Clear Messages");
        clearButton.addActionListener(e -> outputArea.setText(""));
        buttonPanel.add(clearButton);
        
        JButton initButton = new JButton("Initialize Sample Data");
        initButton.addActionListener(e -> initializeSampleData());
        buttonPanel.add(initButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    
    private void addParkingSpot(int floorNumber, String spotId, int row, int spotNum, SpotType type) {
        if (spotController.spotExists(spotId)) {
            showMessage("ERROR: Spot ID " + spotId + " already exists!");
            return;
        }
        
        ParkingSpot spot = new ParkingSpot(spotId, floorNumber, row, spotNum, type);
        
        if (spotController.saveParkingSpot(spot, LOT_ID)) {
            showMessage("SUCCESS: Parking spot " + spotId + " added to Floor " + floorNumber);
            refreshAllData();
        } else {
            showMessage("ERROR: Failed to add parking spot!");
        }
    }
    
    private void removeSelectedSpot() {
        int selectedRow = spotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a spot to remove!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String spotId = (String) spotsTableModel.getValueAt(selectedRow, 0);
        String status = (String) spotsTableModel.getValueAt(selectedRow, 5);
        
        if (status.equals("OCCUPIED")) {
            JOptionPane.showMessageDialog(this, "Cannot remove occupied spot " + spotId, "Spot Occupied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove spot " + spotId + "?", 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (spotController.deleteParkingSpot(spotId)) {
                showMessage("SUCCESS: Parking spot " + spotId + " removed");
                refreshAllData();
            } else {
                showMessage("ERROR: Failed to remove parking spot!");
            }
        }
    }
    
    private void removeSelectedFloor() {
        int selectedRow = floorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a floor to remove!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int floorNumber = (int) floorsTableModel.getValueAt(selectedRow, 0);
        int occupiedSpots = (int) floorsTableModel.getValueAt(selectedRow, 2);
        
        if (occupiedSpots > 0) {
            JOptionPane.showMessageDialog(this, 
                "Cannot remove floor " + floorNumber + " - it has " + occupiedSpots + " occupied spots!", 
                "Floor Occupied", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove all spots on floor " + floorNumber + "?", 
            "Confirm Floor Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (floorController.deleteFloorSpots(floorNumber)) {
                showMessage("SUCCESS: Floor " + floorNumber + " removed");
                refreshAllData();
            } else {
                showMessage("ERROR: Failed to remove floor!");
            }
        }
    }
    
    private void toggleVipStatus() {
        int selectedRow = vehiclesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licensePlate = (String) vehiclesTableModel.getValueAt(selectedRow, 0);
        String currentVipStatus = (String) vehiclesTableModel.getValueAt(selectedRow, 2);
        
        int newVipStatus = currentVipStatus.equals("Yes") ? 0 : 1;
        String newStatusStr = (newVipStatus == 1) ? "VIP" : "Regular";
        
        if (vehicleController.updateVipStatus(licensePlate, newVipStatus)) {
            showMessage("SUCCESS: Vehicle " + licensePlate + " updated to " + newStatusStr);
            refreshAllData();
        } else {
            showMessage("ERROR: Failed to update VIP status!");
        }
    }
    
    private void refreshAllData() {
        refreshFloorsTable();
        refreshSpotsTable();
        refreshVehiclesTable();
    }
    
    private void refreshFloorsTable() {
        floorsTableModel.setRowCount(0);
        
        List<Map<String, Object>> floorStats = floorController.getFloorStatistics();
        for (Map<String, Object> floor : floorStats) {
            floorsTableModel.addRow(new Object[]{
                floor.get("floor_number"),
                floor.get("total_spots"),
                floor.get("occupied_spots"),
                floor.get("available_spots")
            });
        }
    }
    
    private void refreshSpotsTable() {
        spotsTableModel.setRowCount(0);
        
        List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
        for (Map<String, Object> spot : spots) {
            int status = (int) spot.get("status");
            String statusStr = (status == 0) ? "AVAILABLE" : "OCCUPIED";
            
            spotsTableModel.addRow(new Object[]{
                spot.get("spot_id"),
                spot.get("floor_number"),
                spot.get("row_number"),
                spot.get("spot_number"),
                spot.get("spot_type"),
                statusStr
            });
        }
    }
    
    private void refreshVehiclesTable() {
        vehiclesTableModel.setRowCount(0);
        
        List<Map<String, Object>> vehicles = vehicleController.getAllVehiclesForDisplay();
        for (Map<String, Object> vehicle : vehicles) {
            int isVip = (int) vehicle.get("isVip");
            String vipStatus = (isVip == 1) ? "Yes" : "No";
            
            vehiclesTableModel.addRow(new Object[]{
                vehicle.get("plate_number"),
                vehicle.get("vehicle_type"),
                vipStatus
            });
        }
    }

    
    private void initializeSampleData() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "This will add sample floors and parking spots. Continue?", 
            "Initialize Sample Data", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        for (int i = 1; i <= 5; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(1, 1, i), 1, 1, i, SpotType.COMPACT);
            spotController.saveParkingSpot(spot, LOT_ID);
        }
        for (int i = 1; i <= 5; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(1, 2, i), 1, 2, i, SpotType.REGULAR);
            spotController.saveParkingSpot(spot, LOT_ID);
        }
        for (int i = 1; i <= 2; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(1, 3, i), 1, 3, i, SpotType.HANDICAP);
            spotController.saveParkingSpot(spot, LOT_ID);
        }

        for (int i = 1; i <= 5; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(2, 1, i), 2, 1, i, SpotType.COMPACT);
            spotController.saveParkingSpot(spot, LOT_ID);
        }
        for (int i = 1; i <= 5; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(2, 2, i), 2, 2, i, SpotType.REGULAR);
            spotController.saveParkingSpot(spot, LOT_ID);
        }
        for (int i = 1; i <= 2; i++) {
            ParkingSpot spot = new ParkingSpot(IdGenerator.generateSpotId(2, 3, i), 2, 3, i, SpotType.RESERVED);
            spotController.saveParkingSpot(spot, LOT_ID);
        }
        
        showMessage("SUCCESS: Sample data initialized");
        showMessage("Added 2 floors with multiple parking spots");
        refreshAllData();
    }
    
    private void showMessage(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI gui = new AdminGUI();
            gui.setVisible(true);
        });
    }
}
