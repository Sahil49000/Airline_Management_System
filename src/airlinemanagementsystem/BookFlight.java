package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

public class BookFlight extends JFrame implements ActionListener {

    private JTextField tfaadhar;
    private JLabel tfname, tfnationality, tfaddress, labelgender, tfphone;
    private JButton bookFlightButton, fetchButton, searchButton, clearButton, backButton;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private int passengerId = -1;
    private JComboBox<String> departureCity, arrivalCity;
    private JSpinner dateSpinner;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public BookFlight() {
        try {
            // Set up the frame first
            setLayout(null);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Initialize database connection
            DatabaseConnection dbConnection = new DatabaseConnection();
            connection = dbConnection.getConnection();
            
            // Set up UI components
            setupUI();
            loadAirportData();
            viewAvailableFlights();
            
            // Make the frame visible
            setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage() + "\nPlease ensure the database is running and accessible.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error initializing BookFlight: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void setupUI() {
        try {
            getContentPane().setBackground(new Color(235, 245, 255));
            setLayout(null);
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Header Panel
            JPanel headerPanel = new JPanel();
            headerPanel.setBounds(0, 0, 1600, 80);
            headerPanel.setBackground(new Color(0, 120, 215));
            headerPanel.setLayout(null);
            add(headerPanel);

            backButton = createButton("Back", new Color(0, 100, 180));
            backButton.setBounds(20, 20, 100, 35);
            backButton.addActionListener(e -> {
                dispose();
            });
            headerPanel.add(backButton);

            JLabel heading = new JLabel("Book Flight");
            heading.setBounds(700, 20, 500, 35);
            heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
            heading.setForeground(Color.WHITE);
            headerPanel.add(heading);

            // Search Panel
            JPanel searchPanel = new JPanel();
            searchPanel.setBounds(60, 100, 1400, 100);
            searchPanel.setBackground(new Color(235, 245, 255));
            searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
            add(searchPanel);

            searchPanel.add(new JLabel("Search:"));
            searchField = new JTextField(20);
            searchPanel.add(searchField);

            searchButton = createButton("Search", new Color(0, 120, 215));
            searchPanel.add(searchButton);
            searchButton.addActionListener(this);

            clearButton = createButton("Clear", new Color(100, 100, 100));
            searchPanel.add(clearButton);
            clearButton.addActionListener(this);

            // Filter Panel
            JPanel filterPanel = new JPanel();
            filterPanel.setBounds(60, 220, 1400, 60);
            filterPanel.setBackground(new Color(235, 245, 255));
            filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
            add(filterPanel);

            filterPanel.add(new JLabel("From:"));
            departureCity = new JComboBox<>();
            filterPanel.add(departureCity);

            filterPanel.add(new JLabel("To:"));
            arrivalCity = new JComboBox<>();
            filterPanel.add(arrivalCity);

            filterPanel.add(new JLabel("Date:"));
            SpinnerDateModel dateModel = new SpinnerDateModel();
            dateSpinner = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            dateSpinner.setPreferredSize(new Dimension(150, 30));
            filterPanel.add(dateSpinner);

            // Passenger Details Panel
            JPanel passengerPanel = new JPanel();
            passengerPanel.setBounds(60, 300, 1400, 200);
            passengerPanel.setBackground(new Color(235, 245, 255));
            passengerPanel.setLayout(null);
            add(passengerPanel);

            JLabel aadharLabel = new JLabel("Aadhar:");
            aadharLabel.setBounds(20, 10, 80, 30);
            aadharLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            passengerPanel.add(aadharLabel);

            tfaadhar = new JTextField();
            tfaadhar.setBounds(100, 10, 200, 30);
            tfaadhar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            passengerPanel.add(tfaadhar);

            fetchButton = createButton("Fetch User", new Color(0, 150, 0));
            fetchButton.setBounds(320, 10, 120, 30);
            passengerPanel.add(fetchButton);
            fetchButton.addActionListener(this);

            tfname = createDetailLabel("Name", 50, passengerPanel);
            tfnationality = createDetailLabel("Nationality", 90, passengerPanel);
            tfaddress = createDetailLabel("Address", 130, passengerPanel);
            labelgender = createDetailLabel("Gender", 170, passengerPanel);
            tfphone = createDetailLabel("Phone", 210, passengerPanel);

            // Flights Table
            String[] columnNames = {
                "Flight ID", "Flight Name", "Flight Number", "From", "To",
                "Travel Date", "Available Seats", "Price", "Journey Time", "Departure Time", "Arrival Time"
            };
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            flightsTable = new JTable(tableModel);
            flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            flightsTable.setRowHeight(30);

            sorter = new TableRowSorter<>(tableModel);
            flightsTable.setRowSorter(sorter);
            flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scrollPane = new JScrollPane(flightsTable);
            scrollPane.setBounds(60, 520, 1400, 300);
            add(scrollPane);

            // Book Button
            bookFlightButton = createButton("Book Selected Flight", new Color(0, 120, 215));
            bookFlightButton.setBounds(700, 840, 200, 40);
            bookFlightButton.addActionListener(this);
            add(bookFlightButton);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error setting up UI: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAirportData() {
        try {
            String query = "SELECT DISTINCT city FROM airport ORDER BY city";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            Vector<String> cities = new Vector<>();
            cities.add("All Cities");
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }

            departureCity.setModel(new DefaultComboBoxModel<>(cities));
            arrivalCity.setModel(new DefaultComboBoxModel<>(cities));

            departureCity.addActionListener(e -> filterFlights());
            arrivalCity.addActionListener(e -> filterFlights());
            dateSpinner.addChangeListener(e -> filterFlights());

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Error loading airport data: " + e.getMessage());
        }
    }

    private void filterFlights() {
        String departure = (String) departureCity.getSelectedItem();
        String arrival = (String) arrivalCity.getSelectedItem();
        Date date = (Date) dateSpinner.getValue();

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean match = true;

                if (!"All Cities".equals(departure)) {
                    match = match && entry.getStringValue(3).equals(departure);
                }
                if (!"All Cities".equals(arrival)) {
                    match = match && entry.getStringValue(4).equals(arrival);
                }
                if (date != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    match = match && entry.getStringValue(5).equals(sdf.format(date));
                }

                return match;
            }
        };

        sorter.setRowFilter(filter);
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

    private JLabel createDetailLabel(String text, int y, JPanel panel) {
        JLabel label = new JLabel(text + ":");
        label.setBounds(20, y, 100, 25);
        panel.add(label);

        JLabel valueLabel = new JLabel();
        valueLabel.setBounds(100, y, 250, 25);
        panel.add(valueLabel);
        return valueLabel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == fetchButton) {
            fetchUserDetails();
        } else if (ae.getSource() == bookFlightButton) {
            bookFlight();
        } else if (ae.getSource() == searchButton) {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        } else if (ae.getSource() == clearButton) {
            searchField.setText("");
            sorter.setRowFilter(null);
        }
    }

    private void fetchUserDetails() {
        String aadhar = tfaadhar.getText().trim();
        if (aadhar.isEmpty() || !aadhar.matches("\\d{12}")) {
            showMessage("Please enter a valid 12-digit Aadhar number.");
            return;
        }

        try {
            String query = "SELECT * FROM passenger WHERE aadhar = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, aadhar);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                passengerId = rs.getInt("passenger_id");
                tfname.setText(rs.getString("name"));
                tfnationality.setText(rs.getString("nationality"));
                tfaddress.setText(rs.getString("address"));
                labelgender.setText(rs.getString("gender"));
                tfphone.setText(rs.getString("phone"));
            } else {
                showMessage("No user found with this Aadhar number. Please register first.");
                clearPassengerDetails();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error: " + e.getMessage());
        }
    }

    private void clearPassengerDetails() {
        passengerId = -1;
        tfname.setText("");
        tfnationality.setText("");
        tfaddress.setText("");
        labelgender.setText("");
        tfphone.setText("");
    }

    private void viewAvailableFlights() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT f.*, d.city as departure_city, a.city as arrival_city "
                    + "FROM flight f "
                    + "JOIN airport d ON f.departure_airport_id = d.airport_id "
                    + "JOIN airport a ON f.arrival_airport_id = a.airport_id "
                    + "WHERE f.available_seats > 0 "
                    + "AND f.travel_date >= CURDATE() "
                    + "ORDER BY f.travel_date ASC, f.departure_date ASC "
                    + "LIMIT 50";  // Limit to 50 latest flights

            System.out.println("Executing query: " + query);

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Date travelDate = rs.getDate("travel_date");
                Time departureTime = rs.getTime("departure_date");
                Time arrivalTime = rs.getTime("arrival_date");

                // Format the date and time for better display
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                tableModel.addRow(new Object[]{
                    rs.getInt("flight_id"),
                    rs.getString("flight_name"),
                    rs.getString("flight_number"),
                    rs.getString("departure_city"),
                    rs.getString("arrival_city"),
                    dateFormat.format(travelDate),
                    rs.getInt("available_seats"),
                    String.format("₹%.2f", rs.getDouble("price")),
                    rs.getString("journey_time"),
                    timeFormat.format(departureTime),
                    timeFormat.format(arrivalTime)
                });
            }

            System.out.println("Found " + rowCount + " available flights");

            if (rowCount == 0) {
                showMessage("No available flights found for the next few days. Please try again later.");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error while loading flights: " + e.getMessage());
        }
    }

    private void bookFlight() {
        if (passengerId == -1) {
            showMessage("Please fetch passenger details first.");
            return;
        }

        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select a flight to book.");
            return;
        }

        int flightId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        int availableSeats = Integer.parseInt(tableModel.getValueAt(selectedRow, 6).toString());

        if (availableSeats <= 0) {
            showMessage("Sorry, no seats available for this flight.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm booking for flight " + tableModel.getValueAt(selectedRow, 2) + "?",
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (updateAvailableSeats(flightId)) {
                String pnr = generatePNR();
                String seatNumber = generateSeatNumber();

                if (insertBooking(flightId, pnr, seatNumber)) {
                    showMessage("Booking successful!\nPNR: " + pnr + "\nSeat Number: " + seatNumber);
                    viewAvailableFlights(); // Refresh the table
                } else {
                    showMessage("Failed to create booking. Please try again.");
                }
            } else {
                showMessage("Failed to update available seats. Please try again.");
            }
        }
    }

    private boolean updateAvailableSeats(int flightId) {
        try {
            String query = "UPDATE flight SET available_seats = available_seats - 1 WHERE flight_id = ? AND available_seats > 0";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, flightId);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Error updating available seats: " + e.getMessage());
            return false;
        }
    }

    private boolean insertBooking(int flightId, String pnr, String seatNumber) {
        try {
            String query = "INSERT INTO booking (flight_id, passenger_id, pnr_number, seat_number, booking_status) VALUES (?, ?, ?, ?, 'Confirmed')";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, flightId);
            stmt.setInt(2, passengerId);
            stmt.setString(3, pnr);
            stmt.setString(4, seatNumber);
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Error inserting booking: " + e.getMessage());
            return false;
        }
    }

    private String generatePNR() {
        return "PNR" + System.currentTimeMillis();
    }

    private String generateSeatNumber() {
        return "S" + ((int) (Math.random() * 100) + 1);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
