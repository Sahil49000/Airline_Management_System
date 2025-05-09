package airlinemanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class JourneyDetails extends JFrame implements ActionListener {
    private JTextField pnrField, aadharField;
    private JButton showButton, backButton, refreshButton;
    private JPanel detailsPanel;
    private JScrollPane scrollPane;
    private JRadioButton pnrOption, aadharOption;

    public JourneyDetails() {
        setTitle("Journey Details");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 245, 255));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topPanel.setBackground(new Color(0, 120, 215));

        backButton = createButton("Back", new Color(0, 100, 180));
        backButton.addActionListener(e -> {
            // 🟡 Ensure Main() creates a visible frame
            dispose();
        });
        topPanel.add(backButton);

        JLabel titleLabel = new JLabel("Journey Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        topPanel.add(Box.createHorizontalStrut(50));

        JLabel searchLabel = new JLabel("Search by:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(Color.WHITE);
        topPanel.add(searchLabel);

        pnrOption = new JRadioButton("PNR", true);
        aadharOption = new JRadioButton("Aadhar");
        pnrOption.setForeground(Color.WHITE);
        aadharOption.setForeground(Color.WHITE);
        ButtonGroup group = new ButtonGroup();
        group.add(pnrOption);
        group.add(aadharOption);
        topPanel.add(pnrOption);
        topPanel.add(aadharOption);

        pnrField = new JTextField(15);
        aadharField = new JTextField(15);
        aadharField.setVisible(false);
        pnrField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aadharField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        topPanel.add(pnrField);
        topPanel.add(aadharField);

        showButton = createButton("Show Details", new Color(0, 150, 0));
        topPanel.add(showButton);

        refreshButton = createButton("Refresh", new Color(0, 120, 215));
        refreshButton.addActionListener(e -> refreshDetails());
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(235, 245, 255));

        scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Add listeners
        showButton.addActionListener(this);
        pnrOption.addActionListener(e -> toggleInputFields(true));
        aadharOption.addActionListener(e -> toggleInputFields(false));
        
        // ✅ Always make frame visible if called directly
        setVisible(true);
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

    private void toggleInputFields(boolean isPnrSelected) {
        pnrField.setVisible(isPnrSelected);
        aadharField.setVisible(!isPnrSelected);
        if (isPnrSelected) {
            pnrField.requestFocus();
        } else {
            aadharField.requestFocus();
        }
        revalidate();
        repaint();
    }

    private void refreshDetails() {
        detailsPanel.removeAll();
        detailsPanel.revalidate();
        detailsPanel.repaint();
        pnrField.setText("");
        aadharField.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String searchValue;
        String searchType = pnrOption.isSelected() ? "PNR" : "Aadhar";

        if (pnrOption.isSelected()) {
            searchValue = pnrField.getText().trim();
            if (searchValue.isEmpty()) {
                showMessage("Please enter a PNR number.");
                return;
            }
        } else {
            searchValue = aadharField.getText().trim();
            if (searchValue.isEmpty()) {
                showMessage("Please enter an Aadhar number.");
                return;
            }
            if (!searchValue.matches("\\d{12}")) {
                showMessage("Please enter a valid 12-digit Aadhar number.");
                return;
            }
        }

        detailsPanel.removeAll();

        try {
            String query = buildQuery();
            DatabaseConnection dbConnection = new DatabaseConnection();
            Connection conn = dbConnection.getConnection();
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, searchValue);
                ResultSet rs = ps.executeQuery();

                if (!rs.isBeforeFirst()) {
                    showMessage("No booking information found for the given " + searchType);
                    return;
                }

                while (rs.next()) {
                    JPanel card = createDetailCard(rs);
                    detailsPanel.add(card);
                    detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                }

                detailsPanel.revalidate();
                detailsPanel.repaint();
                rs.close();
                ps.close();
                conn.close();
            } else {
                showMessage("Database connection failed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildQuery() {
        return "SELECT b.pnr_number, b.booking_date, b.seat_number, b.booking_status, " +
                "f.flight_number, f.flight_name, f.travel_date, f.journey_time, f.price, f.departure_date, f.arrival_date, " +
                "p.name, p.aadhar, p.nationality, p.gender, p.phone, p.email " +
                "FROM booking b " +
                "JOIN passenger p ON b.passenger_id = p.passenger_id " +
                "JOIN flight f ON b.flight_id = f.flight_id " +
                (pnrOption.isSelected() ? "WHERE b.pnr_number = ?" : "WHERE p.aadhar = ?");
    }

    private JPanel createDetailCard(ResultSet rs) throws SQLException {
        JPanel card = new JPanel(new GridLayout(0, 4, 20, 20));
        card.setBackground(new Color(240, 248, 255));
        card.setPreferredSize(new Dimension(1200, 400));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        card.add(createLabel("PNR:", labelFont));
        card.add(createValue(rs.getString("pnr_number"), valueFont));
        card.add(createLabel("Booking Date:", labelFont));
        card.add(createValue(rs.getString("booking_date"), valueFont));

        card.add(createLabel("Seat Number:", labelFont));
        card.add(createValue(rs.getString("seat_number"), valueFont));
        card.add(createLabel("Booking Status:", labelFont));
        card.add(createValue(rs.getString("booking_status"), valueFont));

        card.add(createLabel("Passenger Name:", labelFont));
        card.add(createValue(rs.getString("name"), valueFont));
        card.add(createLabel("Aadhar:", labelFont));
        card.add(createValue(rs.getString("aadhar"), valueFont));

        card.add(createLabel("Nationality:", labelFont));
        card.add(createValue(rs.getString("nationality"), valueFont));
        card.add(createLabel("Gender:", labelFont));
        card.add(createValue(rs.getString("gender"), valueFont));

        card.add(createLabel("Phone:", labelFont));
        card.add(createValue(rs.getString("phone"), valueFont));
        card.add(createLabel("Email:", labelFont));
        card.add(createValue(rs.getString("email"), valueFont));

        card.add(createLabel("Flight Number:", labelFont));
        card.add(createValue(rs.getString("flight_number"), valueFont));
        card.add(createLabel("Flight Name:", labelFont));
        card.add(createValue(rs.getString("flight_name"), valueFont));

        card.add(createLabel("Price:", labelFont));
        card.add(createValue("₹" + rs.getDouble("price"), valueFont));
        card.add(createLabel("Journey Time:", labelFont));
        card.add(createValue(rs.getString("journey_time"), valueFont));

        card.add(createLabel("Departure Date:", labelFont));
        card.add(createValue(rs.getString("departure_date"), valueFont));
        card.add(createLabel("Arrival Date:", labelFont));
        card.add(createValue(rs.getString("arrival_date"), valueFont));

        return card;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(0, 51, 102));
        return label;
    }

    private JLabel createValue(String text, Font font) {
        JLabel value = new JLabel(text);
        value.setFont(font);
        value.setForeground(Color.DARK_GRAY);
        return value;
    }

    private void showMessage(String message) {
        showMessage(message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JourneyDetails().setVisible(true));
    }
}
