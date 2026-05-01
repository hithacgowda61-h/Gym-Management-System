package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;

public class BillingModule extends JDialog {
    private JComboBox<String> memberList = new JComboBox<>();
    private LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
    private JTextField amount = new JTextField(10);
    private JTextField date = new JTextField(10);
    private JTextField method = new JTextField(10);

    public BillingModule(Frame owner) {
        super(owner,"Add Payment",true);
        setSize(420,300);
        setLocationRelativeTo(owner);
        loadMembers();
        initUI();
    }

    private void loadMembers() {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM members");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                memberList.addItem(name);
                map.put(name,id);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Load error: "+e.getMessage());
        }
    }

    private void initUI() {
        ThemeManager.applyFrame(this);
        JPanel p = new JPanel(new GridBagLayout());//position,padding,alignment
        p.setBackground(ThemeManager.card());
        GridBagConstraints gbc=new GridBagConstraints(); //which coloumn,row
        gbc.insets=new Insets(8,8,8,8); 
        gbc.fill=GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Member:"),gbc);
        gbc.gridx=1; p.add(memberList,gbc);

        gbc.gridx=0; gbc.gridy=1; p.add(new JLabel("Amount:"),gbc);
        gbc.gridx=1; p.add(amount,gbc);

        gbc.gridx=0; gbc.gridy=2; p.add(new JLabel("Date (YYYY-MM-DD):"),gbc);
        gbc.gridx=1; p.add(date,gbc);

        gbc.gridx=0; gbc.gridy=3; p.add(new JLabel("Method:"),gbc);
        gbc.gridx=1; p.add(method,gbc);

        JButton save=new JButton("Save");
        ThemeManager.styleButton(save);

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2;
        p.add(save,gbc);
        add(p);

        save.addActionListener(e->{
            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO payments(member_id,amount,payment_date,method) VALUES(?,?,?,?)")) {

                String name = (String) memberList.getSelectedItem();
                int memberId = map.get(name);

                ps.setInt(1,memberId);
                ps.setDouble(2,Double.parseDouble(amount.getText()));
                ps.setDate(3,java.sql.Date.valueOf(date.getText()));
                ps.setString(4,method.getText());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Payment saved.");
                dispose();

            } catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
            }
        });
    }
}
