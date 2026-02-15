package com.parkingLot.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.parkingLot.controllers.*;
import com.parkingLot.models.ParkingSpot;
import com.parkingLot.models.vehicles.*;
import com.parkingLot.models.spots.SpotType;


public class EntryExitPanel extends JPanel {
    
    private static final String LOT_ID = "LOT-001";

    // Controllers
    private ParkingSpotController spotController;
    private VehicleController vehicleController;
    private ParkingHistoryController historyController;
    private FineController fineController;

    private JTabbedPane operationTabs;
    private VehicleEntryPanel entryPanel;
    private VehicleExitPanel exitPanel;
    
    public EntryExitPanel() {
        setLayout(new BorderLayout());

        // Initialize controllers
        spotController = new ParkingSpotController();
        vehicleController = new VehicleController();
        historyController = new ParkingHistoryController();
        fineController = FineController.getInstance(); // Use singleton

        initializeComponents();
    }
    
    private void initializeComponents() {
        operationTabs = new JTabbedPane();
        
        entryPanel = new VehicleEntryPanel();
        exitPanel = new VehicleExitPanel();
        
        operationTabs.addTab("Vehicle Entry", entryPanel);
        operationTabs.addTab("Vehicle Exit", exitPanel);
        
        add(operationTabs, BorderLayout.CENTER);
    }
    
    class VehicleEntryPanel extends JPanel {
        private JTextField licensePlateField;
        private JComboBox<String> vehicleTypeCombo;
        private JCheckBox handicappedCheckBox;
        private JTable availableSpotsTable;
        private DefaultTableModel spotsTableModel;
        private JTextArea ticketArea;
        private JButton searchSpotsButton;
        private JButton parkVehicleButton;
        
        public VehicleEntryPanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Left panel - Vehicle Information
            JPanel leftPanel = createVehicleInfoPanel();
            
            // Right panel - Available Spots and Ticket
            JPanel rightPanel = createSpotsAndTicketPanel();
            
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            splitPane.setResizeWeight(0.4);
            
            add(splitPane, BorderLayout.CENTER);
        }
        
        private JPanel createVehicleInfoPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(createSectionBorder("Vehicle Information"));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 8, 8, 8);
            
            // License Plate
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(createLabel("License Plate:"), gbc);
            
            gbc.gridx = 1;
            licensePlateField = new JTextField(15);
            licensePlateField.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(licensePlateField, gbc);
            
            // Vehicle Type
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(createLabel("Vehicle Type:"), gbc);
            
            gbc.gridx = 1;
            vehicleTypeCombo = new JComboBox<>(new String[]{
                "Motorcycle",
                "Car",
                "SUV/Truck",
                "Handicapped Vehicle"
            });
            vehicleTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(vehicleTypeCombo, gbc);
            
            // Handicapped Card
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            handicappedCheckBox = new JCheckBox("Handicapped Card Holder");
            handicappedCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));
            formPanel.add(handicappedCheckBox, gbc);
            
            // Search button
            gbc.gridy = 3;
            searchSpotsButton = createStyledButton("Search Available Spots", new Color(52, 152, 219));
            searchSpotsButton.addActionListener(e -> searchAvailableSpots());
            formPanel.add(searchSpotsButton, gbc);
            
            // Instructions
            JPanel instructionsPanel = new JPanel(new BorderLayout());
            instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            
            JTextArea instructionsArea = new JTextArea();
            instructionsArea.setEditable(false);
            instructionsArea.setBackground(new Color(241, 248, 233));
            instructionsArea.setFont(new Font("Arial", Font.PLAIN, 12));
            instructionsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            instructionsArea.setText(
                "PARKING INSTRUCTIONS:\n\n" +
                "1. Enter vehicle license plate number\n" +
                "2. Select vehicle type\n" +
                "3. Check handicapped card if applicable\n" +
                "4. Click 'Search Available Spots'\n" +
                "5. Select a suitable spot from the list\n" +
                "6. Click 'Park Vehicle' to confirm\n" +
                "7. Print/save your parking ticket\n\n" +
                "SPOT ELIGIBILITY:\n" +
                "• Motorcycle → Compact only\n" +
                "• Car → Compact or Regular\n" +
                "• SUV/Truck → Regular only\n" +
                "• Handicapped → Any spot (RM 2/hr)\n\n" +
                "RATES:\n" +
                "• Compact: RM 2/hour\n" +
                "• Regular: RM 5/hour\n" +
                "• Handicapped: RM 2/hour (FREE for card)\n" +
                "• Reserved: RM 10/hour"
            );
            
            instructionsPanel.add(instructionsArea, BorderLayout.CENTER);
            
            panel.add(formPanel, BorderLayout.NORTH);
            panel.add(instructionsPanel, BorderLayout.CENTER);
            
            return panel;
        }
        
        private JPanel createSpotsAndTicketPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            
            // Available spots table
            JPanel spotsPanel = new JPanel(new BorderLayout());
            spotsPanel.setBorder(createSectionBorder("Available Parking Spots"));
            
            String[] columns = {"Spot ID", "Floor", "Type", "Hourly Rate", "Distance"};
            spotsTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            availableSpotsTable = new JTable(spotsTableModel);
            availableSpotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            availableSpotsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            availableSpotsTable.setRowHeight(25);
            
            JScrollPane spotsScrollPane = new JScrollPane(availableSpotsTable);
            spotsPanel.add(spotsScrollPane, BorderLayout.CENTER);
            
            // Park vehicle button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            parkVehicleButton = createStyledButton("Park Vehicle at Selected Spot", new Color(46, 204, 113));
            parkVehicleButton.setEnabled(false);
            parkVehicleButton.addActionListener(e -> parkVehicle());
            buttonPanel.add(parkVehicleButton);
            spotsPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Ticket display
            JPanel ticketPanel = new JPanel(new BorderLayout());
            ticketPanel.setBorder(createSectionBorder("Parking Ticket"));
            
            ticketArea = new JTextArea();
            ticketArea.setEditable(false);
            ticketArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            ticketArea.setBackground(Color.WHITE);
            
            JScrollPane ticketScrollPane = new JScrollPane(ticketArea);
            ticketPanel.add(ticketScrollPane, BorderLayout.CENTER);
            
            // Print button
            JPanel ticketButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton printButton = createStyledButton("Print Ticket", new Color(155, 89, 182));
            printButton.addActionListener(e -> printTicket());
            ticketButtonPanel.add(printButton);
            ticketPanel.add(ticketButtonPanel, BorderLayout.SOUTH);
            
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spotsPanel, ticketPanel);
            splitPane.setResizeWeight(0.6);
            panel.add(splitPane, BorderLayout.CENTER);
            
            return panel;
        }
        
        private void searchAvailableSpots() {
            String licensePlate = licensePlateField.getText().trim();
            String vehicleType = (String) vehicleTypeCombo.getSelectedItem();
            boolean hasHandicappedCard = handicappedCheckBox.isSelected();

            if (!validateSearchInput(licensePlate)) {
                return;
            }

            // Clear and search for compatible spots
            spotsTableModel.setRowCount(0);
            SpotType[] compatibleSpotTypes = getCompatibleSpotTypes(vehicleType, hasHandicappedCard);

            int availableCount = loadCompatibleSpots(licensePlate, vehicleType, hasHandicappedCard, compatibleSpotTypes);

            parkVehicleButton.setEnabled(availableCount > 0);
            showSearchResult(availableCount, vehicleType, hasHandicappedCard);
        }

        private boolean validateSearchInput(String licensePlate) {
            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter license plate number!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (historyController.isVehicleParked(licensePlate)) {
                JOptionPane.showMessageDialog(this,
                    "Vehicle " + licensePlate + " is already parked!",
                    "Already Parked",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        }

        private int loadCompatibleSpots(String licensePlate, String vehicleType,
                                         boolean hasHandicappedCard, SpotType[] compatibleSpotTypes) {
            List<Map<String, Object>> allSpots = spotController.getAllSpotsForDisplay();
            int availableCount = 0;

            for (Map<String, Object> spot : allSpots) {
                if (isSpotAvailable(spot)) {
                    // Check if this spot is specifically reserved for this vehicle
                    boolean isMyReservedSpot = isSpotReservedForVehicle(spot, licensePlate);

                    // Show spot if: it's compatible OR it's reserved for this vehicle
                    if (isMyReservedSpot || isSpotCompatible(spot, compatibleSpotTypes)) {
                        if (canVehicleUseSpot(spot, licensePlate)) {
                            addSpotToTable(spot, licensePlate, vehicleType, hasHandicappedCard);
                            availableCount++;
                        }
                    }
                }
            }

            return availableCount;
        }

        private boolean isSpotReservedForVehicle(Map<String, Object> spot, String licensePlate) {
            String spotType = (String) spot.get("spot_type");
            String reservedForPlate = (String) spot.get("reserved_for_plate");

            boolean isReserved = spotType.equals("RESERVED") &&
                   reservedForPlate != null &&
                   !reservedForPlate.isEmpty() &&
                   licensePlate.equalsIgnoreCase(reservedForPlate);

            if (isReserved) {
                System.out.println("✅ Found reserved spot for " + licensePlate + ": " + spot.get("spot_id"));
            }

            return isReserved;
        }

        private boolean isSpotAvailable(Map<String, Object> spot) {
            return (Integer) spot.get("status") == 0;
        }

        private boolean isSpotCompatible(Map<String, Object> spot, SpotType[] compatibleTypes) {
            String spotType = (String) spot.get("spot_type");
            for (SpotType compatibleType : compatibleTypes) {
                if (spotType.equals(compatibleType.toString())) {
                    return true;
                }
            }
            return false;
        }

        private boolean canVehicleUseSpot(Map<String, Object> spot, String licensePlate) {
            String spotType = (String) spot.get("spot_type");
            String reservedForPlate = (String) spot.get("reserved_for_plate");

            // If spot is RESERVED and has a reserved plate, only that vehicle can use it
            if (spotType.equals("RESERVED") && reservedForPlate != null && !reservedForPlate.isEmpty()) {
                return licensePlate.equalsIgnoreCase(reservedForPlate);
            }

            return true;
        }

        private void addSpotToTable(Map<String, Object> spot, String licensePlate,
                                     String vehicleType, boolean hasHandicappedCard) {
            String spotId = (String) spot.get("spot_id");
            int floor = (Integer) spot.get("floor_number");
            String spotType = (String) spot.get("spot_type");
            String reservedForPlate = (String) spot.get("reserved_for_plate");

            // Get rate with special handling for vehicle types
            String rate = calculateDisplayRate(spotType, vehicleType, hasHandicappedCard);

            // Add indicator if this is user's reserved spot
            if (isReservedForVehicle(reservedForPlate, licensePlate)) {
                rate = rate + " ⭐ YOUR SPOT";
            }

            spotsTableModel.addRow(new Object[]{spotId, floor, spotType, rate});
        }

        private boolean isReservedForVehicle(String reservedForPlate, String licensePlate) {
            return reservedForPlate != null && !reservedForPlate.isEmpty() &&
                   licensePlate.equalsIgnoreCase(reservedForPlate);
        }

        private String calculateDisplayRate(String spotType, String vehicleType, boolean hasHandicappedCard) {
            if (vehicleType.equals("Handicapped Vehicle")) {
                return getRateForHandicappedVehicle(spotType, hasHandicappedCard);
            }
            return getRateForType(spotType, hasHandicappedCard);
        }

        private void showSearchResult(int availableCount, String vehicleType, boolean hasHandicappedCard) {
            if (availableCount == 0) {
                String message = "No available spots for " + vehicleType + "!";
                if (hasHandicappedCard) {
                    message += "\n(Including handicapped spots)";
                }
                JOptionPane.showMessageDialog(this, message, "No Available Spots",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                String message = "Found " + availableCount + " available spot(s) for " + vehicleType;
                if (hasHandicappedCard) {
                    message += "\n(Including handicapped spots with card)";
                }
                JOptionPane.showMessageDialog(this, message, "Spots Found",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private SpotType[] getCompatibleSpotTypes(String vehicleType, boolean hasHandicappedCard) {
            SpotType[] baseTypes;

            switch (vehicleType) {
                case "Motorcycle":
                    // Motorcycle can park in COMPACT spots + any RESERVED spot assigned to them
                    baseTypes = new SpotType[]{SpotType.COMPACT, SpotType.RESERVED};
                    break;
                case "Car":
                    // Car can park in COMPACT, REGULAR + any RESERVED spot assigned to them
                    baseTypes = new SpotType[]{SpotType.COMPACT, SpotType.REGULAR, SpotType.RESERVED};
                    break;
                case "SUV/Truck":
                    // SUV/Truck can park in REGULAR + any RESERVED spot assigned to them
                    baseTypes = new SpotType[]{SpotType.REGULAR, SpotType.RESERVED};
                    break;
                case "Handicapped Vehicle":
                    // Handicapped vehicle can park in ANY spot type
                    baseTypes = new SpotType[]{SpotType.COMPACT, SpotType.REGULAR, SpotType.HANDICAP, SpotType.RESERVED};
                    break;
                default:
                    baseTypes = new SpotType[]{SpotType.REGULAR, SpotType.RESERVED};
                    break;
            }

            // If vehicle has handicapped card (but is not Handicapped Vehicle type),
            // add HANDICAP spots to compatible types
            if (hasHandicappedCard && !vehicleType.equals("Handicapped Vehicle")) {
                SpotType[] withHandicap = new SpotType[baseTypes.length + 1];
                System.arraycopy(baseTypes, 0, withHandicap, 0, baseTypes.length);
                withHandicap[baseTypes.length] = SpotType.HANDICAP;
                return withHandicap;
            }

            return baseTypes;
        }

        private String getRateForType(String type, boolean hasHandicappedCard) {
            switch (type) {
                case "COMPACT": return "RM 2.00/hr";
                case "REGULAR": return "RM 5.00/hr";
                case "HANDICAP":
                    // FREE only if handicapped card holder parks in handicapped spot
                    return hasHandicappedCard ? "FREE (Handicapped Card)" : "RM 2.00/hr";
                case "RESERVED": return "RM 10.00/hr";
                default: return "RM 5.00/hr";
            }
        }
        
        private String getRateForHandicappedVehicle(String spotType, boolean hasHandicappedCard) {
            // Handicapped Vehicle gets RM 2/hour everywhere
            // Except in HANDICAP spots with card = FREE
            if (spotType.equals("HANDICAP") && hasHandicappedCard) {
                return "FREE (Handicapped Card)";
            }
            return "RM 2.00/hr (Handicapped Vehicle Rate)";
        }

        private void parkVehicle() {
            int selectedRow = availableSpotsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a parking spot!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String spotId = (String) spotsTableModel.getValueAt(selectedRow, 0);
            String licensePlate = licensePlateField.getText().trim().toUpperCase();
            String vehicleTypeStr = (String) vehicleTypeCombo.getSelectedItem();
            boolean isHandicapped = handicappedCheckBox.isSelected();
            
            try {
                // Create vehicle object
                VehicleType vType = getVehicleType(vehicleTypeStr);
                Vehicle vehicle = createVehicle(licensePlate, vType, isHandicapped);
                vehicle.setEntryTime(LocalDateTime.now());

                // Save vehicle to database if new
                vehicleController.registerVehicle(licensePlate, vType.toString(), isHandicapped);

                // Update spot status to occupied
                boolean spotUpdated = spotController.updateSpotStatus(spotId, 1, licensePlate);
                if (!spotUpdated) {
                    throw new Exception("Failed to update spot status");
                }

                // Create parking history entry
                boolean entryCreated = historyController.createParkingEntry(
                    licensePlate,
                    vehicle.getEntryTime(),
                    spotId
                );
                if (!entryCreated) {
                    throw new Exception("Failed to create parking entry");
                }

                // Generate ticket
                String ticket = generateTicket(spotId, licensePlate, vehicleTypeStr, isHandicapped);
                ticketArea.setText(ticket);

                JOptionPane.showMessageDialog(this,
                    "Vehicle parked successfully!\n" +
                    "Spot: " + spotId + "\n" +
                    "License Plate: " + licensePlate + "\n\n" +
                    "Please save your parking ticket.",
                    "Parking Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                // Clear form
                licensePlateField.setText("");
                vehicleTypeCombo.setSelectedIndex(0);
                handicappedCheckBox.setSelected(false);
                spotsTableModel.setRowCount(0);
                parkVehicleButton.setEnabled(false);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error parking vehicle: " + e.getMessage(),
                    "Parking Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        private VehicleType getVehicleType(String vehicleTypeStr) {
            switch (vehicleTypeStr) {
                case "Motorcycle": return VehicleType.MOTORCYCLE;
                case "Car": return VehicleType.CAR;
                case "SUV/Truck": return VehicleType.SUV;
                case "Handicapped Vehicle": return VehicleType.HANDICAP_VEHICLE;
                default: return VehicleType.CAR;
            }
        }

        private Vehicle createVehicle(String licensePlate, VehicleType type, boolean hasHandicappedCard) {
            Vehicle vehicle;
            switch (type) {
                case MOTORCYCLE:
                    vehicle = new Motorcycle(licensePlate);
                    break;
                case SUV:
                    vehicle = new SUV(licensePlate);
                    break;
                case HANDICAP_VEHICLE:
                    vehicle = new HandicapVehicle(licensePlate);
                    break;
                case CAR:
                default:
                    vehicle = new Car(licensePlate);
                    break;
            }
            return vehicle;
        }
        
        private String generateTicket(String spotId, String licensePlate, String vehicleType, boolean isHandicapped) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String ticketId = "T-" + licensePlate + "-" + timestamp;
            
            StringBuilder ticket = new StringBuilder();
            ticket.append("UNIVERSITY PARKING LOT - PARKING TICKET\n");
            ticket.append("----------------------------------------\n");
            ticket.append(String.format(" Ticket ID: %-42s \n", ticketId));
            ticket.append("-----------------------------------------\n");
            ticket.append(String.format(" License Plate: %-38s \n", licensePlate));
            ticket.append(String.format(" Vehicle Type:  %-38s \n", vehicleType));
            ticket.append(String.format(" Parking Spot:  %-38s \n", spotId));
            ticket.append(String.format(" Entry Time:    %-38s \n", now.format(formatter)));
            if (isHandicapped) {
                ticket.append(" Handicapped:   YES (Card Holder)   \n");
            }
            ticket.append("-----------------------------\n");
            ticket.append("IMPORTANT:  \n");
            ticket.append(" - Keep this ticket safe \n");
            ticket.append(" - Present at exit for payment \n");
            ticket.append(" - Parking fee calculated hourly (rounded up) \n");
            ticket.append(" - Overstaying >24 hours will incur fines  \n");
            ticket.append("-------------------------------\n");
            
            return ticket.toString();
        }
        
        private void printTicket() {
            if (ticketArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No ticket to print!",
                    "Print Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this,
                "Ticket sent to printer!",
                "Print Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    class VehicleExitPanel extends JPanel {
        private JTextField licensePlateSearchField;
        private JTextArea vehicleInfoArea;
        private JTextArea billDetailsArea;
        private JComboBox<String> paymentMethodCombo;
        private JTextField cashAmountField;
        private JButton searchButton;
        private JButton processPaymentButton;
        private JLabel totalDueLabel;
        
        public VehicleExitPanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Top panel - Search and Test Mode
            JPanel topContainer = new JPanel(new BorderLayout());
            JPanel searchPanel = createSearchPanel();
            JPanel testModePanel = createTestModePanel();
            topContainer.add(searchPanel, BorderLayout.NORTH);
            topContainer.add(testModePanel, BorderLayout.SOUTH);

            // Center panel - Vehicle info and billing
            JPanel centerPanel = createCenterPanel();
            
            // Bottom panel - Payment
            JPanel paymentPanel = createPaymentPanel();
            
            add(topContainer, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
            add(paymentPanel, BorderLayout.SOUTH);
        }
        
        private JPanel createTestModePanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                "⚠️ TEST MODE - Simulate Parking Duration",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(255, 152, 0)
            ));
            panel.setBackground(new Color(255, 248, 225));

            JLabel infoLabel = new JLabel("Add parking hours:");
            infoLabel.setFont(new Font("Arial", Font.BOLD, 11));
            panel.add(infoLabel);

            JButton plus1Hour = createStyledButton("+1 hour", new Color(52, 152, 219));
            plus1Hour.setPreferredSize(new Dimension(85, 28));
            plus1Hour.addActionListener(e -> addParkingHours(1));
            panel.add(plus1Hour);

            JButton plus6Hours = createStyledButton("+6 hours", new Color(52, 152, 219));
            plus6Hours.setPreferredSize(new Dimension(85, 28));
            plus6Hours.addActionListener(e -> addParkingHours(6));
            panel.add(plus6Hours);

            JButton plus24Hours = createStyledButton("+24 hours", new Color(230, 126, 34));
            plus24Hours.setPreferredSize(new Dimension(95, 28));
            plus24Hours.addActionListener(e -> addParkingHours(24));
            panel.add(plus24Hours);

            JButton plus48Hours = createStyledButton("+48 hours", new Color(231, 76, 60));
            plus48Hours.setPreferredSize(new Dimension(95, 28));
            plus48Hours.addActionListener(e -> addParkingHours(48));
            panel.add(plus48Hours);

            JLabel warningLabel = new JLabel("← Click to add time to parking duration");
            warningLabel.setFont(new Font("Arial", Font.ITALIC, 10));
            warningLabel.setForeground(new Color(100, 100, 100));
            panel.add(warningLabel);

            return panel;
        }

        private void addParkingHours(int hoursToAdd) {
            String licensePlate = licensePlateSearchField.getText().trim().toUpperCase();

            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a license plate first!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!historyController.isVehicleParked(licensePlate)) {
                JOptionPane.showMessageDialog(this,
                    "Vehicle " + licensePlate + " is not currently parked!",
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Get current parking session
                Map<String, Object> session = historyController.getActiveParkingSession(licensePlate);
                if (session == null) {
                    throw new Exception("Could not retrieve parking session");
                }

                LocalDateTime currentEntryTime = (LocalDateTime) session.get("entry_time");
                LocalDateTime now = LocalDateTime.now();

                // Calculate CURRENT parking duration
                long currentDurationHours = ChronoUnit.HOURS.between(currentEntryTime, now);

                // To ADD hours to parking duration, we set entry time to be (hoursToAdd) hours BEFORE NOW
                // This replaces the existing entry time, not adds to it
                // Example: If it's 23:30 now and we want 25 hours total parking,
                // we set entry time to NOW minus 25 hours = 22:30 yesterday
                LocalDateTime newEntryTime = now.minusHours(hoursToAdd);

                System.out.println("📅 SETTING PARKING DURATION TO " + hoursToAdd + " HOURS");
                System.out.println("📅 Current entry time: " + currentEntryTime);
                System.out.println("📅 Current duration: " + currentDurationHours + " hours");
                System.out.println("📅 Current time (now): " + now);
                System.out.println("📅 New entry time: " + newEntryTime + " (set to " + hoursToAdd + " hours before now)");
                System.out.println("📅 New parking duration: " + ChronoUnit.HOURS.between(newEntryTime, now) + " hours");

                // Update the entry time in database
                boolean updated = historyController.updateEntryTime(licensePlate, newEntryTime);

                if (updated) {
                    long newDuration = ChronoUnit.HOURS.between(newEntryTime, now);

                    String message = "✅ Parking duration set!\n\n" +
                        "Previous entry: " + currentEntryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                        "New entry:      " + newEntryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                        "Current time:   " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n" +
                        "Previous duration: " + currentDurationHours + " hours\n" +
                        "New duration:      " + hoursToAdd + " hours\n\n" +
                        "⚠️ Click 'Search Vehicle' to calculate fees.";

                    JOptionPane.showMessageDialog(this,
                        message,
                        "Test Mode - Duration Set to " + hoursToAdd + " Hours",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Clear current data so user needs to search again
                    vehicleInfoArea.setText("");
                    billDetailsArea.setText("");
                    totalDueLabel.setText("RM 0.00");
                    processPaymentButton.setEnabled(false);
                } else {
                    throw new Exception("Failed to update entry time");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error adjusting time: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        private JPanel createSearchPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            panel.setBorder(createSectionBorder("Find Vehicle"));
            
            JLabel label = createLabel("License Plate:");
            licensePlateSearchField = new JTextField(20);
            licensePlateSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
            
            searchButton = createStyledButton("Search Vehicle", new Color(52, 152, 219));
            searchButton.addActionListener(e -> searchVehicle());
            
            panel.add(label);
            panel.add(licensePlateSearchField);
            panel.add(searchButton);
            
            return panel;
        }
        
        private JPanel createCenterPanel() {
            JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
            
            // Vehicle information
            JPanel vehiclePanel = new JPanel(new BorderLayout());
            vehiclePanel.setBorder(createSectionBorder("Vehicle Information"));
            
            vehicleInfoArea = new JTextArea();
            vehicleInfoArea.setEditable(false);
            vehicleInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            vehicleInfoArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane vehicleScrollPane = new JScrollPane(vehicleInfoArea);
            vehiclePanel.add(vehicleScrollPane, BorderLayout.CENTER);
            
            // Bill details
            JPanel billPanel = new JPanel(new BorderLayout());
            billPanel.setBorder(createSectionBorder("Billing Details"));
            
            billDetailsArea = new JTextArea();
            billDetailsArea.setEditable(false);
            billDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            billDetailsArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane billScrollPane = new JScrollPane(billDetailsArea);
            billPanel.add(billScrollPane, BorderLayout.CENTER);
            
            panel.add(vehiclePanel);
            panel.add(billPanel);
            
            return panel;
        }
        
        private JPanel createPaymentPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(createSectionBorder("Payment Processing"));
            panel.setPreferredSize(new Dimension(0, 150));
            
            JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            
            // Payment method
            formPanel.add(createLabel("Payment Method:"));
            paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card"});
            paymentMethodCombo.setPreferredSize(new Dimension(150, 30));
            paymentMethodCombo.addActionListener(e -> toggleCashInput());
            formPanel.add(paymentMethodCombo);
            
            // Cash amount
            formPanel.add(createLabel("Cash Amount:"));
            cashAmountField = new JTextField(10);
            cashAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(cashAmountField);
            
            // Total due
            formPanel.add(Box.createHorizontalStrut(20));
            JLabel totalLabel = createLabel("TOTAL DUE:");
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            formPanel.add(totalLabel);
            
            totalDueLabel = new JLabel("RM 0.00");
            totalDueLabel.setFont(new Font("Arial", Font.BOLD, 20));
            totalDueLabel.setForeground(new Color(231, 76, 60));
            formPanel.add(totalDueLabel);
            
            // Process button
            formPanel.add(Box.createHorizontalStrut(20));
            processPaymentButton = createStyledButton("Process Payment & Exit", new Color(46, 204, 113));
            processPaymentButton.setEnabled(false);
            processPaymentButton.addActionListener(e -> processPayment());
            formPanel.add(processPaymentButton);
            
            panel.add(formPanel, BorderLayout.CENTER);
            
            return panel;
        }
        
        private void toggleCashInput() {
            boolean isCash = paymentMethodCombo.getSelectedItem().equals("Cash");
            cashAmountField.setEnabled(isCash);
            if (!isCash) {
                cashAmountField.setText("");
            }
        }
        
        private void searchVehicle() {
            String licensePlate = licensePlateSearchField.getText().trim().toUpperCase();

            if (!validateExitInput(licensePlate)) {
                return;
            }

            // Get parking session data
            ParkingSessionData sessionData = retrieveParkingSession(licensePlate);
            if (sessionData == null) {
                return;
            }

            // Calculate parking duration and fees
            FeeCalculation feeCalc = calculateParkingFees(sessionData);

            // Display results
            displayVehicleInfo(sessionData, feeCalc);
            displayBillingDetails(sessionData, feeCalc);

            totalDueLabel.setText(String.format("RM %.2f", feeCalc.totalDue));
            processPaymentButton.setEnabled(true);
        }

        private boolean validateExitInput(String licensePlate) {
            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter license plate number!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (!historyController.isVehicleParked(licensePlate)) {
                JOptionPane.showMessageDialog(this,
                    "Vehicle " + licensePlate + " is not currently parked!",
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        }

        private ParkingSessionData retrieveParkingSession(String licensePlate) {
            Map<String, Object> session = historyController.getActiveParkingSession(licensePlate);
            if (session == null) {
                JOptionPane.showMessageDialog(this,
                    "Error retrieving parking session!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new ParkingSessionData(
                licensePlate,
                (String) session.get("spot_id"),
                (LocalDateTime) session.get("entry_time"),
                LocalDateTime.now()
            );
        }

        private FeeCalculation calculateParkingFees(ParkingSessionData sessionData) {
            // Calculate duration
            DurationInfo duration = calculateDuration(sessionData.entryTime, sessionData.exitTime);

            // Get vehicle and spot info
            boolean hasHandicappedCard = vehicleController.hasHandicappedCard(sessionData.licensePlate);
            String vehicleType = vehicleController.getVehicleType(sessionData.licensePlate);
            SpotInfo spotInfo = getSpotInfo(sessionData.spotId);

            // Calculate hourly rate
            double hourlyRate = calculateHourlyRate(vehicleType, spotInfo.spotType, hasHandicappedCard);
            double parkingFee = duration.billingHours * hourlyRate;

            // Calculate fines
            double overstayFine = calculateOverstayFine(sessionData, duration.totalHours);
            double existingFines = fineController.getTotalUnpaidFines(sessionData.licensePlate);

            return new FeeCalculation(
                duration,
                spotInfo,
                vehicleType,
                hasHandicappedCard,
                hourlyRate,
                parkingFee,
                overstayFine,
                existingFines,
                parkingFee + overstayFine + existingFines
            );
        }

        private DurationInfo calculateDuration(LocalDateTime entryTime, LocalDateTime exitTime) {
            long totalMinutes = ChronoUnit.MINUTES.between(entryTime, exitTime);

            if (totalMinutes < 0) {
                JOptionPane.showMessageDialog(this,
                    "Error: Entry time is after exit time!",
                    "Invalid Time",
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }

            long actualHours = totalMinutes / 60;
            long actualMinutes = totalMinutes % 60;
            long totalHours = ChronoUnit.HOURS.between(entryTime, exitTime);

            // Calculate billing hours (rounded up, minimum 1 hour)
            long billingHours = actualHours;
            if (actualHours == 0 && actualMinutes > 0) {
                billingHours = 1;
            } else if (actualMinutes > 0) {
                billingHours++;
            }
            if (billingHours == 0) billingHours = 1;

            return new DurationInfo(actualHours, actualMinutes, billingHours, totalHours);
        }

        private SpotInfo getSpotInfo(String spotId) {
            List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
            for (Map<String, Object> spot : spots) {
                if (spotId.equals(spot.get("spot_id"))) {
                    return new SpotInfo(
                        spotId,
                        (String) spot.get("spot_type"),
                        getHourlyRate((String) spot.get("spot_type"))
                    );
                }
            }
            return new SpotInfo(spotId, "REGULAR", 5.0);
        }

        private double calculateHourlyRate(String vehicleType, String spotType, boolean hasHandicappedCard) {
            boolean isHandicappedVehicle = "HANDICAP_VEHICLE".equals(vehicleType);

            if (isHandicappedVehicle) {
                // Handicapped Vehicle: RM 2/hour everywhere, FREE in HANDICAP with card
                if (spotType.equals("HANDICAP") && hasHandicappedCard) {
                    System.out.println("✅ FREE parking (Handicapped Vehicle + Handicapped spot + Card)");
                    return 0.0;
                }
                System.out.println("✅ Handicapped Vehicle rate: RM 2.00/hr");
                return 2.0;
            }

            // Regular vehicles - FREE in HANDICAP spots with card
            double rate = getHourlyRate(spotType);
            if (spotType.equals("HANDICAP") && hasHandicappedCard) {
                System.out.println("✅ FREE parking (Handicapped spot + Card holder)");
                return 0.0;
            }

            return rate;
        }

        private double calculateOverstayFine(ParkingSessionData sessionData, long totalHours) {
            if (totalHours <= 24) {
                return 0.0;
            }

            Duration overstayDuration = Duration.between(sessionData.entryTime, sessionData.exitTime);
            double fine = fineController.getCurrentStrategy().calculateFine(overstayDuration);
            System.out.println("⚠️ Vehicle overstayed! Fine: RM " + fine + " (not saved yet)");
            return fine;
        }

        private void displayVehicleInfo(ParkingSessionData sessionData, FeeCalculation feeCalc) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            StringBuilder info = new StringBuilder();
            info.append("LICENSE PLATE: ").append(sessionData.licensePlate).append("\n");
            info.append("PARKING SPOT: ").append(sessionData.spotId).append("\n");
            info.append("SPOT TYPE: ").append(feeCalc.spotInfo.spotType).append("\n");
            info.append("ENTRY TIME: ").append(sessionData.entryTime.format(formatter)).append("\n");
            info.append("EXIT TIME: ").append(sessionData.exitTime.format(formatter)).append("\n");
            info.append("DURATION: ").append(feeCalc.duration.actualHours).append(" hour(s) ")
                .append(feeCalc.duration.actualMinutes).append(" min(s)\n");

            vehicleInfoArea.setText(info.toString());
        }

        private void displayBillingDetails(ParkingSessionData sessionData, FeeCalculation feeCalc) {
            StringBuilder bill = new StringBuilder();
            bill.append("BILLING BREAKDOWN\n");
            bill.append("─────────────────────────────────────\n");

            appendParkingFeeDetails(bill, feeCalc);
            appendFineDetails(bill, feeCalc);

            bill.append("─────────────────────────────────────\n");
            bill.append(String.format("TOTAL DUE:        RM %.2f\n", feeCalc.totalDue));

            billDetailsArea.setText(bill.toString());
        }

        private void appendParkingFeeDetails(StringBuilder bill, FeeCalculation feeCalc) {
            boolean isHandicappedVehicle = "HANDICAP_VEHICLE".equals(feeCalc.vehicleType);
            boolean isFreeParking = feeCalc.spotInfo.spotType.equals("HANDICAP") &&
                                    feeCalc.hasHandicappedCard && feeCalc.hourlyRate == 0.0;

            if (isFreeParking) {
                bill.append("Parking Fee:      RM 0.00 (FREE)\n");
                bill.append("  (Handicapped spot + Card holder)\n");
                bill.append(String.format("  (Would be: %d hours × RM 2.00/hr = RM %.2f)\n",
                    feeCalc.duration.billingHours, feeCalc.duration.billingHours * 2.0));
            } else if (isHandicappedVehicle && feeCalc.hourlyRate == 2.0) {
                bill.append(String.format("Parking Fee:      RM %.2f\n", feeCalc.parkingFee));
                bill.append(String.format("  (%d hours × RM %.2f/hr)\n",
                    feeCalc.duration.billingHours, feeCalc.hourlyRate));
                bill.append("  (Handicapped Vehicle discounted rate)\n");
            } else {
                bill.append(String.format("Parking Fee:      RM %.2f\n", feeCalc.parkingFee));
                bill.append(String.format("  (%d hours × RM %.2f/hr)\n",
                    feeCalc.duration.billingHours, feeCalc.hourlyRate));
                if (feeCalc.duration.billingHours > feeCalc.duration.actualHours ||
                    (feeCalc.duration.billingHours == 1 && feeCalc.duration.actualHours == 0)) {
                    bill.append("  (Minimum 1 hour charge applied)\n");
                }
            }
        }

        private void appendFineDetails(StringBuilder bill, FeeCalculation feeCalc) {
            if (feeCalc.overstayFine > 0) {
                bill.append(String.format("\nOverstay Fine:    RM %.2f\n", feeCalc.overstayFine));
                bill.append("  (Parking >24 hours - will be saved on exit)\n");
            }
            if (feeCalc.existingFines > 0) {
                bill.append(String.format("\nExisting Unpaid:  RM %.2f\n", feeCalc.existingFines));
                bill.append("  (Previous fines from database)\n");
            }
        }

        // Inner classes for data encapsulation
        private class ParkingSessionData {
            String licensePlate;
            String spotId;
            LocalDateTime entryTime;
            LocalDateTime exitTime;

            ParkingSessionData(String licensePlate, String spotId, LocalDateTime entryTime, LocalDateTime exitTime) {
                this.licensePlate = licensePlate;
                this.spotId = spotId;
                this.entryTime = entryTime;
                this.exitTime = exitTime;
            }
        }

        private class DurationInfo {
            long actualHours;
            long actualMinutes;
            long billingHours;
            long totalHours;

            DurationInfo(long actualHours, long actualMinutes, long billingHours, long totalHours) {
                this.actualHours = actualHours;
                this.actualMinutes = actualMinutes;
                this.billingHours = billingHours;
                this.totalHours = totalHours;
            }
        }

        private class SpotInfo {
            String spotId;
            String spotType;
            double baseRate;

            SpotInfo(String spotId, String spotType, double baseRate) {
                this.spotId = spotId;
                this.spotType = spotType;
                this.baseRate = baseRate;
            }
        }

        private class FeeCalculation {
            DurationInfo duration;
            SpotInfo spotInfo;
            String vehicleType;
            boolean hasHandicappedCard;
            double hourlyRate;
            double parkingFee;
            double overstayFine;
            double existingFines;
            double totalDue;

            FeeCalculation(DurationInfo duration, SpotInfo spotInfo, String vehicleType,
                          boolean hasHandicappedCard, double hourlyRate, double parkingFee,
                          double overstayFine, double existingFines, double totalDue) {
                this.duration = duration;
                this.spotInfo = spotInfo;
                this.vehicleType = vehicleType;
                this.hasHandicappedCard = hasHandicappedCard;
                this.hourlyRate = hourlyRate;
                this.parkingFee = parkingFee;
                this.overstayFine = overstayFine;
                this.existingFines = existingFines;
                this.totalDue = totalDue;
            }
        }
        
        private double getHourlyRate(String spotType) {
            switch (spotType) {
                case "COMPACT": return 2.0;
                case "REGULAR": return 5.0;
                case "HANDICAP": return 2.0;
                case "RESERVED": return 10.0;
                default: return 5.0;
            }
        }

        private void processPayment() {
            String licensePlate = licensePlateSearchField.getText().trim().toUpperCase();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String totalText = totalDueLabel.getText().replace("RM ", "");
            double totalDue = Double.parseDouble(totalText);
            
            if (paymentMethod.equals("Cash")) {
                String cashText = cashAmountField.getText().trim();
                if (cashText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter cash amount!",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                double cashAmount = Double.parseDouble(cashText);
                if (cashAmount < totalDue) {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient cash amount!\n" +
                        "Total due: RM " + String.format("%.2f", totalDue) + "\n" +
                        "Cash given: RM " + String.format("%.2f", cashAmount),
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double change = cashAmount - totalDue;
                processExitAndShowReceipt(licensePlate, paymentMethod, totalDue, cashAmount, change);
            } else {
                processExitAndShowReceipt(licensePlate, paymentMethod, totalDue, totalDue, 0.0);
            }
        }

        private void processExitAndShowReceipt(String licensePlate, String paymentMethod,
                                               double total, double paid, double change) {
            try {
                // Get parking session
                Map<String, Object> session = historyController.getActiveParkingSession(licensePlate);
                if (session == null) {
                    throw new Exception("Parking session not found");
                }

                String spotId = (String) session.get("spot_id");
                LocalDateTime entryTime = (LocalDateTime) session.get("entry_time");
                LocalDateTime exitTime = LocalDateTime.now();

                // Create overstay fine if applicable (only saved once at exit time)
                if (ChronoUnit.HOURS.between(entryTime, exitTime) > 24) {
                    com.parkingLot.models.fines.Fine overstayFine = fineController.checkAndCreateOverstayFine(
                        licensePlate, entryTime, exitTime
                    );
                    if (overstayFine != null) {
                        System.out.println("✅ Overstay fine created and saved: RM " + overstayFine.getAmount());
                    }
                }

                // Update parking history with exit time and fee
                boolean historyUpdated = historyController.completeParkingExit(licensePlate, exitTime, total);
                if (!historyUpdated) {
                    throw new Exception("Failed to update parking history");
                }

                // Free up the parking spot
                boolean spotFreed = spotController.updateSpotStatus(spotId, 0, null);
                if (!spotFreed) {
                    throw new Exception("Failed to free parking spot");
                }

                // Mark all fines as paid (including the overstay fine we just created)
                fineController.markAllFinesAsPaid(licensePlate);

                // Show receipt
                showReceipt(licensePlate, spotId, paymentMethod, total, paid, change);

                // Clear form
                licensePlateSearchField.setText("");
                vehicleInfoArea.setText("");
                billDetailsArea.setText("");
                totalDueLabel.setText("RM 0.00");
                cashAmountField.setText("");
                processPaymentButton.setEnabled(false);

                JOptionPane.showMessageDialog(this,
                    "Payment processed successfully!\n" +
                    "Vehicle " + licensePlate + " can now exit.\n" +
                    "Spot " + spotId + " is now available.",
                    "Exit Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error processing exit: " + e.getMessage(),
                    "Exit Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
        private void showReceipt(String licensePlate, String spotId, String paymentMethod,
                                double total, double paid, double change) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            String receipt = String.format(
                "   UNIVERSITY PARKING LOT - EXIT RECEIPT    \n" +
                "============================================\n" +
                " License Plate:  %-28s\n" +
                " Parking Spot:   %-28s\n" +
                " Exit Time:      %-28s\n" +
                "============================================\n" +
                " Total Amount:   RM %23.2f\n" +
                " Paid:           RM %23.2f\n" +
                " Payment Method: %-28s\n",
                licensePlate,
                spotId,
                now.format(formatter),
                total,
                paid,
                paymentMethod
            );
            
            if (paymentMethod.equals("Cash") && change > 0) {
                receipt += String.format(" Change:         RM %23.2f\n", change);
            }
            
            receipt += "============================================\n" +
                      " Thank you for using our facility!\n" +
                      " Please drive safely!\n" +
                      "============================================\n";

            JTextArea receiptArea = new JTextArea(receipt);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            receiptArea.setEditable(false);
            
            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(500, 350));

            JOptionPane.showMessageDialog(this,
                scrollPane,
                "Payment Receipt",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private Border createSectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(44, 62, 80)
        );
    }
}
