package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PatientNotificationUI extends JFrame {

    private final String patientId;
    private JTextArea notificationArea;
    private JButton refreshButton;
    private JButton clearButton;
    private JButton closeButton;

    public PatientNotificationUI(String patientId) {
        this.patientId = patientId;

        setTitle("Thông Báo Bệnh Nhân");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        createTableIfNotExists();
        loadNotifications();

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(
                "THÔNG BÁO CỦA BẠN",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(new Font("Arial", Font.PLAIN, 15));
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(notificationArea);

        refreshButton = new JButton("Làm Mới");
        clearButton = new JButton("Xóa Màn Hình");
        closeButton = new JButton("Đóng");

        styleButton(refreshButton);
        styleButton(clearButton);
        styleButton(closeButton);

        refreshButton.addActionListener(e -> loadNotifications());

        clearButton.addActionListener(e -> {
            notificationArea.setText("");
            JOptionPane.showMessageDialog(
                    this,
                    "Đã xóa thông báo trên màn hình."
            );
        });

        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createTableIfNotExists() {
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Tạo bảng nếu chưa tồn tại
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id TEXT NOT NULL,
                    message TEXT NOT NULL,
                    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Kiểm tra cấu trúc bảng hiện tại
            boolean hasPatientId = false;
            boolean hasDate = false;

            ResultSet rs = stmt.executeQuery(
                    "PRAGMA table_info(notifications)"
            );

            while (rs.next()) {
                String columnName = rs.getString("name");

                if ("patient_id".equalsIgnoreCase(columnName)) {
                    hasPatientId = true;
                }

                if ("date".equalsIgnoreCase(columnName)) {
                    hasDate = true;
                }
            }
            rs.close();

            // Thêm cột patient_id nếu thiếu
            if (!hasPatientId) {
                stmt.execute("""
                    ALTER TABLE notifications
                    ADD COLUMN patient_id TEXT
                    """);
            }

            // Thêm cột date nếu thiếu
            if (!hasDate) {
                stmt.execute("""
                    ALTER TABLE notifications
                    ADD COLUMN date TIMESTAMP
                    """);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi tạo bảng thông báo: " + e.getMessage()
            );
        }
    }
    private void loadNotifications() {
        notificationArea.setText("");

        try (Connection conn = DBConnection.connect()) {

            String sql = """
                    SELECT message, date
                    FROM notifications
                    WHERE patient_id = ?
                    ORDER BY date DESC
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, patientId);

            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                notificationArea.append(
                        "[" + rs.getString("date") + "]\n"
                                + rs.getString("message")
                                + "\n\n"
                );
            }

            if (!hasData) {
                notificationArea.setText(
                        "Hiện chưa có thông báo nào."
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải thông báo: " + e.getMessage()
            );
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(160, 40));
        button.setFocusPainted(false);
    }
}