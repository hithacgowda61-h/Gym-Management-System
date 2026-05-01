package miniproject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceModule extends JDialog {
    private AttendanceTableModel model;
    private JTable table;
    private JComboBox<String> filterBox;

    public AttendanceModule(Frame owner) {
        super(owner, "Attendance", true);
        setSize(820, 500);
        setLocationRelativeTo(owner);

        initUI();
        model.load();
    }

    private void initUI() {
        ThemeManager.applyFrame(this);

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        model = new AttendanceTableModel();
        table = new JTable(model);

        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 36));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        table.setDefaultRenderer(Object.class, new ModernCellRenderer());
        table.setDefaultRenderer(Boolean.class, new ModernBooleanRenderer());

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- TOP FILTER BAR ----------
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));

        filterBox = new JComboBox<>(new String[]{
                "Show All", "Present Only", "Absent Only"
        });

        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterBox.addActionListener(e -> model.applyFilter((String) filterBox.getSelectedItem()));

        JButton markAll = new JButton("Mark All Present");
        JButton markNone = new JButton("Mark All Absent");

        ThemeManager.styleButton(markAll);
        ThemeManager.styleButton(markNone);

        markAll.addActionListener(e -> model.markAll(true));
        markNone.addActionListener(e -> model.markAll(false));

        top.add(new JLabel("Filter:"));
        top.add(filterBox);
        top.add(markAll);
        top.add(markNone);

        main.add(top, BorderLayout.NORTH);

        // ---------- BOTTOM BUTTON PANEL ----------
        JPanel bottom = new RoundedPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton refresh = new JButton("Refresh");
        JButton save = new JButton("Save Attendance");

        ThemeManager.styleButton(refresh);
        ThemeManager.styleButton(save);

        refresh.setPreferredSize(new Dimension(140, 35));
        save.setPreferredSize(new Dimension(160, 35));

        bottom.add(refresh);
        bottom.add(save);
        main.add(bottom, BorderLayout.SOUTH);

        refresh.addActionListener(e -> model.load());
        save.addActionListener(e -> save());
    }

    // =============== SAVE LOGIC ===============
    private void save() {
        List<Integer> presentList = new ArrayList<>();

        for (int r = 0; r < model.getRowCount(); r++) {
            Boolean isPresent = (Boolean) model.getValueAt(r, 2);
            if (isPresent != null && isPresent)
                presentList.add((Integer) model.getValueAt(r, 0));
        }

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO attendance(member_id, attendance_date, present) VALUES (?, CURDATE(), ?)")) {

            for (int id : presentList) {
                ps.setInt(1, id);
                ps.setBoolean(2, true);
                ps.addBatch();
            }

            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Attendance saved successfully.");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
        }
    }

    // =============== TABLE MODEL ===============
    static class AttendanceTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Name", "Present", "Status"};
        private final List<Object[]> rows = new ArrayList<>();
        private final List<Object[]> filtered = new ArrayList<>();

        void load() {
            rows.clear();

            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT id, name FROM members ORDER BY id ASC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            Boolean.FALSE,
                            "Absent"
                    });
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Load error: " + ex.getMessage());
            }

            applyFilter("Show All");
        }

        void applyFilter(String type) {
            filtered.clear();

            for (Object[] r : rows) {
                boolean present = (boolean) r[2];

                switch (type) {
                    case "Present Only":
                        if (present) filtered.add(r);
                        break;

                    case "Absent Only":
                        if (!present) filtered.add(r);
                        break;

                    default:
                        filtered.add(r);
                }
            }

            fireTableDataChanged();
        }

        void markAll(boolean present) {
            for (Object[] r : rows) {
                r[2] = present;
                r[3] = present ? "Present" : "Absent";
            }
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return filtered.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Object getValueAt(int row, int col) { return filtered.get(row)[col]; }

        @Override
        public boolean isCellEditable(int row, int col) { return col == 2; }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Object[] realRow = filtered.get(row);
            realRow[col] = value;

            realRow[3] = ((boolean) value) ? "Present" : "Absent";
            fireTableRowsUpdated(row, row);
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return switch (col) {
                case 0 -> Integer.class;
                case 2 -> Boolean.class;
                default -> String.class;
            };
        }
    }

    // =============== RENDERERS (PREMIUM LOOK) ===============
    static class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? new Color(248, 248, 248) : Color.WHITE);
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(new Color(220, 66, 66));
                c.setForeground(Color.WHITE);
            }

            setHorizontalAlignment(col == 0 ? CENTER : LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return c;
        }
    }

    static class ModernBooleanRenderer extends JCheckBox implements TableCellRenderer {
        public ModernBooleanRenderer() { setHorizontalAlignment(CENTER); }

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean selected, boolean focus, int row, int col) {

            setSelected(v != null && (Boolean) v);

            if (!selected)
                setBackground(row % 2 == 0 ? new Color(248, 248, 248) : Color.WHITE);
            else
                setBackground(new Color(220, 66, 66));

            return this;
        }
    }

    static class RoundedPanel extends JPanel {
        public RoundedPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(240, 240, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g);
        }
    }
}
