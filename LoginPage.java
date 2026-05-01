package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField = new JTextField(16);
    private JPasswordField passwordField = new JPasswordField(16);

    public LoginPage() {
        setTitle("Gym Management — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 340);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        ThemeManager.applyFrame(this);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.card());
        card.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        card.add(title, gbc);

        gbc.gridwidth=1;
        gbc.gridy++; gbc.gridx=0;
        JLabel ulabel = new JLabel("Username:"); ulabel.setForeground(ThemeManager.text()); card.add(ulabel, gbc);
        gbc.gridx=1; card.add(usernameField, gbc);

        gbc.gridy++; gbc.gridx=0;
        JLabel plabel = new JLabel("Password:"); plabel.setForeground(ThemeManager.text()); card.add(plabel, gbc);
        gbc.gridx=1; card.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx=0; gbc.gridwidth=2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.setBackground(ThemeManager.card());

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton toggleBtn = new JButton(ThemeManager.isDark() ? "☀ Light" : "🌙 Dark");

        ThemeManager.styleButton(loginBtn);
        ThemeManager.styleButton(registerBtn);
        ThemeManager.styleButton(toggleBtn);

        btns.add(loginBtn); btns.add(registerBtn); btns.add(toggleBtn);
        card.add(btns, gbc);

        add(card);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });

        toggleBtn.addActionListener(e -> {
            ThemeManager.toggle();
            SwingUtilities.invokeLater(() -> {
                dispose();
                new LoginPage().setVisible(true);
            });
        });
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username and password."); return; }

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,username,password FROM users WHERE username=?")) {
            ps.setString(1, u);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password");
                if (PasswordUtils.verify(p, stored)) {
                    dispose();
                    new Dashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
