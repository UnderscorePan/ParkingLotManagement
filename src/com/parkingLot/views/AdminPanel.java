package com.parkingLot.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class AdminPanel extends JPanel {
    
    private JTextArea statusArea;
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
        
        initializeComponents();
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
        
        // Statistics Panel
        JPanel statsPanel = createStatsPanel();
        
        // Control Panel
        JPanel controlPanel = createControlPanel();
        
        // Parking Lot Visualization Panel
        JPanel visualPanel = createVisualizationPanel();
        
        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplitPane.setLeftComponent(statsPanel);
        topSplitPane.setRightComponent(visualPanel);
        topSplitPane.setResizeWeight(0.3);
        
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(topSplitPane, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(createTitledBorder("Statistics"));
        
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
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(revenuePanel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(finesPanel);
        statsPanel.add(Box.createVerticalGlue());
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        cardPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valuePanel, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(createTitledBorder("Fine Scheme Configuration"));
        
        JLabel schemeLabel = new JLabel("Fine Scheme:");
        schemeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        fineSchemeCombo = new JComboBox<>(new String[]{
            "Option A: Fixed Fine (RM 50)",
            "Option B: Progressive Fine",
            "Option C: Hourly Fine (RM 20/hour)"
        });
        fineSchemeCombo.setPreferredSize(new Dimension(300, 30));
        
        JButton applyButton = new JButton("Apply Scheme");
        applyButton.setBackground(new Color(52, 152, 219));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> applyFineScheme());
        
        refreshButton = new JButton("Refresh Data");
        refreshButton.setPreferredSize(new Dimension(150, 30));
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        controlPanel.add(schemeLabel);
        controlPanel.add(fineSchemeCombo);
        controlPanel.add(applyButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(refreshButton);
        
        return controlPanel;
    }
    
    private JPanel createVisualizationPanel() {
        JPanel visualPanel = new JPanel(new BorderLayout());
        visualPanel.setBorder(createTitledBorder("Parking Lot Overview"));
        
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusArea.setText(generateParkingLotVisualization());
        
        JScrollPane scrollPane = new JScrollPane(statusArea);
        visualPanel.add(scrollPane, BorderLayout.CENTER);
        
        return visualPanel;
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
    
    private String generateParkingLotVisualization() {
        StringBuilder sb = new StringBuilder();
        sb.append("PARKING LOT STRUCTURE (5 Floors)\n");
        sb.append("--------------------------------------\n\n");
        
        for (int floor = 1; floor <= 5; floor++) {
            sb.append(String.format("Floor %d:\n", floor));
            sb.append("  Row 1: [C] [C] [R] [R] [H] [RV]\n");
            sb.append("  Row 2: [C] [R] [R] [R] [H] [RV]\n");
            sb.append("  Row 3: [R] [R] [R] [R] [C] [RV]\n");
            sb.append("\n");
        }
        
        sb.append("\nLegend:\n");
        sb.append("  [C]  = Compact (RM 2/hr)\n");
        sb.append("  [R]  = Regular (RM 5/hr)\n");
        sb.append("  [H]  = Handicapped (RM 2/hr, FREE for handicapped card)\n");
        sb.append("  [RV] = Reserved (RM 10/hr)\n");
        sb.append("   +   = Available\n");
        sb.append("   -   = Occupied\n");
        
        return sb.toString();
    }
    
    private void applyFineScheme() {
        String selectedScheme = (String) fineSchemeCombo.getSelectedItem();
        // Call controller to update fine scheme in database
    }