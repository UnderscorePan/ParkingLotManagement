package com.parkingLot.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EntryExitPanel extends JPanel {
    
    private JTabbedPane operationTabs;
    private VehicleEntryPanel entryPanel;
    private VehicleExitPanel exitPanel;
    
    public EntryExitPanel() {
        setLayout(new BorderLayout());
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
            
            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter license plate number!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            //Call controller to fetch available spots based on vehicle type
            parkVehicleButton.setEnabled(spotsTableModel.getRowCount() > 0);
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
            String licensePlate = licensePlateField.getText().trim();
            String vehicleType = (String) vehicleTypeCombo.getSelectedItem();
            boolean isHandicapped = handicappedCheckBox.isSelected();
            
            // Call controller to park vehicle
            // Generate ticket
            String ticket = generateTicket(spotId, licensePlate, vehicleType, isHandicapped);
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
            // Top panel - Search
            JPanel searchPanel = createSearchPanel();
            
            // Center panel - Vehicle info and billing
            JPanel centerPanel = createCenterPanel();
            
            // Bottom panel - Payment
            JPanel paymentPanel = createPaymentPanel();
            
            add(searchPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
            add(paymentPanel, BorderLayout.SOUTH);
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
            String licensePlate = licensePlateSearchField.getText().trim();
            
            if (licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter license plate number!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Call controller to find vehicle
            processPaymentButton.setEnabled(true);
        }
        
        private void processPayment() {
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
                showReceipt(paymentMethod, totalDue, cashAmount, change);
            } else {
                showReceipt(paymentMethod, totalDue, totalDue, 0.0);
            }
            
            // Clear form
            licensePlateSearchField.setText("");
            vehicleInfoArea.setText("");
            billDetailsArea.setText("");
            totalDueLabel.setText("RM 0.00");
            cashAmountField.setText("");
            processPaymentButton.setEnabled(false);
        }
        
        private void showReceipt(String paymentMethod, double total, double paid, double change) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            String receipt = String.format(
                "   UNIVERSITY PARKING LOT - EXIT RECEIPT    \n" +
                "----------------------------------------\n" +
                " License Plate:  %-38s \n" +
                " Exit Time:      %-38s \n" +
                "----------------------------------------\n" +
                " Parking Fee:    RM %-34.2f \n" +
                " Fines:          RM %-34.2f \n" +
                " Total Paid:     RM %-34.2f \n" +
                " Payment Method: %-38s \n",
                licensePlateSearchField.getText(),
                now.format(formatter),
                total - 0.0, // parking fee
                0.0, // fines
                paid,
                paymentMethod
            );
            
            if (paymentMethod.equals("Cash")) {
                receipt += String.format(" Change:         RM %-34.2f \n", change);
            }
            
            receipt += " Thank you for using our facility! \n" +
                        " Please drive safely! \n" +
                        "----------------------------------------\n";
            
            JTextArea receiptArea = new JTextArea(receipt);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            receiptArea.setEditable(false);
            
            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            int result = JOptionPane.showConfirmDialog(this,
                scrollPane,
                "Payment Successful - Exit Receipt",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Vehicle exited successfully!\n" +
                    "Parking spot released.",
                    "Exit Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
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
