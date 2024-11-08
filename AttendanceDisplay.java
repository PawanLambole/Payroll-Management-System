package EmployeePayrollSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellR
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class AttendanceDisplay extends JFrame {

    JTextField txtSearch;
    JLabel labelSearch;
    JButton btnSearch, btnBack;
    JTable table;
    JPanel attendancePanel, searchPanel;
    JScrollPane scrollPane;
    DefaultTableCellRenderer cellRenderer;
    public AttendanceDisplay() {
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font tableFont = new Font("Arial", Font.PLAIN, 16);

        // Search Panel
        labelSearch = new JLabel("Search Employee: ");
        labelSearch.setFont(labelFont);

        txtSearch = new JTextField();
        txtSearch.setText("Enter Employee ID");
        txtSearch.setPreferredSize(new Dimension(150, 22));

        btnSearch = new JButton("Search");
        btnSearch.setFont(labelFont);

        searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(new Color(145, 194, 236));
        searchPanel.setPreferredSize(new Dimension(900, 50));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Add components to search panel
        searchPanel.add(labelSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        attendancePanel = new JPanel(new BorderLayout()); // Use BorderLayout for main panel
        attendancePanel.setBackground(new Color(145, 194, 236));
        attendancePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        table = new JTable(tableModel);
        table.setPreferredSize(new Dimension(400, 100));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Set header height
        header.setFont(labelFont); // Set header font to bold
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align header text

        // Center align cell contents
        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Set row height and cell font
        table.setRowHeight(30); // Set row height
        table.setFont(tableFont); // Set font for table cells

        // Add columns to table model
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Date");
        tableModel.addColumn("Check In Time");
        tableModel.addColumn("Check Out Time");

        scrollPane = new JScrollPane(table);

        // Add scroll pane to center of attendance panel
        attendancePanel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        btnBack = new JButton("Back");
        btnBack.setFont(labelFont);
        btnBack.setPreferredSize(new Dimension(100, 30));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align right for back button
        buttonPanel.add(btnBack);
        attendancePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add back button to south of attendance panel
        attendancePanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

        // Add search panel and attendance panel to JFrame
        add(searchPanel, BorderLayout.NORTH);
        add(attendancePanel, BorderLayout.CENTER);

        setTitle("Attendance");
        setSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Handle Search Button
        btnSearch.addActionListener(event -> {
            try {
                String employee_id_text = txtSearch.getText().trim();
                if (employee_id_text.isEmpty()) {
                    JOptionPane.showMessageDialog(attendancePanel, "Please enter Employee ID");
                } else {
                    int employee_id = Integer.parseInt(employee_id_text);
                    ManagerService managerService = new ManagerService();
                    ResultSet resultSet = managerService.attendance(employee_id);
                    try {
                        tableModel.setRowCount(0); // Clear existing rows
                        while (resultSet.next()) {
                            Vector<Object> row = new Vector<>();
                            row.add(resultSet.getString("first_name"));
                            row.add(resultSet.getString("last_name"));
                            row.add(resultSet.getDate("date"));
                            row.add(resultSet.getTime("check_in_time"));
                            row.add(resultSet.getTime("check_out_time"));
                            tableModel.addRow(row);
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(attendancePanel, "Please enter Employee ID");
            }
        });

        // Handle Back Button
        btnBack.addActionListener(event ->{
            new ManagerDashboard();
            dispose();
        });
    }
}
