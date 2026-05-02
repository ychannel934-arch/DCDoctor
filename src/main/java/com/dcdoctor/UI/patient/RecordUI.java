// ===============================
// PATIENT - RecordUI.java
// Chức năng: Bệnh nhân xem hồ sơ bệnh án điện tử
// Package: com.dcdoctor.UI
// ===============================

package com.dcdoctor.UI.patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RecordUI extends JFrame {

    private JTable recordTable;

    public RecordUI() {
        setTitle("Medical Records");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(
                "DIGITAL MEDICAL RECORDS",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        String[] columns = {
                "Record ID",
                "Visit Date",
                "Doctor",
                "Diagnosis",
                "Prescription",
                "Status"
        };

        Object[][] data = {
                {
                        "MR001",
                        "2026-03-15",
                        "Dr. John Smith",
                        "Common Cold",
                        "Paracetamol 500mg",
                        "Completed"
                },
                {
                        "MR002",
                        "2026-04-01",
                        "Dr. Sarah Lee",
                        "Migraine",
                        "Ibuprofen",
                        "Completed"
                },
                {
                        "MR003",
                        "2026-04-10",
                        "Dr. Michael Brown",
                        "Routine Checkup",
                        "Vitamin Supplements",
                        "Completed"
                }
        };

        DefaultTableModel model =
                new DefaultTableModel(data, columns);

        recordTable = new JTable(model);
        recordTable.setRowHeight(30);
        recordTable.setFont(new Font("Arial", Font.PLAIN, 14));
        recordTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        JScrollPane scrollPane =
                new JScrollPane(recordTable);

        JButton viewButton = new JButton("View Details");
        JButton closeButton = new JButton("Close");

        styleButton(viewButton);
        styleButton(closeButton);

        viewButton.addActionListener(e -> viewRecord());
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void viewRecord() {
        int row = recordTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medical record first!"
            );
            return;
        }

        String details =
                "Record ID: " + recordTable.getValueAt(row, 0) + "\n" +
                        "Visit Date: " + recordTable.getValueAt(row, 1) + "\n" +
                        "Doctor: " + recordTable.getValueAt(row, 2) + "\n" +
                        "Diagnosis: " + recordTable.getValueAt(row, 3) + "\n" +
                        "Prescription: " + recordTable.getValueAt(row, 4) + "\n" +
                        "Status: " + recordTable.getValueAt(row, 5);

        JOptionPane.showMessageDialog(
                this,
                details,
                "Medical Record Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
    }
}