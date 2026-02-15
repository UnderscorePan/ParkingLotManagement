package com.parkingLot.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.parkingLot.controllers.ParkingHistoryController;
import com.parkingLot.controllers.ParkingSpotController;
import com.parkingLot.database.databaseConnection;
import com.parkingLot.models.Floor;
import com.parkingLot.models.ParkingLot;
import com.parkingLot.models.ParkingSpot;
import com.parkingLot.models.Ticket;
import com.parkingLot.models.spots.SpotType;
import com.parkingLot.models.vehicles.Car;
import com.parkingLot.models.vehicles.HandicapVehicle;
import com.parkingLot.models.vehicles.Motorcycle;
import com.parkingLot.models.vehicles.SUV;
import com.parkingLot.models.vehicles.Vehicle;
import com.parkingLot.models.vehicles.VehicleType;

public class VehicleGUI extends JFrame {
    private ParkingLot parkingLot;
    private Map<String, Ticket> activeTickets;
    private Map<String, Vehicle> parkedVehicles;
    
    // UI Components for Registration
    private JTable registeredVehiclesTable;
    private DefaultTableModel registeredVehiclesModel;
    private JTextField searchField;
    private JButton registerButton;
    private JButton enterParkingButton;
    
    // UI Components for Active Parking
    private JTextArea outputArea;
    private JTable activeTicketsTable;
    private DefaultTableModel activeTicketsModel;
    
    public VehicleGUI() {
        // Initialize data structures
        parkingLot = new ParkingLot("LOT-001");
        activeTickets = new HashMap<>();
        parkedVehicles = new HashMap<>();
        
        // Initialize parking lot with some spots
        initializeParkingLot();
        
        // Setup GUI
        setTitle("Vehicle Registration & Parking System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create panels
        add(createRegistrationPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        
        // Load registered vehicles and active parking sessions
        loadRegisteredVehicles();
        loadActiveParkingSessions();
        
        setLocationRelativeTo(null);
    }
    
    private void initializeParkingLot() {
        Floor floor1 = new Floor(1);
        
        // Add different types of parking spots
        for (int i = 1; i <= 5; i++) {
            floor1.addParkingSpot(new ParkingSpot("F1-COMPACT-" + i, 1, 1, i, SpotType.COMPACT));
        }
        for (int i = 1; i <= 5; i++) {
            floor1.addParkingSpot(new ParkingSpot("F1-REGULAR-" + i, 1, 2, i, SpotType.REGULAR));
        }
        for (int i = 1; i <= 2; i++) {
            floor1.addParkingSpot(new ParkingSpot("F1-HANDICAP-" + i, 1, 3, i, SpotType.HANDICAP));
        }
        
        parkingLot.addFloor(floor1);
    }

    
    private JPanel createRegistrationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Management"));
        
        // Create split panel for Entry (left) and Exit (right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // LEFT SIDE - Entry Panel
        JPanel entryPanel = new JPanel(new BorderLayout(5, 5));
        entryPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Registration & Entry"));
        
        // Top panel with buttons and search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        registerButton = new JButton("Register Vehicle");
        registerButton.addActionListener(e -> openRegistrationDialog());
        topPanel.add(registerButton);
        
        topPanel.add(new JLabel("Search License Plate:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });
        topPanel.add(searchField);
        
        entryPanel.add(topPanel, BorderLayout.NORTH);
        
        // Table for registered vehicles
        String[] columns = {"License Plate", "Vehicle Type", "VIP Status"};
        registeredVehiclesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registeredVehiclesTable = new JTable(registeredVehiclesModel);
        JScrollPane tableScroll = new JScrollPane(registeredVehiclesTable);
        entryPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Bottom panel with Enter Parking button
        JPanel entryBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        enterParkingButton = new JButton("Enter Parking");
        enterParkingButton.addActionListener(e -> handleEnterParking());
        entryBottomPanel.add(enterParkingButton);
        
        entryPanel.add(entryBottomPanel, BorderLayout.SOUTH);
        
        // RIGHT SIDE - Exit Panel
        JPanel exitPanel = new JPanel(new BorderLayout(5, 5));
        exitPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Exit"));
        
        // Table for active tickets
        String[] exitColumns = {"Ticket ID", "License Plate", "Spot ID", "Entry Time", "Vehicle Type"};
        activeTicketsModel = new DefaultTableModel(exitColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activeTicketsTable = new JTable(activeTicketsModel);
        JScrollPane exitTableScroll = new JScrollPane(activeTicketsTable);
        exitPanel.add(exitTableScroll, BorderLayout.CENTER);
        
        // Bottom panel with Exit Parking button
        JPanel exitBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton exitParkingButton = new JButton("Exit Parking");
        exitParkingButton.addActionListener(e -> handleVehicleExitFromTable());
        exitBottomPanel.add(exitParkingButton);
        
        exitPanel.add(exitBottomPanel, BorderLayout.SOUTH);
        
        // Add both panels to split pane
        splitPane.setLeftComponent(entryPanel);
        splitPane.setRightComponent(exitPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void openRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register New Vehicle", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // License Plate
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("License Plate:"), gbc);
        
        gbc.gridx = 1;
        JTextField licensePlateField = new JTextField(15);
        dialog.add(licensePlateField, gbc);
        
        // Vehicle Type
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Vehicle Type:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<VehicleType> vehicleTypeCombo = new JComboBox<>(VehicleType.values());
        dialog.add(vehicleTypeCombo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String licensePlate = licensePlateField.getText().trim();
            VehicleType vehicleType = (VehicleType) vehicleTypeCombo.getSelectedItem();
            
            // Validation: No spaces allowed
            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "License Plate cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (licensePlate.contains(" ")) {
                JOptionPane.showMessageDialog(dialog, "License Plate cannot contain spaces!\nExample: ABC1234 (valid), ABC 1234 (invalid)", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Save to database
            if (saveVehicleToDatabase(licensePlate, vehicleType)) {
                JOptionPane.showMessageDialog(dialog, "Vehicle registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegisteredVehicles(); // Refresh table
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to register vehicle. It may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    
    private boolean saveVehicleToDatabase(String licensePlate, VehicleType vehicleType) {
        String sql = "INSERT INTO vehicles (plate_number, vehicle_type, isVip) VALUES (?, ?, 0)";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            pstmt.setString(2, vehicleType.name());
            
            pstmt.executeUpdate();
            showMessage("✅ Vehicle " + licensePlate + " registered successfully!");
            return true;
            
        } catch (SQLException e) {
            showMessage("❌ Error registering vehicle: " + e.getMessage());
            return false;
        }
    }
    
    private void loadRegisteredVehicles() {
        registeredVehiclesModel.setRowCount(0);
        
        String sql = "SELECT plate_number, vehicle_type, isVip FROM vehicles ORDER BY plate_number ASC";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String licensePlate = rs.getString("plate_number");
                String vehicleType = rs.getString("vehicle_type");
                int isVip = rs.getInt("isVip");
                
                registeredVehiclesModel.addRow(new Object[]{
                    licensePlate,
                    vehicleType,
                    isVip == 1 ? "Yes" : "No"
                });
            }
            
        } catch (SQLException e) {
            showMessage("❌ Error loading vehicles: " + e.getMessage());
        }
    }
    
    private void loadActiveParkingSessions() {
        ParkingHistoryController historyController = new ParkingHistoryController();
        java.util.List<java.util.Map<String, Object>> sessions = historyController.getAllActiveSessions();
        
        for (java.util.Map<String, Object> session : sessions) {
            String licensePlate = (String) session.get("plate_number");
            LocalDateTime entryTime = (LocalDateTime) session.get("entry_time");
            String spotId = (String) session.get("spot_id");
            String vehicleTypeStr = (String) session.get("vehicle_type");
            
            // Create vehicle and ticket objects
            VehicleType vehicleType = VehicleType.valueOf(vehicleTypeStr);
            Vehicle vehicle = createVehicle(licensePlate, vehicleType);
            vehicle.setEntryTime(entryTime);
            vehicle.setAssignedSpotId(spotId);
            
            Ticket ticket = new Ticket(spotId, licensePlate, spotId, entryTime);
            
            // Add to in-memory maps
            activeTickets.put(licensePlate, ticket);
            parkedVehicles.put(licensePlate, vehicle);
            
            // Add to table
            addTicketToActiveTable(ticket, vehicleType);
            
            // Mark spot as occupied in memory
            ParkingSpot spot = findSpotById(spotId);
            if (spot != null) {
                try {
                    spot.parkVehicle(vehicle);
                } catch (Exception e) {
                    // Spot already marked as occupied in DB
                }
            }
        }
        
        if (!sessions.isEmpty()) {
            showMessage("✅ Loaded " + sessions.size() + " active parking session(s) from database");
        }
    }
    
    private void filterTable() {
        String searchText = searchField.getText().trim().toUpperCase();
        
        if (searchText.isEmpty()) {
            loadRegisteredVehicles(); // Show all
            return;
        }
        
        registeredVehiclesModel.setRowCount(0);
        
        String sql = "SELECT plate_number, vehicle_type, isVip FROM vehicles WHERE UPPER(plate_number) LIKE ? ORDER BY plate_number ASC";
        
        try (Connection conn = databaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String licensePlate = rs.getString("plate_number");
                String vehicleType = rs.getString("vehicle_type");
                int isVip = rs.getInt("isVip");
                
                registeredVehiclesModel.addRow(new Object[]{
                    licensePlate,
                    vehicleType,
                    isVip == 1 ? "Yes" : "No"
                });
            }
            
        } catch (SQLException e) {
            showMessage("❌ Error searching vehicles: " + e.getMessage());
        }
    }
    
    private void handleEnterParking() {
        int selectedRow = registeredVehiclesTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licensePlate = (String) registeredVehiclesModel.getValueAt(selectedRow, 0);
        String vehicleTypeStr = (String) registeredVehiclesModel.getValueAt(selectedRow, 1);
        
        // Check if already parked (check database)
        ParkingHistoryController historyController = new ParkingHistoryController();
        if (historyController.isVehicleParked(licensePlate)) {
            JOptionPane.showMessageDialog(this, "Vehicle " + licensePlate + " is already parked!", "Already Parked", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        VehicleType vehicleType = VehicleType.valueOf(vehicleTypeStr);
        
        // Create vehicle
        Vehicle vehicle = createVehicle(licensePlate, vehicleType);
        
        // Find available spot
        ParkingSpot availableSpot = findAvailableSpot(vehicle);
        
        if (availableSpot == null) {
            JOptionPane.showMessageDialog(this, "No available parking spot for " + vehicleType, "No Spot Available", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Park vehicle
        try {
            availableSpot.parkVehicle(vehicle);
            vehicle.setEntryTime(LocalDateTime.now());
            vehicle.setAssignedSpotId(availableSpot.getSpotId());
            
            // Create ticket
            Ticket ticket = new Ticket(
                availableSpot.getSpotId(),
                licensePlate,
                availableSpot.getSpotId(),
                vehicle.getEntryTime()
            );
            
            activeTickets.put(licensePlate, ticket);
            parkedVehicles.put(licensePlate, vehicle);
            
            // Update database - spot status
            ParkingSpotController spotController = new ParkingSpotController();
            spotController.updateSpotStatus(availableSpot.getSpotId(), 1, licensePlate);
            
            // Create parking history entry
            historyController.createParkingEntry(licensePlate, vehicle.getEntryTime(), availableSpot.getSpotId());
            
            // Update UI
            addTicketToActiveTable(ticket, vehicleType);
            showMessage("✅ Vehicle " + licensePlate + " parked at " + availableSpot.getSpotId());
            showMessage("Ticket ID: " + ticket.getTicketId());
            
            updateStatus();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Parking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Output area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("System Messages"));
        panel.add(outputScroll, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh Status");
        refreshButton.addActionListener(e -> updateStatus());
        statusPanel.add(refreshButton);
        
        JButton clearButton = new JButton("Clear Messages");
        clearButton.addActionListener(e -> outputArea.setText(""));
        statusPanel.add(clearButton);
        
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void handleVehicleExitFromTable() {
        int selectedRow = activeTicketsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the Active Tickets table", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licensePlate = (String) activeTicketsModel.getValueAt(selectedRow, 1); // Column 1 is License Plate
        
        // Call the existing exit handler
        handleVehicleExit(licensePlate);
    }
    
    private void handleVehicleExit(String licensePlate) {
        licensePlate = licensePlate.trim();
        
        if (licensePlate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a license plate!", "Input Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check database for active parking session
        ParkingHistoryController historyController = new ParkingHistoryController();
        java.util.Map<String, Object> session = historyController.getActiveParkingSession(licensePlate);
        
        if (session == null) {
            JOptionPane.showMessageDialog(this, "Vehicle " + licensePlate + " is not in the parking lot!", "Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get session details
        LocalDateTime entryTime = (LocalDateTime) session.get("entry_time");
        String spotId = (String) session.get("spot_id");
        
        // Find and free the spot
        ParkingSpot spot = findSpotById(spotId);
        if (spot != null) {
            spot.removeVehicle();
        }
        
        // Calculate parking duration and fee
        LocalDateTime exitTime = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
        if (hours == 0) hours = 1; // Minimum 1 hour
        
        double rate = spot != null ? spot.getEffectiveRate(parkedVehicles.get(licensePlate)) : 5.0;
        double fee = hours * rate;
        
        // Update database - complete parking history
        historyController.completeParkingExit(licensePlate, exitTime, fee);
        
        // Update database - free the spot
        if (spot != null) {
            ParkingSpotController spotController = new ParkingSpotController();
            spotController.updateSpotStatus(spot.getSpotId(), 0, null);
        }
        
        // Remove from active tickets
        activeTickets.remove(licensePlate);
        parkedVehicles.remove(licensePlate);
        
        // Update UI
        removeTicketFromActiveTable(licensePlate);
        showMessage("✅ Vehicle " + licensePlate + " exited");
        showMessage("Parking Duration: " + hours + " hour(s)");
        showMessage("Parking Fee: $" + String.format("%.2f", fee));
        showMessage("Spot " + spotId + " is now available");
        updateStatus();
    }
    
    private Vehicle createVehicle(String licensePlate, VehicleType type) {
        return switch (type) {
            case MOTORCYCLE -> new Motorcycle(licensePlate);
            case CAR -> new Car(licensePlate);
            case SUV -> new SUV(licensePlate);
            case HANDICAP_VEHICLE -> new HandicapVehicle(licensePlate);
            default -> new Car(licensePlate);
        };
    }
    
    private ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : parkingLot.getAvailableSpots()) {
            if (spot.canAccommodate(vehicle)) {
                return spot;
            }
        }
        return null;
    }
    
    private ParkingSpot findSpotById(String spotId) {
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getParkingSpots()) {
                if (spot.getSpotId().equals(spotId)) {
                    return spot;
                }
            }
        }
        return null;
    }
    
    private void addTicketToActiveTable(Ticket ticket, VehicleType vehicleType) {
        activeTicketsModel.addRow(new Object[]{
            ticket.getTicketId(),
            ticket.getLicensePlate(),
            ticket.getSpotId(),
            ticket.getEntryTime().toString(),
            vehicleType
        });
    }
    
    private void removeTicketFromActiveTable(String licensePlate) {
        for (int i = 0; i < activeTicketsModel.getRowCount(); i++) {
            if (activeTicketsModel.getValueAt(i, 1).equals(licensePlate)) {
                activeTicketsModel.removeRow(i);
                break;
            }
        }
    }
    
    private void updateStatus() {
        int total = parkingLot.getTotalSpots();
        int occupied = parkingLot.getOccupiedSpots();
        int available = total - occupied;
        double occupancy = parkingLot.getOccupancyRate();
        
        showMessage("=== Parking Lot Status ===");
        showMessage("Total Spots: " + total);
        showMessage("Occupied: " + occupied);
        showMessage("Available: " + available);
        showMessage("Occupancy Rate: " + String.format("%.2f%%", occupancy));
    }
    
    private void showMessage(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VehicleGUI gui = new VehicleGUI();
            gui.setVisible(true);
        });
    }
}
