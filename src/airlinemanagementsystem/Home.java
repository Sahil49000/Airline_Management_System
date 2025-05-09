package airlinemanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class Home extends JFrame implements ActionListener {
    private String userRole;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private Timer timer;

    public Home(String role) {
        this.userRole = role;
        setLayout(null);
        
        // Background Image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("airlinemanagementsystem/icons/front.jpg"));
        JLabel image = new JLabel(i1);
        image.setBounds(0, 0, 1600, 800);
        add(image);

        // Welcome Label
        welcomeLabel = new JLabel("Welcome to Air India Management System");
        welcomeLabel.setBounds(500, 40, 1000, 40);
        welcomeLabel.setForeground(Color.BLUE);
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
        image.add(welcomeLabel);

        // Time Label
        timeLabel = new JLabel();
        timeLabel.setBounds(1200, 40, 300, 40);
        timeLabel.setForeground(Color.BLUE);
        timeLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        image.add(timeLabel);

        // Update time every second
        timer = new Timer(1000, e -> updateTime());
        timer.start();

        // Create Buttons based on user role
        createButtons(image);

        // Add logout button
        JButton logoutButton = createModernButton("Logout", 1200, 700);
        logoutButton.addActionListener(e -> {
            timer.stop();
            dispose();
            new Login();
        });
        image.add(logoutButton);

        // Frame Settings
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createButtons(JLabel image) {
        int y = 200;
        int spacing = 100;

        // Common buttons for all users
        addButton(image, "Flight Details", 300, y);
        y += spacing;
        addButton(image, "Book Flight", 300, y);
        y += spacing;
        addButton(image, "Journey Details", 300, y);
        y += spacing;
        addButton(image, "Cancel Ticket", 300, y);
        y += spacing;
        addButton(image, "Boarding Pass", 300, y);

        // Admin-only buttons
        if ("admin".equalsIgnoreCase(userRole)) {
            y = 200;
            addButton(image, "Add Customer Details", 800, y);
            y += spacing;
            addButton(image, "Manage Flights", 800, y);
            y += spacing;
            addButton(image, "View Bookings", 800, y);
            y += spacing;
            addButton(image, "Manage Users", 800, y);
            y += spacing;
            addButton(image, "Reports", 800, y);
        }
    }

    private void addButton(JLabel image, String text, int x, int y) {
        JButton button = createModernButton(text, x, y);
        button.addActionListener(this);
        image.add(button);
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }

    private JButton createModernButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                g2d.setColor(new Color(50, 150, 250));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth(), getHeight(), 15, 15);

                // Button text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };

        button.setBounds(x, y, 250, 40);
        button.setFont(new Font("Tahoma", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);

        // Enhanced hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBackground(new Color(30, 130, 230));
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBackground(new Color(50, 150, 250));
                button.repaint();
            }
        });

        return button;
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        
        switch (command) {
            case "Add Customer Details":
                new AddCustomer();
                break;
            case "Flight Details":
                new FlightInfo();
                break;
            case "Book Flight":
                new BookFlight();
                break;
            case "Journey Details":
                new JourneyDetails();
                break;
            case "Cancel Ticket":
                new Cancel();
                break;
            case "Boarding Pass":
                new BoardingPass();
                break;
            case "Manage Flights":
                new ManageFlights();
                break;
            case "View Bookings":
                new ViewBookings();
                break;
            case "Manage Users":
                new ManageUsers();
                break;
            case "Reports":
                new Reports();
                break;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Home("user"));
    }
}
