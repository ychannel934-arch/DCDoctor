package com.dcdoctor.UI.hospital;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateRecordUI extends JFrame {

    private JTextField txtPatientId;

    private JTextField txtDiagnosis;

    private JTextField txtTreatment;

    private JTextField txtDate;

    public UpdateRecordUI() {

        setTitle("DC Doctor - Update Patient Record");

        setSize(750, 500);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {

        getContentPane().setBackground(
                new Color(15, 23, 42)
        );

        setLayout(new BorderLayout());

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
                "Update patient diagnosis and treatment information"
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

        // ===== MAIN PANEL =====

        JPanel mainPanel = new JPanel(
                new GridLayout(5, 2, 15, 15)
        );

        mainPanel.setBackground(
                new Color(30, 41, 59)
        );

        mainPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(51, 65, 85),
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                30,
                                30,
                                30,
                                30
                        )
                )
        );

        Font labelFont = new Font(
                "Segoe UI",
                Font.BOLD,
                14
        );

        Font inputFont = new Font(
                "Segoe UI",
                Font.PLAIN,
                14
        );

        // ===== LABELS =====

        JLabel lblPatientId = createLabel(
                "Mã bệnh nhân",
                labelFont
        );

        JLabel lblDiagnosis = createLabel(
                "Chẩn đoán",
                labelFont
        );

        JLabel lblTreatment = createLabel(
                "Kế hoạch điều trị",
                labelFont
        );

        JLabel lblDate = createLabel(
                "Ngày cập nhật",
                labelFont
        );

        // ===== INPUTS =====

        txtPatientId = createStyledField(
                inputFont
        );

        txtDiagnosis = createStyledField(
                inputFont
        );

        txtTreatment = createStyledField(
                inputFont
        );

        txtDate = createStyledField(
                inputFont
        );

        // ===== ADD COMPONENTS =====

        mainPanel.add(lblPatientId);

        mainPanel.add(txtPatientId);

        mainPanel.add(lblDiagnosis);

        mainPanel.add(txtDiagnosis);

        mainPanel.add(lblTreatment);

        mainPanel.add(txtTreatment);

        mainPanel.add(lblDate);

        mainPanel.add(txtDate);

        // ===== BUTTON =====

        JButton btnUpdate = new JButton(
                "Cập Nhật Hồ Sơ"
        );

        stylePrimaryButton(btnUpdate);

        btnUpdate.addActionListener(
                e -> updateRecord()
        );

        mainPanel.add(new JLabel());

        mainPanel.add(btnUpdate);

        // ===== WRAPPER =====

        JPanel wrapper = new JPanel(
                new BorderLayout()
        );

        wrapper.setBackground(
                new Color(15, 23, 42)
        );

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        40,
                        40,
                        40
                )
        );

        wrapper.add(mainPanel,
                BorderLayout.CENTER);

        add(wrapper,
                BorderLayout.CENTER);
    }

    private JLabel createLabel(
            String text,
            Font font
    ) {

        JLabel label = new JLabel(text);

        label.setForeground(Color.WHITE);

        label.setFont(font);

        return label;
    }

    private JTextField createStyledField(
            Font font
    ) {

        JTextField field = new JTextField();

        field.setBackground(
                new Color(51, 65, 85)
        );

        field.setForeground(Color.WHITE);

        field.setCaretColor(Color.WHITE);

        field.setFont(font);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(71, 85, 105)
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        return field;
    }

    private void stylePrimaryButton(
            JButton button
    ) {

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
                        20,
                        12,
                        20
                )
        );
    }

    private void updateRecord() {

        if (txtPatientId.getText()
                .trim()
                .isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập mã bệnh nhân!"
            );

            return;
        }

        try (
                Connection conn =
                        DBConnection.connect()
        ) {

            String sql =
                    "UPDATE patient_records " +
                            "SET diagnosis = ?, " +
                            "treatment = ?, " +
                            "record_date = ? " +
                            "WHERE patient_id = ?";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(
                    1,
                    txtDiagnosis.getText().trim()
            );

            ps.setString(
                    2,
                    txtTreatment.getText().trim()
            );

            ps.setString(
                    3,
                    txtDate.getText().trim()
            );

            ps.setString(
                    4,
                    txtPatientId.getText().trim()
            );

            int rows = ps.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cập nhật hồ sơ bệnh nhân thành công!"
                );

                txtPatientId.setText("");

                txtDiagnosis.setText("");

                txtTreatment.setText("");

                txtDate.setText("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Không tìm thấy bệnh nhân!"
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi: " + ex.getMessage()
            );
        }
    }
}