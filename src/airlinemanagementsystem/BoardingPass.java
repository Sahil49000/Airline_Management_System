package airlinemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.border.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoardingPass extends JFrame implements ActionListener {
    private JTextField tfpnr;
    private JLabel bookingIdLabel, flightIdLabel, passengerIdLabel, bookingDateLabel, seatNumberLabel, bookingStatusLabel;
    private JLabel nameLabel, aadharLabel, nationalityLabel, addressLabel, genderLabel, phoneLabel, emailLabel, dobLabel;
    private JLabel flightNameLabel, flightNumberLabel, departureTimeLabel, arrivalTimeLabel, departureCityLabel, arrivalCityLabel;
    private JButton fetchBtn, printBtn, clearBtn, backBtn;
    private JPanel detailsPanel, flightPanel, passengerPanel;
    private Connection connection;
    private String pnrNumber;
    private Image airlineLogo;

    public BoardingPass() {
        try {
            // Load airline logo with null check
            java.net.URL logoUrl = getClass().getResource("/airlinemanagementsystem/icons/airline_logo.png");
            if (logoUrl != null) {
                airlineLogo = ImageIO.read(logoUrl);
            } else {
                airlineLogo = null; // No logo, but don't crash
            }
            
            // Initialize database connection
            DatabaseConnection dbConnection = new DatabaseConnection();
            connection = dbConnection.getConnection();
            
            setupUI();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error initializing Boarding Pass: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void setupUI() {
        setTitle("Boarding Pass Generator");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(235, 245, 255));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 1000, 80);
        headerPanel.setBackground(new Color(0, 120, 215));
        headerPanel.setLayout(null);
        add(headerPanel);

        backBtn = createButton("Back", new Color(0, 100, 180));
        backBtn.setBounds(20, 20, 100, 35);
        backBtn.addActionListener(e -> dispose());
        headerPanel.add(backBtn);

        JLabel heading = new JLabel("Boarding Pass Generator");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        heading.setBounds(350, 20, 400, 40);
        headerPanel.add(heading);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(50, 100, 900, 100);
        searchPanel.setBackground(new Color(235, 245, 255));
        searchPanel.setLayout(null);
        add(searchPanel);

        JLabel lblpnr = new JLabel("Enter PNR Number:");
        lblpnr.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblpnr.setBounds(50, 30, 160, 25);
        searchPanel.add(lblpnr);

        tfpnr = new JTextField();
        tfpnr.setBounds(220, 30, 300, 35);
        tfpnr.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfpnr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 102), 2),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        searchPanel.add(tfpnr);

        fetchBtn = createButton("Search", new Color(0, 120, 215));
        fetchBtn.setBounds(540, 30, 120, 35);
        fetchBtn.addActionListener(this);
        searchPanel.add(fetchBtn);

        clearBtn = createButton("Clear", new Color(100, 100, 100));
        clearBtn.setBounds(680, 30, 120, 35);
        clearBtn.addActionListener(this);
        searchPanel.add(clearBtn);

        // Flight Details Panel
        flightPanel = createDetailsPanel("Flight Information", 50, 220, 900, 200);
        add(flightPanel);

        flightNameLabel = createField(flightPanel, "Flight Name", 20);
        flightNumberLabel = createField(flightPanel, "Flight Number", 60);
        departureTimeLabel = createField(flightPanel, "Departure Time", 100);
        arrivalTimeLabel = createField(flightPanel, "Arrival Time", 140);
        departureCityLabel = createField(flightPanel, "From", 20, 460);
        arrivalCityLabel = createField(flightPanel, "To", 60, 460);

        // Passenger Details Panel
        passengerPanel = createDetailsPanel("Passenger Information", 50, 440, 900, 300);
        add(passengerPanel);

        nameLabel = createField(passengerPanel, "Name", 20);
        aadharLabel = createField(passengerPanel, "Aadhar Number", 60);
        nationalityLabel = createField(passengerPanel, "Nationality", 100);
        addressLabel = createField(passengerPanel, "Address", 140);
        genderLabel = createField(passengerPanel, "Gender", 180);
        phoneLabel = createField(passengerPanel, "Phone", 220);
        emailLabel = createField(passengerPanel, "Email", 260);

        bookingIdLabel = createField(passengerPanel, "Booking ID", 20, 460);
        seatNumberLabel = createField(passengerPanel, "Seat Number", 60, 460);
        bookingStatusLabel = createField(passengerPanel, "Status", 100, 460);
        bookingDateLabel = createField(passengerPanel, "Booking Date", 140, 460);

        // Print Button
        printBtn = createButton("Print Boarding Pass", new Color(0, 150, 0));
        printBtn.setBounds(400, 760, 200, 40);
        printBtn.setEnabled(false);
        printBtn.addActionListener(this);
        add(printBtn);

        // Initially hide details panels
        flightPanel.setVisible(false);
        passengerPanel.setVisible(false);

        setResizable(false);
        setVisible(true);
    }

    private JPanel createDetailsPanel(String title, int x, int y, int width, int height) {
        JPanel panel = new JPanel(null);
        panel.setBounds(x, y, width, height);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(0, 120, 215)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    private JLabel createField(JPanel panel, String label, int y) {
        return createField(panel, label, y, 20);
    }

    private JLabel createField(JPanel panel, String label, int y, int x) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(x, y, 150, 25);
        panel.add(lbl);

        JLabel val = new JLabel();
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setBounds(x + 160, y, 300, 25);
        panel.add(val);

        return val;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fetchBtn) {
            searchBoardingPass();
        } else if (e.getSource() == clearBtn) {
            clearFields();
        } else if (e.getSource() == printBtn) {
            printBoardingPass();
        }
    }

    private void searchBoardingPass() {
        String pnr = tfpnr.getText().trim();

        if (pnr.isEmpty()) {
            showMessage("Please enter a PNR number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "SELECT b.*, f.flight_name, f.flight_number, f.departure_date, f.arrival_date, " +
                          "a1.city AS departure_city, a2.city AS arrival_city, " +
                          "p.name, p.aadhar, p.nationality, p.address, p.gender, p.phone, p.email " +
                          "FROM booking b " +
                          "JOIN flight f ON b.flight_id = f.flight_id " +
                          "JOIN passenger p ON b.passenger_id = p.passenger_id " +
                          "JOIN airport a1 ON f.departure_airport_id = a1.airport_id " +
                          "JOIN airport a2 ON f.arrival_airport_id = a2.airport_id " +
                          "WHERE b.pnr_number = ? AND b.booking_status = 'Confirmed'";

            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, pnr);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                pnrNumber = pnr;
                
                // Flight Details
                flightNameLabel.setText(rs.getString("flight_name"));
                flightNumberLabel.setText(rs.getString("flight_number"));
                departureTimeLabel.setText(formatDateTime(rs.getTimestamp("departure_date")));
                arrivalTimeLabel.setText(formatDateTime(rs.getTimestamp("arrival_date")));
                departureCityLabel.setText(rs.getString("departure_city"));
                arrivalCityLabel.setText(rs.getString("arrival_city"));

                // Passenger Details
                nameLabel.setText(rs.getString("name"));
                aadharLabel.setText(rs.getString("aadhar"));
                nationalityLabel.setText(rs.getString("nationality"));
                addressLabel.setText(rs.getString("address"));
                genderLabel.setText(rs.getString("gender"));
                phoneLabel.setText(rs.getString("phone"));
                emailLabel.setText(rs.getString("email"));

                // Booking Details
                bookingIdLabel.setText(String.valueOf(rs.getInt("booking_id")));
                seatNumberLabel.setText(rs.getString("seat_number"));
                bookingStatusLabel.setText(rs.getString("booking_status"));
                bookingDateLabel.setText(formatDateTime(rs.getTimestamp("booking_date")));

                flightPanel.setVisible(true);
                passengerPanel.setVisible(true);
                printBtn.setEnabled(true);
            } else {
                showMessage("No confirmed booking found for the provided PNR.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }

            rs.close();
            pst.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printBoardingPass() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new BoardingPassPrintable());
            
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
            showMessage("Error printing boarding pass: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class BoardingPassPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Set up the boarding pass layout
            int width = (int) pageFormat.getImageableWidth();
            int height = (int) pageFormat.getImageableHeight();
            
            // Draw airline logo if available
            if (airlineLogo != null) {
                g2d.drawImage(airlineLogo, 50, 50, 100, 100, null);
            }

            // Draw header
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("BOARDING PASS", width/2 - 100, 50);

            // Draw flight details
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Flight: " + flightNameLabel.getText() + " (" + flightNumberLabel.getText() + ")", 50, 200);
            g2d.drawString("From: " + departureCityLabel.getText(), 50, 230);
            g2d.drawString("To: " + arrivalCityLabel.getText(), 50, 260);
            g2d.drawString("Departure: " + departureTimeLabel.getText(), 50, 290);
            g2d.drawString("Arrival: " + arrivalTimeLabel.getText(), 50, 320);

            // Draw passenger details
            g2d.drawString("Passenger: " + nameLabel.getText(), 50, 380);
            g2d.drawString("Seat: " + seatNumberLabel.getText(), 50, 410);
            g2d.drawString("PNR: " + pnrNumber, 50, 440);

            // Draw barcode (placeholder)
            g2d.setColor(Color.BLACK);
            g2d.drawRect(50, 480, 300, 100);
            g2d.drawString("BARCODE", 150, 530);

            return PAGE_EXISTS;
        }
    }

    private void clearFields() {
        tfpnr.setText("");
        flightPanel.setVisible(false);
        passengerPanel.setVisible(false);
        printBtn.setEnabled(false);
        pnrNumber = null;
    }

    private String formatDateTime(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(timestamp);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(BoardingPass::new);
    }
}
