package miniproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MembersModule extends JFrame {
    private DefaultTableModel model;//stores table data
    private JTable table; 

    public MembersModule() {
        setTitle("Members");
        setSize(980,560);
        setLocationRelativeTo(null);
        initUI();
        load();
    }

    private void initUI() {
        ThemeManager.applyFrame(this);//apply theme

        model = new DefaultTableModel(new String[]{"ID","Name","Age","Gender","Phone","Email","Address","Membership"},0);
        table = new JTable(model);     //store globally
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(340,0));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(ThemeManager.card());
        right.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JTextField idF = new JTextField(); idF.setEditable(false);
        JTextField nameF = new JTextField();
        JTextField ageF = new JTextField();
        JComboBox<String> genderCb = new JComboBox<>(new String[]{"M","F","Other"});
        JTextField phoneF = new JTextField();
        JTextField emailF = new JTextField();
        JTextField addressF = new JTextField();
        JTextField membershipF = new JTextField();

        right.add(new JLabel("ID:")); right.add(idF);
        right.add(new JLabel("Name:")); right.add(nameF);
        right.add(new JLabel("Age:")); right.add(ageF);
        right.add(new JLabel("Gender:")); right.add(genderCb);
        right.add(new JLabel("Phone:")); right.add(phoneF);
        right.add(new JLabel("Email:")); right.add(emailF);
        right.add(new JLabel("Address:")); right.add(addressF);
        right.add(new JLabel("Membership:")); right.add(membershipF);

        JPanel btns = new JPanel(new GridLayout(1,3,8,8));
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JButton add = new JButton("Add"), upd = new JButton("Update"), del = new JButton("Delete");
        ThemeManager.styleButton(add); ThemeManager.styleButton(upd); ThemeManager.styleButton(del);
        btns.add(add); btns.add(upd); btns.add(del);
        right.add(Box.createRigidArea(new Dimension(0,8)));
        right.add(btns);

        add(right, BorderLayout.EAST);

        // fill form when row is clicked
        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idF.setText(String.valueOf(model.getValueAt(r,0)));
                    nameF.setText(String.valueOf(model.getValueAt(r,1)));
                    ageF.setText(String.valueOf(model.getValueAt(r,2)));
                    genderCb.setSelectedItem(String.valueOf(model.getValueAt(r,3)));
                    phoneF.setText(String.valueOf(model.getValueAt(r,4)));
                    emailF.setText(String.valueOf(model.getValueAt(r,5)));
                    addressF.setText(String.valueOf(model.getValueAt(r,6)));
                    membershipF.setText(String.valueOf(model.getValueAt(r,7)));
                }
            }
        });

        // add button
        add.addActionListener(e -> {
            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO members(name,age,gender,phone,email,address,membership) VALUES(?,?,?,?,?,?,?)")) {

                ps.setString(1, nameF.getText());
                ps.setInt(2, safeInt(ageF.getText()));
                ps.setString(3, (String)genderCb.getSelectedItem());
                ps.setString(4, phoneF.getText());
                ps.setString(5, emailF.getText());
                ps.setString(6, addressF.getText());
                ps.setString(7, membershipF.getText());

                ps.executeUpdate();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
            }
        });

        //update button
        upd.addActionListener(e -> {
            if (table.getSelectedRow() == -1) { 
                JOptionPane.showMessageDialog(this, "Select a row!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "UPDATE members SET name=?,age=?,gender=?,phone=?,email=?,address=?,membership=? WHERE id=?")) {

                ps.setString(1, nameF.getText());
                ps.setInt(2, safeInt(ageF.getText()));
                ps.setString(3, (String)genderCb.getSelectedItem());
                ps.setString(4, phoneF.getText());
                ps.setString(5, emailF.getText());
                ps.setString(6, addressF.getText());
                ps.setString(7, membershipF.getText());
                ps.setInt(8, safeInt(idF.getText()));

                ps.executeUpdate();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
            }
        });

        //delete button
        del.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {   // <--- FIX ADDED
                JOptionPane.showMessageDialog(this, "Select a row!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM members WHERE id=?")) {

                ps.setInt(1, safeInt(idF.getText()));
                ps.executeUpdate();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
            }
        });
    }

    private int safeInt(String s) { 
        try { return Integer.parseInt(s); } 
        catch (Exception e) { return 0; } 
    }

    private void load() {
        model.setRowCount(0);
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM members");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("membership")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }
}
