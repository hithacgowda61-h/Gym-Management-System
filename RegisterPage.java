package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    private JTextField usernameField = new JTextField(16);
    private JTextField fullNameField = new JTextField(16);
    private JPasswordField passField = new JPasswordField(16);
    private JPasswordField confirmField = new JPasswordField(16);

    public RegisterPage() {
        setTitle("Register - Gym");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        ThemeManager.applyFrame(this);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.card());
        card.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; card.add(title, gbc);
        gbc.gridwidth=1;

        gbc.gridy++; gbc.gridx=0; JLabel ulabel = new JLabel("Username:"); ulabel.setForeground(ThemeManager.text()); card.add(ulabel, gbc);
        gbc.gridx=1; card.add(usernameField, gbc);

        gbc.gridy++; gbc.gridx=0; JLabel fnlabel = new JLabel("Full Name:"); fnlabel.setForeground(ThemeManager.text()); card.add(fnlabel, gbc);
        gbc.gridx=1; card.add(fullNameField, gbc);

        gbc.gridy++; gbc.gridx=0; JLabel plabel = new JLabel("Password:"); plabel.setForeground(ThemeManager.text()); card.add(plabel, gbc);
        gbc.gridx=1; card.add(passField, gbc);

        gbc.gridy++; gbc.gridx=0; JLabel clabel = new JLabel("Confirm Password:"); clabel.setForeground(ThemeManager.text()); card.add(clabel, gbc);
        gbc.gridx=1; card.add(confirmField, gbc);

        gbc.gridy++; gbc.gridx=0; gbc.gridwidth=2;
        JButton registerBtn = new JButton("Register");
        ThemeManager.styleButton(registerBtn);
        card.add(registerBtn, gbc);

        add(card);

        registerBtn.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String u = usernameField.getText().trim();
        String fn = fullNameField.getText().trim();
        String p = new String(passField.getPassword());
        String cp = new String(confirmField.getPassword());

        if (u.isEmpty() || p.isEmpty() || fn.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill required fields."); return; }
        if (!p.equals(cp)) { JOptionPane.showMessageDialog(this, "Passwords do not match."); return; }

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(username,full_name,password) VALUES(?,?,?)")) {
            ps.setString(1, u);
            ps.setString(2, fn);
            ps.setString(3, PasswordUtils.hash(p));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered. Now login.");
            dispose();
            new LoginPage().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
