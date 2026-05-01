package miniproject;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("GYM MANAGEMENT - Dashboard");
        setSize(1100,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();//set up the complete UI design
    }

    private void initUI() {

        ThemeManager.applyFrame(this);

           JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(280,0));
        left.setBackground(ThemeManager.card());
        left.setLayout(new GridLayout(6,1,12,12));
        left.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel userLabel = new JLabel("Welcome", SwingConstants.CENTER);
        userLabel.setForeground(ThemeManager.text());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        left.add(userLabel);
// sidebar button
        JButton membersBtn = new JButton("Members");
        JButton trainersBtn = new JButton("Trainers");
        JButton attendanceBtn = new JButton("Attendance");
        JButton paymentsBtn = new JButton("Payments");
        JButton themeToggle = new JButton(ThemeManager.isDark() ? "☀ Light" : "🌙 Dark");

        ThemeManager.styleButton(membersBtn);
        ThemeManager.styleButton(trainersBtn);
        ThemeManager.styleButton(attendanceBtn);
        ThemeManager.styleButton(paymentsBtn);
        ThemeManager.styleButton(themeToggle);

        left.add(membersBtn);
        left.add(trainersBtn);
        left.add(attendanceBtn);
        left.add(paymentsBtn);
        left.add(themeToggle);

        add(left, BorderLayout.WEST);

        //create a grid in center
        JPanel center = new JPanel(new GridLayout(2,2,30,30));
        center.setBorder(BorderFactory.createEmptyBorder(60,60,60,60));
        center.setBackground(ThemeManager.bg());
//had each tile 
        center.add(tile("Members", membersBtn));
        center.add(tile("Trainers", trainersBtn));
        center.add(tile("Attendance", attendanceBtn));
        center.add(tile("Payments", paymentsBtn));

        add(center, BorderLayout.CENTER);

        // open all the modules
        membersBtn.addActionListener(e -> new MembersModule().setVisible(true));
        trainersBtn.addActionListener(e -> new TrainersModule().setVisible(true));
        attendanceBtn.addActionListener(e -> new AttendanceModule(this).setVisible(true));
        paymentsBtn.addActionListener(e -> new BillingModule(this).setVisible(true));
//light and dark color
        themeToggle.addActionListener(e -> {
            ThemeManager.toggle();
            SwingUtilities.invokeLater(() -> {
                dispose();
                new Dashboard().setVisible(true);
            });
        });
    }

    
    private JPanel tile(String title, JButton sourceBtn) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ThemeManager.card());
        p.setBorder(BorderFactory.createLineBorder(ThemeManager.btnBg(), 2));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(ThemeManager.text());

        p.add(t, BorderLayout.CENTER);

        
        p.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e) {
                sourceBtn.doClick();
            }
        });

        return p;
    }

    public static void main(String[] args) {
        new Dashboard().setVisible(true);
    }
}
