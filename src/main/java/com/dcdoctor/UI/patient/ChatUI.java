package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ChatUI extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;

    public ChatUI() {
        setTitle("Chat Hỏi Đáp Tiểu Đường");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        JButton btnSend = new JButton("Gửi");

        btnSend.addActionListener(e -> sendQuestion());

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void sendQuestion() {
        String question = inputField.getText().trim();

        if (question.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập câu hỏi!");
            return;
        }

        chatArea.append("Bạn: " + question + "\n");

        String answer = findAnswer(question);

        if (answer != null) {

            // có dữ liệu → trả lời AI giả lập
            chatArea.append("AI: " + answer + "\n\n");

        } else {

            // KHÔNG có dữ liệu → lấy SystemConfig
            String fallback = getAIConfig();

            chatArea.append("AI: " + fallback + "\n");

            chatArea.append(
                    "Hệ thống: Câu hỏi đã được chuyển đến bác sĩ.\n\n"
            );
        }

        inputField.setText("");
    }

    // ================= AI LOGIC GIẢ =================
    private String findAnswer(String question) {
        question = question.toLowerCase();

        if (question.contains("tiểu đường")) {
            return "Tiểu đường là bệnh rối loạn chuyển hóa đường huyết.";
        }

        if (question.contains("insulin")) {
            return "Insulin là hormone giúp kiểm soát lượng đường trong máu.";
        }

        if (question.contains("ăn gì")) {
            return "Người bệnh nên ăn rau xanh, ngũ cốc nguyên hạt và hạn chế đồ ngọt.";
        }

        return null;
    }

    // ================= GET SYSTEM CONFIG =================
    private String getAIConfig() {
        String fallback = "Vui lòng chờ phản hồi từ bác sĩ.";

        try (Connection conn = DBConnection.connect()) {

            String sql = "SELECT ai_response FROM system_config ORDER BY id DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                fallback = rs.getString("ai_response");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fallback;
    }


}