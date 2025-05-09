package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Reports extends JFrame {
    private JLabel totalFlightsLabel, totalBookingsLabel, totalUsersLabel, totalRevenueLabel;
    private JButton refreshButton, backButton;
    private Connection connection;

    public Reports() {
        setTitle("Reports");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(235, 245, 255));

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            connection = dbConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 1600, 80);
        headerPanel.setBackground(new Color(0, 120, 215));
        headerPanel.setLayout(null);
        add(headerPanel);

        JLabel heading = new JLabel("Reports");
        heading.setBounds(700, 20, 500, 35);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);

        backButton = createButton("Back", new Color(0, 100, 180));
        backButton.setBounds(20, 20, 100, 35);
        backButton.addActionListener(e -> dispose());
        headerPanel.add(backButton);

        totalFlightsLabel = createStatLabel("Total Flights: ", 200);
        totalBookingsLabel = createStatLabel("Total Bookings: ", 300);
        totalUsersLabel = createStatLabel("Total Users: ", 400);
        totalRevenueLabel = createStatLabel("Total Revenue: ₹", 500);

        refreshButton = createButton("Refresh", new Color(0, 120, 215));
        refreshButton.setBounds(700, 600, 200, 40);
        refreshButton.addActionListener(e -> loadStats());
        add(refreshButton);

        loadStats();
        setVisible(true);
    }

    private JLabel createStatLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(600, y, 600, 50);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(0, 51, 102));
        add(label);
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void loadStats() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flight");
            if (rs.next()) totalFlightsLabel.setText("Total Flights: " + rs.getInt(1));
            rs.close();

            rs = stmt.executeQuery("SELECT COUNT(*) FROM booking");
            if (rs.next()) totalBookingsLabel.setText("Total Bookings: " + rs.getInt(1));
            rs.close();

            rs = stmt.executeQuery("SELECT COUNT(*) FROM login");
            if (rs.next()) totalUsersLabel.setText("Total Users: " + rs.getInt(1));
            rs.close();

            rs = stmt.executeQuery("SELECT IFNULL(SUM(amount),0) FROM payment");
            if (rs.next()) totalRevenueLabel.setText("Total Revenue: ₹" + rs.getDouble(1));
            rs.close();

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Reports::new);
    }
} 