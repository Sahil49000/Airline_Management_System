package airlinemanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class Cancel extends JFrame implements ActionListener {
    private JTextField tfpnr, tfaadhar;
    private JLabel tfname, cancellationno, lblFlightCodeValue, lblTravelDateValue;
    private JLabel lblArrivalDate, lblArrivalPlace, lblDepartureDate, lblDeparturePlace, lblPrice;
    private JLabel lblJourneyTime, lblFlightNumber, lblGender, lblPhone, lblNationality;
    private JButton fetchButton, cancelButton, clearButton;
    private JPanel detailsPanel;
    private Connection connection;
    private int bookingId = -1;
    private double refundAmount = 0.0;

    public Cancel() {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            connection = dbConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setupUI();
    }

    private void setupUI() {
        setTitle("Flight Cancellation System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(235, 245, 255));
        setLayout(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 1000, 80);
        headerPanel.setBackground(new Color(0, 120, 215));
        headerPanel.setLayout(null);
        add(headerPanel);

        JLabel heading = new JLabel("Flight Cancellation");
        heading.setBounds(380, 20, 400, 40);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);

        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(60, 100, 880, 100);
        searchPanel.setBackground(new Color(235, 245, 255));
        searchPanel.setLayout(null);
        add(searchPanel);

        searchPanel.add(createLabel("Aadhar Number:", 20, 20));
        tfaadhar = createTextField(180, 20);
        searchPanel.add(tfaadhar);

        searchPanel.add(createLabel("PNR Number:", 420, 20));
        tfpnr = createTextField(580, 20);
        searchPanel.add(tfpnr);

        fetchButton = createButton("Search Booking", Color.BLUE);
        fetchButton.setBounds(580, 65, 150, 30);
        fetchButton.addActionListener(this);
        searchPanel.add(fetchButton);

        clearButton = createButton("Clear", Color.GRAY);
        clearButton.setBounds(750, 65, 100, 30);
        clearButton.addActionListener(this);
        searchPanel.add(clearButton);

        detailsPanel = new JPanel();
        detailsPanel.setBounds(60, 220, 880, 450);
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(null);
        detailsPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
        add(detailsPanel);

        int leftX = 20, rightX = 460, valueXOffset = 160, y = 20, dy = 40;
        String[] labels = {"Passenger Name:", "Flight Code:", "Travel Date:", "Arrival Date:", "Arrival Place:",
            "Departure Date:", "Departure Place:", "Price:", "Journey Time:", "Flight Number:", "Gender:", "Phone:", "Nationality:"};

        JLabel[] values = {
            tfname = new JLabel(), lblFlightCodeValue = new JLabel(), lblTravelDateValue = new JLabel(),
            lblArrivalDate = new JLabel(), lblArrivalPlace = new JLabel(), lblDepartureDate = new JLabel(),
            lblDeparturePlace = new JLabel(), lblPrice = new JLabel(), lblJourneyTime = new JLabel(),
            lblFlightNumber = new JLabel(), lblGender = new JLabel(), lblPhone = new JLabel(),
            lblNationality = new JLabel()
        };

        for (int i = 0; i < labels.length; i++) {
            int x = i % 2 == 0 ? leftX : rightX;
            int vx = x + valueXOffset;
            int cy = y + (i / 2) * dy;

            JLabel label = createLabel(labels[i], x, cy);
            JLabel value = values[i];
            value.setBounds(vx, cy, 200, 25);
            value.setFont(new Font("Arial", Font.PLAIN, 16));
            value.setForeground(Color.BLACK);

            detailsPanel.add(label);
            detailsPanel.add(value);
        }

        cancellationno = new JLabel();
        cancellationno.setBounds(20, y + 7 * dy, 400, 30);
        cancellationno.setFont(new Font("Arial", Font.BOLD, 18));
        cancellationno.setForeground(Color.RED);
        detailsPanel.add(cancellationno);

        cancelButton = createButton("Cancel Booking", Color.RED);
        cancelButton.setBounds(380, y + 9 * dy, 200, 40);
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(this);
        detailsPanel.add(cancelButton);

        detailsPanel.setVisible(false);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 160, 25);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(0, 51, 102));
        return label;
    }

    private JTextField createTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 200, 35);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 51, 102), 2),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == fetchButton) searchBooking();
        else if (ae.getSource() == cancelButton) cancelBooking();
        else if (ae.getSource() == clearButton) clearFields();
    }

    private void searchBooking() {
        String pnr = tfpnr.getText().trim();
        String aadhar = tfaadhar.getText().trim();

        if (pnr.isEmpty() || aadhar.isEmpty()) {
            showMessage("Please enter both PNR and Aadhar number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!aadhar.matches("\\d{12}")) {
            showMessage("Please enter a valid 12-digit Aadhar number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "SELECT b.booking_id, f.flight_name, f.flight_number, f.departure_date, f.arrival_date, " +
                    "a1.city AS departure, a2.city AS arrival, f.price, f.journey_time, " +
                    "p.name, p.nationality, p.gender, p.phone, b.booking_date " +
                    "FROM booking b " +
                    "JOIN flight f ON b.flight_id = f.flight_id " +
                    "JOIN passenger p ON b.passenger_id = p.passenger_id " +
                    "JOIN airport a1 ON f.departure_airport_id = a1.airport_id " +
                    "JOIN airport a2 ON f.arrival_airport_id = a2.airport_id " +
                    "WHERE b.pnr_number = ? AND p.aadhar = ? AND b.booking_status = 'Confirmed'";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, pnr);
            ps.setString(2, aadhar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bookingId = rs.getInt("booking_id");
                tfname.setText(rs.getString("name"));
                lblFlightCodeValue.setText(rs.getString("flight_name"));
                lblTravelDateValue.setText(formatDate(rs.getTimestamp("booking_date")));
                lblArrivalDate.setText(formatDate(rs.getTimestamp("arrival_date")));
                lblDepartureDate.setText(formatDate(rs.getTimestamp("departure_date")));
                lblArrivalPlace.setText(rs.getString("arrival"));
                lblDeparturePlace.setText(rs.getString("departure"));
                double price = rs.getDouble("price");
                lblPrice.setText("₹" + String.format("%.2f", price));
                lblJourneyTime.setText(rs.getString("journey_time"));
                lblFlightNumber.setText(rs.getString("flight_number"));
                lblGender.setText(rs.getString("gender"));
                lblPhone.setText(rs.getString("phone"));
                lblNationality.setText(rs.getString("nationality"));

                Date departureDate = rs.getTimestamp("departure_date");
                long hoursUntilDeparture = (departureDate.getTime() - System.currentTimeMillis()) / (60 * 60 * 1000);
                refundAmount = hoursUntilDeparture > 24 ? price * 0.8 : price * 0.5;

                cancellationno.setText(String.format("Estimated Refund: ₹%.2f", refundAmount));
                cancelButton.setEnabled(true);
                detailsPanel.setVisible(true);
            } else {
                showMessage("No active booking found with the provided details.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Are you sure you want to cancel this booking?\nEstimated refund amount: ₹%.2f", refundAmount),
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                connection.setAutoCommit(false);

                // Update booking status
                String updateQuery = "UPDATE booking SET booking_status = 'Cancelled' WHERE booking_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, bookingId);

                if (updateStmt.executeUpdate() > 0) {
                    // Update available seats in flight
                    String updateSeatsQuery = "UPDATE flight f " +
                            "JOIN booking b ON f.flight_id = b.flight_id " +
                            "SET f.available_seats = f.available_seats + 1 " +
                            "WHERE b.booking_id = ?";
                    PreparedStatement seatsStmt = connection.prepareStatement(updateSeatsQuery);
                    seatsStmt.setInt(1, bookingId);
                    seatsStmt.executeUpdate();
                    seatsStmt.close();
 
                    // Record refund
                    String refundQuery = "INSERT INTO payment (booking_id, amount, payment_method) VALUES (?, ?, 'Refund')";
                    PreparedStatement refundStmt = connection.prepareStatement(refundQuery);
                    refundStmt.setInt(1, bookingId);
                    refundStmt.setDouble(2, refundAmount);

                    if (refundStmt.executeUpdate() > 0) {
                        connection.commit();
                        showMessage("Booking cancelled successfully!\nRefund amount: ₹" + String.format("%.2f", refundAmount), "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } else {
                        connection.rollback();
                        showMessage("Failed to process refund.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refundStmt.close();
                } else {
                    connection.rollback();
                    showMessage("Failed to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                updateStmt.close();
                connection.setAutoCommit(true);

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                showMessage("Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        tfaadhar.setText("");
        tfpnr.setText("");
        bookingId = -1;
        refundAmount = 0.0;
        cancelButton.setEnabled(false);
        detailsPanel.setVisible(false);
        tfaadhar.requestFocus();
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Cancel::new);
    }
}
