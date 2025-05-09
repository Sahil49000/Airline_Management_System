package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddCustomer extends JFrame implements ActionListener {

    JTextField tfname, tfphone, tfaadhar, tfnationality, tfaddress, tfemail;
    JRadioButton rbmale, rbfemale;
    JFormattedTextField tfdob;

    public AddCustomer() {
        // Full Screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 250, 255));
        setLayout(new BorderLayout());

        // Heading
        JLabel heading = new JLabel("ADD CUSTOMER DETAILS", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 36));
        heading.setForeground(new Color(0, 102, 204));
        heading.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(heading, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Single column layout
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(40, 80, 40, 80));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 18);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Fields
        addField(formPanel, "Name", tfname = createStyledTextField(fieldFont), labelFont);
        addField(formPanel, "Aadhar Number", tfaadhar = createStyledTextField(fieldFont), labelFont);
        addField(formPanel, "Nationality", tfnationality = createStyledTextField(fieldFont), labelFont);
        addField(formPanel, "Address", tfaddress = createStyledTextField(fieldFont), labelFont);
        addField(formPanel, "Phone", tfphone = createStyledTextField(fieldFont), labelFont);
        addField(formPanel, "Email", tfemail = createStyledTextField(fieldFont), labelFont);

        // Date of Birth
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dobPanel.setBackground(Color.WHITE);
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD)");
        dobLabel.setFont(labelFont);
        dobPanel.add(dobLabel);
        tfdob = new JFormattedTextField();
        tfdob.setFont(fieldFont);
        tfdob.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        tfdob.setBackground(Color.WHITE);
        tfdob.setPreferredSize(new Dimension(300, 30)); // Adjust size
        dobPanel.add(tfdob);
        formPanel.add(dobPanel);

        // Gender
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        JLabel lblgender = new JLabel("Gender");
        lblgender.setFont(labelFont);
        genderPanel.add(lblgender);
        rbmale = new JRadioButton("Male");
        rbfemale = new JRadioButton("Female");
        rbmale.setFont(fieldFont);
        rbfemale.setFont(fieldFont);
        rbmale.setBackground(Color.WHITE);
        rbfemale.setBackground(Color.WHITE);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbmale);
        genderGroup.add(rbfemale);
        genderPanel.add(rbmale);
        genderPanel.add(rbfemale);
        formPanel.add(genderPanel);

        add(formPanel, BorderLayout.CENTER);

        // Save Button
        JButton save = new JButton("SAVE");
        save.setPreferredSize(new Dimension(180, 40));
        save.setFont(new Font("Segoe UI", Font.BOLD, 18));
        save.setBackground(new Color(0, 102, 204));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        save.addActionListener(this);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 250, 255));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        btnPanel.add(save);

        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        field.setPreferredSize(new Dimension(300, 30)); // Adjust Size
        return field;
    }

    private void addField(JPanel panel, String labelText, JTextField field, Font font) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        fieldPanel.add(label);
        fieldPanel.add(field);
        panel.add(fieldPanel);
    }

    public void actionPerformed(ActionEvent ae) {
        String name = tfname.getText().trim();
        String aadhar = tfaadhar.getText().trim();
        String nationality = tfnationality.getText().trim();
        String address = tfaddress.getText().trim();
        String phone = tfphone.getText().trim();
        String email = tfemail.getText().trim();
        String dob = tfdob.getText().trim();
        String gender = rbmale.isSelected() ? "Male" : rbfemale.isSelected() ? "Female" : "";

        if (name.isEmpty() || aadhar.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Aadhar, and Phone are required.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            Connection connection = dbConnection.getConnection();

            String query = "INSERT INTO passenger(name, aadhar, nationality, address, gender, phone, email, date_of_birth) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, aadhar);
            pstmt.setString(3, nationality);
            pstmt.setString(4, address);
            pstmt.setString(5, gender);
            pstmt.setString(6, phone);
            pstmt.setString(7, email);
            pstmt.setString(8, dob.isEmpty() ? null : dob);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer Details Added Successfully");
            setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AddCustomer();
    }
}