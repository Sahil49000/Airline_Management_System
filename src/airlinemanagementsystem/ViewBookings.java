package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewBookings extends JFrame implements ActionListener {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, refreshButton, backButton;
    private Connection connection;

    public ViewBookings() {
        setTitle("View Bookings");
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

        JLabel heading = new JLabel("View Bookings");
        heading.setBounds(700, 20, 500, 35);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);

        backButton = createButton("Back", new Color(0, 100, 180));
        backButton.setBounds(20, 20, 100, 35);
        backButton.addActionListener(e -> dispose());
        headerPanel.add(backButton);

        searchField = new JTextField(30);
        searchField.setBounds(60, 100, 300, 35);
        add(searchField);

        searchButton = createButton("Search", new Color(0, 120, 215));
        searchButton.setBounds(380, 100, 120, 35);
        searchButton.addActionListener(this);
        add(searchButton);

        refreshButton = createButton("Refresh", new Color(100, 100, 100));
        refreshButton.setBounds(520, 100, 120, 35);
        refreshButton.addActionListener(e -> loadBookings(""));
        add(refreshButton);

        String[] columnNames = {"Booking ID", "PNR", "Passenger Name", "Flight Number", "Flight Name", "Status", "Booking Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookingsTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(60, 160, 1400, 500);
        add(scrollPane);

        loadBookings("");
        setVisible(true);
    }

    private void loadBookings(String search) {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT b.booking_id, b.pnr_number, p.name, f.flight_number, f.flight_name, b.booking_status, b.booking_date " +
                    "FROM booking b JOIN passenger p ON b.passenger_id = p.passenger_id " +
                    "JOIN flight f ON b.flight_id = f.flight_id ";
            if (search != null && !search.trim().isEmpty()) {
                query += "WHERE b.pnr_number LIKE ? OR p.name LIKE ? OR f.flight_number LIKE ? ";
            }
            query += "ORDER BY b.booking_date DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            if (search != null && !search.trim().isEmpty()) {
                String like = "%" + search.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("booking_id"),
                    rs.getString("pnr_number"),
                    rs.getString("name"),
                    rs.getString("flight_number"),
                    rs.getString("flight_name"),
                    rs.getString("booking_status"),
                    rs.getTimestamp("booking_date")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        if (e.getSource() == searchButton) {
            loadBookings(searchField.getText());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewBookings::new);
    }
} 