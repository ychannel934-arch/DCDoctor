package com.dcdoctor.UI.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UpdatePatientRecordUI extends JFrame {

    private JTable table;

    private DefaultTableModel model;

    public UpdatePatientRecordUI() {

        setTitle("DC Doctor - Update Patient Record");

        setSize(1000, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {

        getContentPane().setBackground(
                new Color(15, 23, 42)
        );

        setLayout(new BorderLayout(15, 15));

        // ===== HEADER =====

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setBackground(
                new Color(30, 41, 59)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        25,
                        20,
                        25
                )
        );

        JLabel titleLabel = new JLabel(
                "📋 UPDATE PATIENT RECORD"
        );

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        28)
        );

        JLabel subtitleLabel = new JLabel(
                "Manage and update patient medical information"
        );

        subtitleLabel.setForeground(
                new Color(148, 163, 184)
        );

        subtitleLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        15)
        );

        JPanel textPanel = new JPanel(
                new GridLayout(2, 1)
        );

        textPanel.setOpaque(false);

        textPanel.add(titleLabel);

        textPanel.add(subtitleLabel);

        headerPanel.add(
                textPanel,
                BorderLayout.WEST
        );

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE =====

        String[] columns = {

                "Patient ID",

                "Patient Name",

                "Diagnosis",

                "Treatment",

                "Medication"
        };

        model = new DefaultTableModel(
                columns,
                0
        );

        // ===== SAMPLE DATA =====

        model.addRow(new Object[]{

                "P001",

                "Nguyen Van A",

                "Type 2 Diabetes",

                "Diet Control",

                "Metformin"
        });

        model.addRow(new Object[]{

                "P002",

                "Tran Thi B",

                "Type 1 Diabetes",

                "Insulin Therapy",

                "Insulin"
        });

        model.addRow(new Object[]{

                "P003",

                "Le Van C",

                "High Blood Sugar",

                "Exercise Plan",

                "Glucophage"
        });

        table = new JTable(model);

        table.setRowHeight(40);

        table.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        14)
        );

        table.setBackground(
                new Color(30, 41, 59)
        );

        table.setForeground(Color.WHITE);

        table.setGridColor(
                new Color(51, 65, 85)
        );

        table.setSelectionBackground(
                new Color(59, 130, 246)
        );

        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        14)
        );

        table.getTableHeader().setBackground(
                new Color(59, 130, 246)
        );

        table.getTableHeader().setForeground(
                Color.WHITE
        );

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );

        scrollPane.getViewport().setBackground(
                new Color(15, 23, 42)
        );

        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====

        JPanel bottomPanel = new JPanel();

        bottomPanel.setBackground(
                new Color(15, 23, 42)
        );

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        20,
                        20,
                        20
                )
        );

        JButton updateBtn = new JButton(
                "Update Record"
        );

        stylePrimaryButton(updateBtn);

        updateBtn.addActionListener(
                e -> updateRecord()
        );

        bottomPanel.add(updateBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void stylePrimaryButton(JButton button) {

        button.setBackground(
                new Color(59, 130, 246)
        );

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        15)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        25,
                        12,
                        25
                )
        );
    }

    private void updateRecord() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a patient."
            );

            return;
        }

        String diagnosis = JOptionPane.showInputDialog(
                this,
                "Enter new diagnosis:",
                model.getValueAt(row, 2)
        );

        String treatment = JOptionPane.showInputDialog(
                this,
                "Enter new treatment:",
                model.getValueAt(row, 3)
        );

        String medication = JOptionPane.showInputDialog(
                this,
                "Enter new medication:",
                model.getValueAt(row, 4)
        );

        if (diagnosis != null
                && treatment != null
                && medication != null) {

            model.setValueAt(
                    diagnosis,
                    row,
                    2
            );

            model.setValueAt(
                    treatment,
                    row,
                    3
            );

            model.setValueAt(
                    medication,
                    row,
                    4
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Patient record updated successfully!"
            );
        }
    }
}