package airlinemanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {
    JButton submit, reset, close;
    JTextField tfusername;
    JPasswordField tfpassword;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private int loginAttempts = 0;

    public Login() {
        // Set up the frame properties
        setTitle("Airline Management System - Login");
        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(null);

        // Create a panel for better organization
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(20, 20, 340, 180);
        mainPanel.setBackground(new Color(240, 240, 240));
        add(mainPanel);

        // Username Label and Field
        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(20, 20, 100, 20);
        mainPanel.add(lblusername);

        tfusername = new JTextField();
        tfusername.setBounds(130, 20, 200, 20);
        mainPanel.add(tfusername);

        // Password Label and Field
        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(20, 60, 100, 20);
        mainPanel.add(lblpassword);

        tfpassword = new JPasswordField();
        tfpassword.setBounds(130, 60, 200, 20);
        mainPanel.add(tfpassword);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBounds(20, 100, 300, 40);
        buttonPanel.setBackground(new Color(240, 240, 240));
        mainPanel.add(buttonPanel);

        // Reset Button
        reset = new JButton("Reset");
        reset.addActionListener(this);
        buttonPanel.add(reset);

        // Submit Button
        submit = new JButton("Submit");
        submit.addActionListener(this);
        buttonPanel.add(submit);

        // Close Button
        close = new JButton("Close");
        close.addActionListener(this);
        buttonPanel.add(close);

        // Frame settings
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private boolean validateInput() {
        String username = tfusername.getText().trim();
        String password = new String(tfpassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (username.length() < 3 || username.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "Username must be between 3 and 50 characters.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submit) {
            if (!validateInput()) {
                return;
            }

            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                JOptionPane.showMessageDialog(this,
                        "Too many failed login attempts. Please try again later.",
                        "Login Disabled",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = tfusername.getText().trim();
            String password = new String(tfpassword.getPassword());

            try {
                DatabaseConnection dbConnection = new DatabaseConnection();
                Connection conn = dbConnection.getConnection();

                if (conn != null) {
                    String query = "SELECT * FROM login WHERE username = ? AND password = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password); // Using plain text password

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        new Home(role); // Pass the role to Home screen
                        dispose();
                    } else {
                        loginAttempts++;
                        JOptionPane.showMessageDialog(this,
                                "Invalid Username or Password. Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - loginAttempts),
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                        tfpassword.setText("");
                    }

                    rs.close();
                    stmt.close();
                    conn.close();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Database connection failed. Please try again later.",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == close) {
            System.exit(0);
        } else if (ae.getSource() == reset) {
            tfusername.setText("");
            tfpassword.setText("");
            tfusername.requestFocus();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Login::new);
    }
}