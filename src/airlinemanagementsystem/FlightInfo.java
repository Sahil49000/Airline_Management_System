package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightInfo extends JFrame {

    public FlightInfo() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(220, 230, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Flight panel with 2-column grid layout
        JPanel flightPanel = new JPanel(new GridLayout(0, 2, 30, 30)); // dynamic rows, 2 columns, spacing
        flightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(flightPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Header
        JLabel header = new JLabel("Flight Information", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 32));
        header.setForeground(new Color(51, 102, 255));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Load flight data
        loadFlightData(flightPanel);

        // Frame settings
        setTitle("Flight Information");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadFlightData(JPanel flightPanel) {
        DatabaseConnection dbConn = new DatabaseConnection();
        String sql = "SELECT f.*, dep.airport_name AS departure, arr.airport_name AS arrival FROM flight f " +
                     "LEFT JOIN airport dep ON f.departure_airport_id = dep.airport_id " +
                     "LEFT JOIN airport arr ON f.arrival_airport_id = arr.airport_id";

        try (Connection connection = dbConn.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JPanel card = createFlightCard(rs);

                // Padding container for spacing between cards
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setOpaque(false);
                wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                wrapper.add(card, BorderLayout.CENTER);

                flightPanel.add(wrapper);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flight data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createFlightCard(ResultSet rs) throws SQLException {
        RoundedPanel card = new RoundedPanel(15);
        card.setLayout(new GridLayout(6, 2, 10, 10)); // fewer rows for cleaner display
        card.setBorder(createRoundedBorder());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(500, 200));

        addLabelToCard(card, "Flight ID", String.valueOf(rs.getInt("flight_id")));
        addLabelToCard(card, "Flight Number", rs.getString("flight_number"));
        addLabelToCard(card, "Flight Name", rs.getString("flight_name"));
        addLabelToCard(card, "Departure", rs.getString("departure"));
        addLabelToCard(card, "Arrival", rs.getString("arrival"));
        addLabelToCard(card, "Travel Date", String.valueOf(rs.getDate("travel_date")));
        addLabelToCard(card, "Available Seats", String.valueOf(rs.getInt("available_seats")));
        addLabelToCard(card, "Price", "$" + rs.getBigDecimal("price"));
        addLabelToCard(card, "Journey Time", rs.getString("journey_time"));
        addLabelToCard(card, "Arrival Date", String.valueOf(rs.getDate("arrival_date")));
        addLabelToCard(card, "Departure Date", String.valueOf(rs.getDate("departure_date")));

        return card;
    }

    private void addLabelToCard(JPanel card, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(51, 102, 204));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel valueLabel = new JLabel(valueText != null ? valueText : "N/A");
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valueLabel.setForeground(Color.DARK_GRAY);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(label);
        card.add(valueLabel);
    }

    private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 153, 255), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlightInfo::new);
    }
}

class RoundedPanel extends JPanel {
    private final int radius;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
    }
}
