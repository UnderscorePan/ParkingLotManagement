package com.parkingLot.views;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private AdminPanel adminPanel;
    private EntryExitPanel entryExitPanel;
    private ReportPanel reportPanel;
    
    public MainFrame() {
        initializeFrame();
        initializeComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Parking Lot Management System - University Parking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create panels
        entryExitPanel = new EntryExitPanel();
        adminPanel = new AdminPanel();
        reportPanel = new ReportPanel();
        
        // Add tabs
        tabbedPane.addTab("Entry/Exit", entryExitPanel, "Vehicle Entry and Exit Management");
        tabbedPane.addTab("Admin",  adminPanel, "Administrative Functions");
        tabbedPane.addTab("Reports", reportPanel, "View Reports and Statistics");
        
        // Add header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("University Parking Lot Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Multi-Level Parking Management");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        headerPanel.add(textPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
