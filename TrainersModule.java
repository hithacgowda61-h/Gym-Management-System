package miniproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TrainersModule extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    public TrainersModule() {
        setTitle("Trainers");
        setSize(800, 500);
        setLocationRelativeTo(null);
        initUI();
        load();
    }

    private void initUI() {
        ThemeManager.applyFrame(this);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Specialization", "Phone"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel(new GridLayout(9, 1, 8, 8));
        right.setPreferredSize(new Dimension(300, 0));
        right.setBackground(ThemeManager.card());
        right.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTextField idF = new JTextField();
        idF.setEditable(false);

        JTextField nameF = new JTextField();
        JTextField specF = new JTextField();
        JTextField phoneF = new JTextField();

        right.add(new JLabel("ID:"));
        right.add(idF);

        right.add(new JLabel("Name:"));
        right.add(nameF);

        right.add(new JLabel("Specialization:"));
        right.add(specF);

        right.add(new JLabel("Phone:"));
        right.add(phoneF);

        JPanel btns = new JPanel(new GridLayout(1, 3, 8, 8));
        JButton add = new JButton("Add");
        JButton upd = new JButton("Update");
        JButton del = new JButton("Delete");

        ThemeManager.styleButton(add);
        ThemeManager.styleButton(upd);
        ThemeManager.styleButton(del);

        btns.add(add);
        btns.add(upd);
        btns.add(del);

        right.add(btns);
        add(right, BorderLayout.EAST);

        // TABLE CLICK FILL INPUTS
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idF.setText(String.valueOf(model.getValueAt(r, 0)));
                    nameF.setText(String.valueOf(model.getValueAt(r, 1)));
                    specF.setText(String.valueOf(model.getValueAt(r, 2)));
                    phoneF.setText(String.valueOf(model.getValueAt(r, 3)));
                }
            }
        });

        // ADD TRAINER
        add.addActionListener(e -> {
            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "INSERT INTO trainers (name, specialization, phone) VALUES (?, ?, ?)")) {

                ps.setString(1, nameF.getText());
                ps.setString(2, specF.getText());
                ps.setString(3, phoneF.getText());
                ps.executeUpdate();

                load();
                clear(idF, nameF, specF, phoneF);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // UPDATE TRAINER
        upd.addActionListener(e -> {
            if (idF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a row first!");
                return;
            }

            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "UPDATE trainers SET name=?, specialization=?, phone=? WHERE id=?")) {

                ps.setString(1, nameF.getText());
                ps.setString(2, specF.getText());
                ps.setString(3, phoneF.getText());
                ps.setInt(4, Integer.parseInt(idF.getText()));

                ps.executeUpdate();

                load();
                clear(idF, nameF, specF, phoneF);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // DELETE TRAINER
        del.addActionListener(e -> {
            if (idF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a row first!");
                return;
            }

            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "DELETE FROM trainers WHERE id=?")) {

                ps.setInt(1, Integer.parseInt(idF.getText()));
                ps.executeUpdate();

                load();
                clear(idF, nameF, specF, phoneF);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    private void load() {
        model.setRowCount(0);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM trainers ORDER BY id ASC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage());
        }
    }

    private void clear(JTextField id, JTextField n, JTextField s, JTextField p) {
        id.setText("");
        n.setText("");
        s.setText("");
        p.setText("");
    }
}
