package com.parkingLot.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import com.parkingLot.views.ReportPanel.RevenueReportPanel.FinesReportPanel;
import com.parkingLot.views.ReportPanel.RevenueReportPanel.OccupancyReportPanel;

import java.awt.*;
import java.awt.event.*;

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
        
        add(reportTabs, BorderLayout.CENTER);
    }

    class CurrentVehiclesPanel extends JPanel {
        private JTable vehiclesTable;
        private DefaultTableModel tableModel;
        private JLabel totalVehiclesLabel;
        private JButton refreshButton;
        private JTextField searchField;
        
        public CurrentVehiclesPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
            
            JButton searchButton = createButton("Search", new Color(52, 152, 219));
            searchButton.addActionListener(e -> searchVehicles());
            searchPanel.add(searchButton);
            
            refreshButton = createButton("Refresh", new Color(46, 204, 113));
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
            panel.setBorder(createSectionBorder("Currently Parked Vehicles"));
            
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
            
            // Load from database via controller
            totalVehiclesLabel.setText("Total Vehicles: " + tableModel.getRowCount());
        }
        
        private void searchVehicles() {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                loadVehicles();
                return;
            }
            
            //Implement search via controller
            JOptionPane.showMessageDialog(this,
                "Search functionality to be implemented",
                "Search",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    class RevenueReportPanel extends JPanel {
        private JTextArea summaryArea;
        private JTable detailsTable;
        private DefaultTableModel tableModel;
        private JComboBox<String> periodCombo;
        
        public RevenueReportPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
            
            JButton generateButton = createButton("Generate Report", new Color(52, 152, 219));
            generateButton.addActionListener(e -> generateReport());
            topPanel.add(generateButton);
            
            // Split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);
            
            // Summary panel
            JPanel summaryPanel = new JPanel(new BorderLayout());
            summaryPanel.setBorder(createSectionBorder("Revenue Summary"));
            
            summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            summaryArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            summaryPanel.add(summaryScroll, BorderLayout.CENTER);
            
            // Details panel
            JPanel detailsPanel = new JPanel(new BorderLayout());
            detailsPanel.setBorder(createSectionBorder("Revenue Breakdown"));
            
            String[] columns = {"Date", "Parking Fees", "Fines Collected", "Total Revenue", "Transactions"};
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
        // Load from database via controller
            String period = (String) periodCombo.getSelectedItem();
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
            JButton refreshButton = createButton("Refresh", new Color(46, 204, 113));
            refreshButton.addActionListener(e -> loadOccupancyData());
            topPanel.add(refreshButton);
            
            // Split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);
            
            // Summary panel
            JPanel summaryPanel = new JPanel(new BorderLayout());
            summaryPanel.setBorder(createSectionBorder("Occupancy Summary"));
            
            summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            summaryArea.setBackground(new Color(236, 240, 241));
            
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            summaryPanel.add(summaryScroll, BorderLayout.CENTER);
            
            // Table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(createSectionBorder("Floor-wise Occupancy"));
            
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
        
        private void loadOccupancyData() {
            // Load from database
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
            
            JButton refreshButton = createButton("Refresh", new Color(46, 204, 113));
            refreshButton.addActionListener(e -> loadFinesData());
            
            topPanel.add(totalVehiclesLabel);
            topPanel.add(totalFinesLabel);
            topPanel.add(refreshButton);
            
            // Table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(createSectionBorder("Outstanding Fines"));
            
            String[] columns = {
                "License Plate", "Fine Type", "Fine Amount", "Issue Date", 
                "Days Overdue", "Current Location", "Status"
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
            
            // Bottom panel - actions
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            
            JButton exportButton = createButton("Export to CSV", new Color(52, 152, 219));
            exportButton.addActionListener(e -> exportFines());
            
            JButton sendReminderButton = createButton("Send Reminder", new Color(243, 156, 18));
            sendReminderButton.addActionListener(e -> sendReminder());
            
            bottomPanel.add(exportButton);
            bottomPanel.add(sendReminderButton);
            
            add(topPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
            
            // Load initial data
            loadFinesData();
        }
        
        private void loadFinesData() {
            tableModel.setRowCount(0);
            
            //Load from database
            totalVehiclesLabel.setText("Vehicles with Fines: " + tableModel.getRowCount());
            
            double total = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String amountStr = ((String) tableModel.getValueAt(i, 2)).replace("RM ", "");
                total += Double.parseDouble(amountStr);
            }
            totalFinesLabel.setText("Total Unpaid: RM " + String.format("%.2f", total));
        }
        
        private void exportFines() {
            JOptionPane.showMessageDialog(this,
                "Fines report exported to CSV successfully!",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void sendReminder() {
            int selectedRow = finesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a vehicle to send reminder!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String licensePlate = (String) tableModel.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(this,
                "Reminder sent to vehicle owner: " + licensePlate,
                "Reminder Sent",
                JOptionPane.INFORMATION_MESSAGE);
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
