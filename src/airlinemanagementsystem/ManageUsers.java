package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageUsers extends JFrame implements ActionListener {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private Connection connection;

    public ManageUsers() {
        setTitle("Manage Users");
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

        JLabel heading = new JLabel("Manage Users");
        heading.setBounds(700, 20, 500, 35);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);

        backButton = createButton("Back", new Color(0, 100, 180));
        backButton.setBounds(20, 20, 100, 35);
        backButton.addActionListener(e -> dispose());
        headerPanel.add(backButton);

        String[] columnNames = {"User ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        usersTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBounds(60, 120, 1400, 500);
        add(scrollPane);

        addButton = createButton("Add User", new Color(0, 150, 0));
        addButton.setBounds(200, 650, 150, 40);
        addButton.addActionListener(this);
        add(addButton);

        editButton = createButton("Edit User", new Color(0, 120, 215));
        editButton.setBounds(400, 650, 150, 40);
        editButton.addActionListener(this);
        add(editButton);

        deleteButton = createButton("Delete User", new Color(200, 0, 0));
        deleteButton.setBounds(600, 650, 150, 40);
        deleteButton.addActionListener(this);
        add(deleteButton);

        refreshButton = createButton("Refresh", new Color(100, 100, 100));
        refreshButton.setBounds(800, 650, 150, 40);
        refreshButton.addActionListener(e -> loadUsers());
        add(refreshButton);

        loadUsers();
        setVisible(true);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT user_id, username, role FROM login ORDER BY user_id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            showAddUserDialog();
        } else if (e.getSource() == editButton) {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showEditUserDialog(selectedRow);
        } else if (e.getSource() == deleteButton) {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteUser(selectedRow);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "user"});

        dialog.add(new JLabel("Username:")); dialog.add(usernameField);
        dialog.add(new JLabel("Password:")); dialog.add(passwordField);
        dialog.add(new JLabel("Role:")); dialog.add(roleBox);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(saveBtn); dialog.add(cancelBtn);

        saveBtn.addActionListener(ev -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleBox.getSelectedItem();

                // Validate input
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username and password are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if username already exists
                String checkSql = "SELECT COUNT(*) FROM login WHERE username = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "INSERT INTO login (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password); // In production, this should be hashed
                stmt.setString(3, role);
                
                int result = stmt.executeUpdate();
                stmt.close();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "User added successfully!");
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditUserDialog(int selectedRow) {
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        // Get selected user data
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "user"});

        // Set current values
        roleBox.setSelectedItem(role);

        dialog.add(new JLabel("Username:")); dialog.add(usernameField);
        dialog.add(new JLabel("New Password (leave blank to keep current):")); dialog.add(passwordField);
        dialog.add(new JLabel("Role:")); dialog.add(roleBox);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(saveBtn); dialog.add(cancelBtn);

        saveBtn.addActionListener(ev -> {
            try {
                String newUsername = usernameField.getText().trim();
                String newPassword = new String(passwordField.getPassword());
                String newRole = (String) roleBox.getSelectedItem();

                // Validate input
                if (newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if new username already exists (if changed)
                if (!newUsername.equals(username)) {
                    String checkSql = "SELECT COUNT(*) FROM login WHERE username = ? AND user_id != ?";
                    PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                    checkStmt.setString(1, newUsername);
                    checkStmt.setInt(2, userId);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sql;
                PreparedStatement stmt;
                
                if (newPassword.isEmpty()) {
                    // Update without changing password
                    sql = "UPDATE login SET username=?, role=? WHERE user_id=?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setString(1, newUsername);
                    stmt.setString(2, newRole);
                    stmt.setInt(3, userId);
                } else {
                    // Update including new password
                    sql = "UPDATE login SET username=?, password=?, role=? WHERE user_id=?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setString(1, newUsername);
                    stmt.setString(2, newPassword); // In production, this should be hashed
                    stmt.setString(3, newRole);
                    stmt.setInt(4, userId);
                }
                
                int result = stmt.executeUpdate();
                stmt.close();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "User updated successfully!");
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update user.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteUser(int selectedRow) {
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user " + username + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // If no bookings exist, proceed with deletion
                String sql = "DELETE FROM login WHERE user_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, userId);
                int result = stmt.executeUpdate();
                stmt.close();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageUsers::new);
    }
} 