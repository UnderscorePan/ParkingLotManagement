package com.parkingLot.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.parkingLot.controllers.FineController;
import com.parkingLot.controllers.ParkingHistoryController;
import com.parkingLot.controllers.ParkingSpotController;
import com.parkingLot.controllers.TransactionController;
import com.parkingLot.controllers.VehicleController;

public class ReportPanel extends JPanel {
    
    private JTabbedPane reportTabs;
    private CurrentVehiclesPanel currentVehiclesPanel;
    private RevenueReportPanel revenuePanel;
    private OccupancyReportPanel occupancyPanel;
    private FinesReportPanel finesPanel;
    
    public ReportPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
    }
    
    private void initializeComponents() {
        reportTabs = new JTabbedPane();
        
        currentVehiclesPanel = new CurrentVehiclesPanel();
        revenuePanel = new RevenueReportPanel();
        occupancyPanel = new OccupancyReportPanel();
        finesPanel = new FinesReportPanel();
        
        reportTabs.addTab("Current Vehicles", currentVehiclesPanel);
        reportTabs.addTab("Revenue Report", revenuePanel);
        reportTabs.addTab("Occupancy Report", occupancyPanel);
        reportTabs.addTab("Outstanding Fines", finesPanel);
        
        // Auto-refresh occupancy report when tab is selected
        reportTabs.addChangeListener(e -> {
            int selectedIndex = reportTabs.getSelectedIndex();
            if (selectedIndex == 2) { // Occupancy Report tab index
                occupancyPanel.loadOccupancyData();
            }
        });
        
        add(reportTabs, BorderLayout.CENTER);
    }

    class CurrentVehiclesPanel extends JPanel {
        private JTable vehiclesTable;
        private DefaultTableModel tableModel;
        private JLabel totalVehiclesLabel;
        private JButton refreshButton;
        private JTextField searchField;
        
        private final ParkingHistoryController parkingHistoryController;
        private final ParkingSpotController spotController;
        private final FineController fineController;
        private final VehicleController vehicleController;
        
        public CurrentVehiclesPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Initialize controllers
            parkingHistoryController = new ParkingHistoryController();
            spotController = new ParkingSpotController();
            fineController = FineController.getInstance();
            vehicleController = new VehicleController();
            
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Top panel - controls
            JPanel topPanel = createTopPanel();
            
            // Center - table
            JPanel centerPanel = createTablePanel();
            
            add(topPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
        }
        
        private JPanel createTopPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            searchPanel.add(new JLabel("Search:"));
            searchField = new JTextField(20);
            searchPanel.add(searchField);
            
            JButton searchButton = ReportPanel.this.createButton("Search", new Color(52, 152, 219));
            searchButton.addActionListener(e -> searchVehicles());
            searchPanel.add(searchButton);
            
            refreshButton = ReportPanel.this.createButton("Refresh", new Color(46, 204, 113));
            refreshButton.addActionListener(e -> loadVehicles());
            searchPanel.add(refreshButton);
            
            // Stats panel
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalVehiclesLabel = new JLabel("Total Vehicles: 0");
            totalVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statsPanel.add(totalVehiclesLabel);
            
            panel.add(searchPanel, BorderLayout.WEST);
            panel.add(statsPanel, BorderLayout.EAST);
            
            return panel;
        }
        
        private JPanel createTablePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(ReportPanel.this.createSectionBorder("Currently Parked Vehicles"));

            String[] columns = {
                "License Plate", "Vehicle Type", "Spot ID", "Floor",
                "Entry Time", "Duration (hrs)", "Current Fee", "Unpaid Fines"
            };
            
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            vehiclesTable = new JTable(tableModel);
            vehiclesTable.setRowHeight(25);
            vehiclesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane scrollPane = new JScrollPane(vehiclesTable);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Load initial data
            loadVehicles();
            
            return panel;
        }
        
        private void loadVehicles() {
            tableModel.setRowCount(0);
            
            try {
                List<Map<String, Object>> currentVehicles = parkingHistoryController.getCurrentlyParkedVehicles();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime now = LocalDateTime.now();
                
                for (Map<String, Object> vehicle : currentVehicles) {
                    String licensePlate = (String) vehicle.get("plate_number");
                    String vehicleType = (String) vehicle.get("vehicle_type");
                    String spotId = (String) vehicle.get("spot_id");
                    String entryTimeStr = (String) vehicle.get("entry_time");
                    
                    // Parse entry time
                    LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr);
                    
                    // Get floor number from spot
                    int floorNumber = getFloorFromSpot(spotId);
                    
                    // Calculate duration in hours (minimum 1 hour)
                    long totalMinutes = ChronoUnit.MINUTES.between(entryTime, now);
                    long actualHours = totalMinutes / 60;
                    long actualMinutes = totalMinutes % 60;
                    long billingHours = actualHours;
                    if (actualHours == 0 && actualMinutes > 0) {
                        billingHours = 1;
                    } else if (actualMinutes > 0) {
                        billingHours++;
                    }
                    if (billingHours == 0) billingHours = 1;
                    
                    // Debug output
                    System.out.println("🔍 Vehicle: " + licensePlate);
                    System.out.println("   Entry: " + entryTime + " | Now: " + now);
                    System.out.println("   Total Minutes: " + totalMinutes);
                    System.out.println("   Actual Hours: " + actualHours + " | Actual Minutes: " + actualMinutes);
                    System.out.println("   Billing Hours: " + billingHours);
                    
                    // Calculate current fee
                    double hourlyRate = getHourlyRateForVehicle(licensePlate, spotId, vehicleType);
                    double currentFee = billingHours * hourlyRate;
                    
                    // Get unpaid fines
                    double unpaidFines = fineController.getTotalUnpaidFines(licensePlate);
                    
                    // Add row to table
                    Object[] row = {
                        licensePlate,
                        vehicleType,
                        spotId,
                        "Floor " + floorNumber,
                        entryTime.format(formatter),
                        billingHours + " hrs",
                        String.format("RM %.2f", currentFee),
                        String.format("RM %.2f", unpaidFines)
                    };
                    tableModel.addRow(row);
                }
                
                totalVehiclesLabel.setText("Total Vehicles: " + tableModel.getRowCount());
                
            } catch (Exception e) {
                System.err.println("❌ Error loading vehicles: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading vehicles: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private int getFloorFromSpot(String spotId) {
            try {
                List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
                for (Map<String, Object> spot : spots) {
                    if (spotId.equals(spot.get("spot_id"))) {
                        return (int) spot.get("floor_number");
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error getting floor number: " + e.getMessage());
            }
            return 0;
        }
        
        private double getHourlyRateForVehicle(String licensePlate, String spotId, String vehicleType) {
            try {
                boolean hasHandicappedCard = vehicleController.hasHandicappedCard(licensePlate);
                String spotType = getSpotType(spotId);
                
                System.out.println("🔍 Rate Calculation for " + licensePlate);
                System.out.println("   Spot Type: " + spotType + " | Vehicle Type: " + vehicleType);
                System.out.println("   Has Handicapped Card: " + hasHandicappedCard);
                
                boolean isHandicappedVehicle = "HANDICAP_VEHICLE".equals(vehicleType);
                
                if (isHandicappedVehicle) {
                    if ("HANDICAP".equals(spotType) && hasHandicappedCard) {
                        System.out.println("   Rate: RM 0.00 (FREE - Handicapped Vehicle + Card)");
                        return 0.0; // FREE
                    }
                    System.out.println("   Rate: RM 2.00 (Handicapped Vehicle)");
                    return 2.0; // RM 2/hour for handicapped vehicles
                }
                
                // Regular vehicles - FREE in HANDICAP spots with card
                if ("HANDICAP".equals(spotType) && hasHandicappedCard) {
                    System.out.println("   Rate: RM 0.00 (FREE - Handicap Spot + Card)");
                    return 0.0; // FREE
                }
                
                // Get base rate for spot type
                double rate = getBaseRateForSpotType(spotType);
                System.out.println("   Rate: RM " + rate + " (Base rate for " + spotType + ")");
                return rate;
                
            } catch (Exception e) {
                System.err.println("❌ Error calculating hourly rate: " + e.getMessage());
                return 5.0; // Default rate
            }
        }
        
        private String getSpotType(String spotId) {
            try {
                List<Map<String, Object>> spots = spotController.getAllSpotsForDisplay();
                for (Map<String, Object> spot : spots) {
                    if (spotId.equals(spot.get("spot_id"))) {
                        return (String) spot.get("spot_type");
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error getting spot type: " + e.getMessage());
            }
            return "REGULAR";
        }
        
        private double getBaseRateForSpotType(String spotType) {
            switch (spotType) {
                case "HANDICAP":
                    return 3.0;
                case "COMPACT":
                    return 2.0;
                case "LARGE":
                    return 6.0;
                case "MOTORCYCLE":
                    return 3.0;
                case "REGULAR":
                default:
                    return 5.0;
            }
        }
        
        private void searchVehicles() {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                loadVehicles();
                return;
            }
            
            tableModel.setRowCount(0);
            
            try {
                List<Map<String, Object>> currentVehicles = parkingHistoryController.getCurrentlyParkedVehicles();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime now = LocalDateTime.now();
                int matchCount = 0;
                
                for (Map<String, Object> vehicle : currentVehicles) {
                    String licensePlate = (String) vehicle.get("plate_number");
                    String vehicleType = (String) vehicle.get("vehicle_type");
                    String spotId = (String) vehicle.get("spot_id");
                    
                    // Check if matches search term
                    if (!licensePlate.toLowerCase().contains(searchTerm) && 
                        !vehicleType.toLowerCase().contains(searchTerm) &&
                        !spotId.toLowerCase().contains(searchTerm)) {
                        continue;
                    }
                    
                    matchCount++;
                    String entryTimeStr = (String) vehicle.get("entry_time");
                    LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr);
                    int floorNumber = getFloorFromSpot(spotId);
                    
                    // Calculate duration
                    long totalMinutes = ChronoUnit.MINUTES.between(entryTime, now);
                    long actualHours = totalMinutes / 60;
                    long actualMinutes = totalMinutes % 60;
                    long billingHours = actualHours;
                    if (actualHours == 0 && actualMinutes > 0) {
                        billingHours = 1;
                    } else if (actualMinutes > 0) {
                        billingHours++;
                    }
                    if (billingHours == 0) billingHours = 1;
                    
                    // Calculate current fee
                    double hourlyRate = getHourlyRateForVehicle(licensePlate, spotId, vehicleType);
                    double currentFee = billingHours * hourlyRate;
                    double unpaidFines = fineController.getTotalUnpaidFines(licensePlate);
                    
                    Object[] row = {
                        licensePlate,
                        vehicleType,
                        spotId,
                        "Floor " + floorNumber,
                        entryTime.format(formatter),
                        billingHours + " hrs",
                        String.format("RM %.2f", currentFee),
                        String.format("RM %.2f", unpaidFines)
                    };
                    tableModel.addRow(row);
                }
                
                totalVehiclesLabel.setText("Total Vehicles: " + matchCount);
                
                if (matchCount == 0) {
                    JOptionPane.showMessageDialog(this,
                        "No vehicles found matching: " + searchTerm,
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error searching vehicles: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error searching vehicles: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class RevenueReportPanel extends JPanel {
        private JTextArea summaryArea;
        private JTable detailsTable;
        private DefaultTableModel tableModel;
        private JComboBox<String> periodCombo;
        private final TransactionController transactionController;
        
        public RevenueReportPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            transactionController = new TransactionController();
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Top - controls
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topPanel.add(new JLabel("Report Period:"));
            
            periodCombo = new JComboBox<>(new String[]{
                "Today",
                "This Week",
                "This Month",
                "All Time"
            });
            topPanel.add(periodCombo);
            
            JButton generateButton = ReportPanel.this.createButton("Generate Report", new Color(52, 152, 219));
            generateButton.addActionListener(e -> generateReport());
            topPanel.add(generateButton);
            
            // Split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);
            
            // Summary panel
            JPanel summaryPanel = new JPanel(new BorderLayout());
            summaryPanel.setBorder(ReportPanel.this.createSectionBorder("Revenue Summary"));

            summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            summaryArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            summaryPanel.add(summaryScroll, BorderLayout.CENTER);
            
            // Details panel
            JPanel detailsPanel = new JPanel(new BorderLayout());
            detailsPanel.setBorder(ReportPanel.this.createSectionBorder("Revenue Breakdown"));

            String[] columns = {"Date", "License Plate", "Parking Fees", "Fines Collected", "Total Revenue", "Transaction Type"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            detailsTable = new JTable(tableModel);
            detailsTable.setRowHeight(25);
            detailsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            JScrollPane detailsScroll = new JScrollPane(detailsTable);
            detailsPanel.add(detailsScroll, BorderLayout.CENTER);
            
            splitPane.setTopComponent(summaryPanel);
            splitPane.setBottomComponent(detailsPanel);
            
            add(topPanel, BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);
            
            // Load initial data
            generateReport();
        }
        
        private void generateReport() {
            String period = (String) periodCombo.getSelectedItem();
            tableModel.setRowCount(0);
            
            try {
                // Get date range based on period
                LocalDateTime startDate;
                LocalDateTime endDate = LocalDateTime.now();
                
                switch (period) {
                    case "Today":
                        startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                        break;
                    case "This Week":
                        startDate = LocalDateTime.now().minusDays(7);
                        break;
                    case "This Month":
                        startDate = LocalDateTime.now().minusMonths(1);
                        break;
                    case "All Time":
                    default:
                        startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
                        break;
                }
                
                // Get transactions
                List<Map<String, Object>> transactions;
                if (period.equals("All Time")) {
                    transactions = transactionController.getAllTransactions();
                } else {
                    transactions = transactionController.getTransactionsByDateRange(startDate, endDate);
                }
                
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                
                double totalParkingFees = 0;
                double totalFines = 0;
                double totalRevenue = 0;
                int totalTransactionCount = transactions.size();
                
                // Display each transaction as a separate row
                for (Map<String, Object> transaction : transactions) {
                    String transactionDateStr = (String) transaction.get("transaction_date");
                    LocalDateTime transactionDate = LocalDateTime.parse(transactionDateStr);
                    
                    String licensePlate = (String) transaction.get("plate_number");
                    double amount = (double) transaction.get("amount");
                    double parkingFee = (double) transaction.get("parking_fee");
                    double fineAmount = (double) transaction.get("fine_amount");
                    String paymentMethod = (String) transaction.get("payment_method");
                    
                    // Add to totals
                    totalParkingFees += parkingFee;
                    totalFines += fineAmount;
                    totalRevenue += amount;
                    
                    // Add row to table
                    Object[] row = {
                        transactionDate.format(dateTimeFormatter),
                        licensePlate,
                        String.format("RM %.2f", parkingFee),
                        String.format("RM %.2f", fineAmount),
                        String.format("RM %.2f", amount),
                        paymentMethod
                    };
                    tableModel.addRow(row);
                }
                
                // Update summary
                StringBuilder summary = new StringBuilder();
                summary.append("REVENUE SUMMARY - ").append(period).append("\n");
                summary.append("═══════════════════════════════════════════\n\n");
                summary.append(String.format("Total Parking Fees:    RM %,10.2f\n", totalParkingFees));
                summary.append(String.format("Total Fines Collected: RM %,10.2f\n", totalFines));
                summary.append("───────────────────────────────────────────\n");
                summary.append(String.format("TOTAL REVENUE:         RM %,10.2f\n\n", totalRevenue));
                summary.append(String.format("Total Transactions:    %,d\n", totalTransactionCount));
                summary.append(String.format("Average per Transaction: RM %.2f\n", 
                    totalTransactionCount > 0 ? totalRevenue / totalTransactionCount : 0));
                
                // Payment method breakdown
                Map<String, Double> paymentMethodRevenue = transactionController.getRevenueByPaymentMethod();
                if (!paymentMethodRevenue.isEmpty()) {
                    summary.append("\n\nPAYMENT METHOD BREAKDOWN:\n");
                    summary.append("───────────────────────────────────────────\n");
                    for (Map.Entry<String, Double> entry : paymentMethodRevenue.entrySet()) {
                        summary.append(String.format("%-15s: RM %,10.2f\n", entry.getKey(), entry.getValue()));
                    }
                }
                
                // Transaction type breakdown
                Map<String, Double> transactionTypeRevenue = transactionController.getRevenueByTransactionType();
                if (!transactionTypeRevenue.isEmpty()) {
                    summary.append("\n\nTRANSACTION TYPE BREAKDOWN:\n");
                    summary.append("───────────────────────────────────────────\n");
                    for (Map.Entry<String, Double> entry : transactionTypeRevenue.entrySet()) {
                        summary.append(String.format("%-20s: RM %,10.2f\n", entry.getKey(), entry.getValue()));
                    }
                }
                
                summaryArea.setText(summary.toString());
                
            } catch (Exception e) {
                System.err.println("❌ Error generating revenue report: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error generating report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class OccupancyReportPanel extends JPanel {
        private JTextArea summaryArea;
        private JTable occupancyTable;
        private DefaultTableModel tableModel;
        
        public OccupancyReportPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Top panel - controls
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshButton = ReportPanel.this.createButton("Refresh", new Color(46, 204, 113));
            refreshButton.addActionListener(e -> loadOccupancyData());
            topPanel.add(refreshButton);
            
            // Split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);
            
            // Summary panel
            JPanel summaryPanel = new JPanel(new BorderLayout());
            summaryPanel.setBorder(ReportPanel.this.createSectionBorder("Occupancy Summary"));

            summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            summaryArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            summaryPanel.add(summaryScroll, BorderLayout.CENTER);
            
            // Table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(ReportPanel.this.createSectionBorder("Floor-wise Occupancy"));

            String[] columns = {"Floor", "Total Spots", "Occupied", "Available", "Occupancy %"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            occupancyTable = new JTable(tableModel);
            occupancyTable.setRowHeight(25);
            occupancyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            JScrollPane tableScroll = new JScrollPane(occupancyTable);
            tablePanel.add(tableScroll, BorderLayout.CENTER);
            
            splitPane.setTopComponent(summaryPanel);
            splitPane.setBottomComponent(tablePanel);
            
            add(topPanel, BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);
            
            // Load initial data
            loadOccupancyData();
        }
        
        public void loadOccupancyData() {
            // ============ COMPOSITE PATTERN DEMONSTRATION ============
            // Build parking lot structure from database using Composite Pattern
            com.parkingLot.models.ParkingLot parkingLot = com.parkingLot.utils.ParkingLotBuilder.buildFromDatabase();
            
            // Use composite pattern methods to get statistics recursively
            int totalSpots = parkingLot.getTotalSpots();
            int occupiedSpots = parkingLot.getOccupiedSpots();
            int availableSpots = totalSpots - occupiedSpots;
            double occupancyRate = parkingLot.getOccupancyRate();
            
            // Display overall summary using composite pattern results
            StringBuilder summary = new StringBuilder();
            summary.append("╔════════════════════════════════════════════╗\n");
            summary.append("║     PARKING LOT OCCUPANCY SUMMARY          ║\n");
            summary.append("╚════════════════════════════════════════════╝\n\n");
            summary.append(String.format("📊 Total Parking Spots:     %d\n", totalSpots));
            summary.append(String.format("🚗 Occupied Spots:          %d\n", occupiedSpots));
            summary.append(String.format("✅ Available Spots:         %d\n", availableSpots));
            summary.append(String.format("📈 Overall Occupancy Rate:  %.1f%%\n\n", occupancyRate));
            
            if (occupancyRate >= 90) {
                summary.append("⚠️  Status: NEARLY FULL - Limited availability\n");
            } else if (occupancyRate >= 70) {
                summary.append("⚡ Status: BUSY - Moderate availability\n");
            } else if (occupancyRate >= 40) {
                summary.append("✓  Status: NORMAL - Good availability\n");
            } else {
                summary.append("✓  Status: QUIET - Plenty of space available\n");
            }
            
            summary.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            summary.append("Note: Statistics calculated using Composite Pattern\n");
            summary.append("      (Recursive aggregation from Floor → ParkingLot)\n");
            
            summaryArea.setText(summary.toString());
            
            // Load floor-wise data using composite pattern
            tableModel.setRowCount(0);
            java.util.List<com.parkingLot.models.Floor> floors = parkingLot.getFloors();
            
            // Sort floors by floor number
            floors.sort((f1, f2) -> Integer.compare(f1.getFloorNumber(), f2.getFloorNumber()));
            
            for (com.parkingLot.models.Floor floor : floors) {
                // Use composite pattern methods at floor level
                int floorTotal = floor.getTotalSpots();
                int floorOccupied = floor.getOccupiedSpots();
                int floorAvailable = floorTotal - floorOccupied;
                double floorOccupancy = floor.getOccupancyRate();
                
                tableModel.addRow(new Object[]{
                    "Floor " + floor.getFloorNumber(),
                    floorTotal,
                    floorOccupied,
                    floorAvailable,
                    String.format("%.1f%%", floorOccupancy)
                });
            }
            
            System.out.println("✅ Occupancy data loaded using Composite Pattern");
        }
    }
    
    class FinesReportPanel extends JPanel {
        private JTable finesTable;
        private DefaultTableModel tableModel;
        private JLabel totalFinesLabel;
        private JLabel totalVehiclesLabel;
        
        public FinesReportPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            initializeComponents();
        }
        
        private void initializeComponents() {
            // Top panel - stats
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            
            totalVehiclesLabel = new JLabel("Vehicles with Fines: 0");
            totalVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 14));
            totalVehiclesLabel.setForeground(new Color(231, 76, 60));
            
            totalFinesLabel = new JLabel("Total Unpaid: RM 0.00");
            totalFinesLabel.setFont(new Font("Arial", Font.BOLD, 14));
            totalFinesLabel.setForeground(new Color(231, 76, 60));
            
            JButton refreshButton = ReportPanel.this.createButton("Refresh", new Color(46, 204, 113));
            refreshButton.addActionListener(e -> loadFinesData());
            
            topPanel.add(totalVehiclesLabel);
            topPanel.add(totalFinesLabel);
            topPanel.add(refreshButton);
            
            // Table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(ReportPanel.this.createSectionBorder("Outstanding Fines"));

            String[] columns = {
                "License Plate", "Fine Type", "Fine Amount", "Issue Date", 
                "Days Overdue", "Status"
            };
            
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            finesTable = new JTable(tableModel);
            finesTable.setRowHeight(25);
            finesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            finesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane scrollPane = new JScrollPane(finesTable);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            
            add(topPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            
            // Load initial data
            loadFinesData();
        }
        
        private void loadFinesData() {
            tableModel.setRowCount(0);
            
            // Load fines from database
            String sql = "SELECT f.fine_id, f.plate_number, f.fine_type, f.fine_amount, f.fine_date, f.is_paid " +
                        "FROM fines f " +
                        "WHERE f.is_paid = 0 " +
                        "ORDER BY f.fine_date DESC";
            
            int vehicleCount = 0;
            double totalUnpaid = 0.0;
            java.util.Set<String> uniquePlates = new java.util.HashSet<>();
            
            try (java.sql.Connection conn = com.parkingLot.database.databaseConnection.connect();
                 java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    String licensePlate = rs.getString("plate_number");
                    String fineType = rs.getString("fine_type");
                    double fineAmount = rs.getDouble("fine_amount");
                    String fineDateStr = rs.getString("fine_date");
                    int isPaid = rs.getInt("is_paid");
                    
                    uniquePlates.add(licensePlate);
                    totalUnpaid += fineAmount;
                    
                    // Parse date and calculate days overdue
                    LocalDateTime fineDate = LocalDateTime.parse(fineDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(fineDate, LocalDateTime.now());
                    
                    // Determine status based on is_paid column
                    String status = (isPaid == 0) ? "Unpaid" : "Paid";
                    
                    tableModel.addRow(new Object[]{
                        licensePlate,
                        fineType != null ? fineType : "N/A",
                        String.format("RM %.2f", fineAmount),
                        fineDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        daysOverdue + " days",
                        status
                    });
                }
                
                vehicleCount = uniquePlates.size();
                
            } catch (java.sql.SQLException e) {
                System.err.println("❌ Error loading fines data: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Update summary labels
            totalVehiclesLabel.setText("Vehicles with Fines: " + vehicleCount);
            totalFinesLabel.setText("Total Unpaid: RM " + String.format("%.2f", totalUnpaid));
            
            System.out.println("✅ Fines data loaded: " + tableModel.getRowCount() + " unpaid fines");
        }
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Force button to use custom background color (fixes Windows LaF issue)
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
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
