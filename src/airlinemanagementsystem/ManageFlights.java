package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageFlights extends JFrame implements ActionListener {
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private Connection connection;

    public ManageFlights() {
        setTitle("Manage Flights");
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

        JLabel heading = new JLabel("Manage Flights");
        heading.setBounds(700, 20, 500, 35);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);

        backButton = createButton("Back", new Color(0, 100, 180));
        backButton.setBounds(20, 20, 100, 35);
        backButton.addActionListener(e -> dispose());
        headerPanel.add(backButton);

        String[] columnNames = {"Flight ID", "Flight Name", "Flight Number", "From", "To", "Travel Date", "Available Seats", "Price", "Journey Time", "Departure", "Arrival"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        flightsTable = new JTable(tableModel);
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        flightsTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBounds(60, 120, 1400, 500);
        add(scrollPane);

        addButton = createButton("Add Flight", new Color(0, 150, 0));
        addButton.setBounds(200, 650, 150, 40);
        addButton.addActionListener(this);
        add(addButton);

        editButton = createButton("Edit Flight", new Color(0, 120, 215));
        editButton.setBounds(400, 650, 150, 40);
        editButton.addActionListener(this);
        add(editButton);

        deleteButton = createButton("Delete Flight", new Color(200, 0, 0));
        deleteButton.setBounds(600, 650, 150, 40);
        deleteButton.addActionListener(this);
        add(deleteButton);

        refreshButton = createButton("Refresh", new Color(100, 100, 100));
        refreshButton.setBounds(800, 650, 150, 40);
        refreshButton.addActionListener(e -> loadFlights());
        add(refreshButton);

        loadFlights();
        setVisible(true);
    }

    private void loadFlights() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT f.flight_id, f.flight_name, f.flight_number, d.city as departure_city, a.city as arrival_city, f.travel_date, f.available_seats, f.price, f.journey_time, f.departure_date, f.arrival_date FROM flight f JOIN airport d ON f.departure_airport_id = d.airport_id JOIN airport a ON f.arrival_airport_id = a.airport_id ORDER BY f.travel_date DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("flight_id"),
                    rs.getString("flight_name"),
                    rs.getString("flight_number"),
                    rs.getString("departure_city"),
                    rs.getString("arrival_city"),
                    rs.getDate("travel_date"),
                    rs.getInt("available_seats"),
                    rs.getDouble("price"),
                    rs.getString("journey_time"),
                    rs.getString("departure_date"),
                    rs.getString("arrival_date")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            showAddFlightDialog();
        } else if (e.getSource() == editButton) {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showEditFlightDialog(selectedRow);
        } else if (e.getSource() == deleteButton) {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteFlight(selectedRow);
        }
    }

    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add Flight", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JTextField flightNameField = new JTextField();
        JTextField flightNumberField = new JTextField();
        JComboBox<String> fromBox = new JComboBox<>();
        JComboBox<String> toBox = new JComboBox<>();
        JTextField travelDateField = new JTextField("yyyy-MM-dd");
        JTextField availableSeatsField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField journeyTimeField = new JTextField();
        JTextField departureDateField = new JTextField("yyyy-MM-dd");
        JTextField arrivalDateField = new JTextField("yyyy-MM-dd");

        // Load airports
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT airport_id, city FROM airport ORDER BY city");
            while (rs.next()) {
                String item = rs.getInt("airport_id") + ": " + rs.getString("city");
                fromBox.addItem(item);
                toBox.addItem(item);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        dialog.add(new JLabel("Flight Name:")); dialog.add(flightNameField);
        dialog.add(new JLabel("Flight Number:")); dialog.add(flightNumberField);
        dialog.add(new JLabel("From (Airport):")); dialog.add(fromBox);
        dialog.add(new JLabel("To (Airport):")); dialog.add(toBox);
        dialog.add(new JLabel("Travel Date (yyyy-MM-dd):")); dialog.add(travelDateField);
        dialog.add(new JLabel("Available Seats:")); dialog.add(availableSeatsField);
        dialog.add(new JLabel("Price:")); dialog.add(priceField);
        dialog.add(new JLabel("Journey Time (e.g. 2h 30m):")); dialog.add(journeyTimeField);
        dialog.add(new JLabel("Departure Date (yyyy-MM-dd):")); dialog.add(departureDateField);
        dialog.add(new JLabel("Arrival Date (yyyy-MM-dd):")); dialog.add(arrivalDateField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(saveBtn); dialog.add(cancelBtn);

        saveBtn.addActionListener(ev -> {
            try {
                String flightName = flightNameField.getText().trim();
                String flightNumber = flightNumberField.getText().trim();
                int fromId = Integer.parseInt(fromBox.getSelectedItem().toString().split(":")[0]);
                int toId = Integer.parseInt(toBox.getSelectedItem().toString().split(":")[0]);
                String travelDate = travelDateField.getText().trim();
                int availableSeats = Integer.parseInt(availableSeatsField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                String journeyTime = journeyTimeField.getText().trim();
                String departureDate = departureDateField.getText().trim();
                String arrivalDate = arrivalDateField.getText().trim();

                String sql = "INSERT INTO flight (flight_name, flight_number, departure_airport_id, arrival_airport_id, travel_date, available_seats, price, journey_time, departure_date, arrival_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, flightName);
                stmt.setString(2, flightNumber);
                stmt.setInt(3, fromId);
                stmt.setInt(4, toId);
                stmt.setString(5, travelDate);
                stmt.setInt(6, availableSeats);
                stmt.setDouble(7, price);
                stmt.setString(8, journeyTime);
                stmt.setString(9, departureDate);
                stmt.setString(10, arrivalDate);
                int result = stmt.executeUpdate();
                stmt.close();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "Flight added successfully!");
                    dialog.dispose();
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add flight.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditFlightDialog(int selectedRow) {
        JDialog dialog = new JDialog(this, "Edit Flight", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        // Get selected flight data
        int flightId = (int) tableModel.getValueAt(selectedRow, 0);
        String flightName = (String) tableModel.getValueAt(selectedRow, 1);
        String flightNumber = (String) tableModel.getValueAt(selectedRow, 2);
        String fromCity = (String) tableModel.getValueAt(selectedRow, 3);
        String toCity = (String) tableModel.getValueAt(selectedRow, 4);
        Date travelDate = (Date) tableModel.getValueAt(selectedRow, 5);
        int availableSeats = (int) tableModel.getValueAt(selectedRow, 6);
        double price = (double) tableModel.getValueAt(selectedRow, 7);
        String journeyTime = (String) tableModel.getValueAt(selectedRow, 8);
        String departureDate = (String) tableModel.getValueAt(selectedRow, 9);
        String arrivalDate = (String) tableModel.getValueAt(selectedRow, 10);

        JTextField flightNameField = new JTextField(flightName);
        JTextField flightNumberField = new JTextField(flightNumber);
        JComboBox<String> fromBox = new JComboBox<>();
        JComboBox<String> toBox = new JComboBox<>();
        JTextField travelDateField = new JTextField(travelDate.toString());
        JTextField availableSeatsField = new JTextField(String.valueOf(availableSeats));
        JTextField priceField = new JTextField(String.valueOf(price));
        JTextField journeyTimeField = new JTextField(journeyTime);
        JTextField departureDateField = new JTextField(departureDate);
        JTextField arrivalDateField = new JTextField(arrivalDate);

        // Load airports
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT airport_id, city FROM airport ORDER BY city");
            while (rs.next()) {
                String item = rs.getInt("airport_id") + ": " + rs.getString("city");
                fromBox.addItem(item);
                toBox.addItem(item);
                // Set selected airports
                if (rs.getString("city").equals(fromCity)) {
                    fromBox.setSelectedItem(item);
                }
                if (rs.getString("city").equals(toCity)) {
                    toBox.setSelectedItem(item);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        dialog.add(new JLabel("Flight Name:")); dialog.add(flightNameField);
        dialog.add(new JLabel("Flight Number:")); dialog.add(flightNumberField);
        dialog.add(new JLabel("From (Airport):")); dialog.add(fromBox);
        dialog.add(new JLabel("To (Airport):")); dialog.add(toBox);
        dialog.add(new JLabel("Travel Date (yyyy-MM-dd):")); dialog.add(travelDateField);
        dialog.add(new JLabel("Available Seats:")); dialog.add(availableSeatsField);
        dialog.add(new JLabel("Price:")); dialog.add(priceField);
        dialog.add(new JLabel("Journey Time (e.g. 2h 30m):")); dialog.add(journeyTimeField);
        dialog.add(new JLabel("Departure Date (yyyy-MM-dd):")); dialog.add(departureDateField);
        dialog.add(new JLabel("Arrival Date (yyyy-MM-dd):")); dialog.add(arrivalDateField);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(saveBtn); dialog.add(cancelBtn);

        saveBtn.addActionListener(ev -> {
            try {
                String newFlightName = flightNameField.getText().trim();
                String newFlightNumber = flightNumberField.getText().trim();
                int newFromId = Integer.parseInt(fromBox.getSelectedItem().toString().split(":")[0]);
                int newToId = Integer.parseInt(toBox.getSelectedItem().toString().split(":")[0]);
                String newTravelDate = travelDateField.getText().trim();
                int newAvailableSeats = Integer.parseInt(availableSeatsField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                String newJourneyTime = journeyTimeField.getText().trim();
                String newDepartureDate = departureDateField.getText().trim();
                String newArrivalDate = arrivalDateField.getText().trim();

                String sql = "UPDATE flight SET flight_name=?, flight_number=?, departure_airport_id=?, arrival_airport_id=?, " +
                           "travel_date=?, available_seats=?, price=?, journey_time=?, departure_date=?, arrival_date=? " +
                           "WHERE flight_id=?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, newFlightName);
                stmt.setString(2, newFlightNumber);
                stmt.setInt(3, newFromId);
                stmt.setInt(4, newToId);
                stmt.setString(5, newTravelDate);
                stmt.setInt(6, newAvailableSeats);
                stmt.setDouble(7, newPrice);
                stmt.setString(8, newJourneyTime);
                stmt.setString(9, newDepartureDate);
                stmt.setString(10, newArrivalDate);
                stmt.setInt(11, flightId);
                
                int result = stmt.executeUpdate();
                stmt.close();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "Flight updated successfully!");
                    dialog.dispose();
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update flight.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteFlight(int selectedRow) {
        int flightId = (int) tableModel.getValueAt(selectedRow, 0);
        String flightNumber = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete flight " + flightNumber + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First check if there are any bookings for this flight
                String checkSql = "SELECT COUNT(*) FROM booking WHERE flight_id = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                checkStmt.setInt(1, flightId);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int bookingCount = rs.getInt(1);
                rs.close();
                checkStmt.close();

                if (bookingCount > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot delete flight. There are existing bookings for this flight.",
                        "Deletion Not Allowed",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // If no bookings exist, proceed with deletion
                String sql = "DELETE FROM flight WHERE flight_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, flightId);
                int result = stmt.executeUpdate();
                stmt.close();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete flight.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting flight: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageFlights::new);
    }
} 