package com.dcdoctor.database;

import com.dcdoctor.model.Chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    // ── ĐÃ SỬA: Nhận 2 tham số, chèn thêm status vào DB ─────────────────
    public void saveChat(Chat chat, String status) {
        String sql = "INSERT INTO chat(patient_id, message, response, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, chat.getPatientId());
            stmt.setString(2, chat.getMessage());
            stmt.setString(3, chat.getResponse());
            stmt.setString(4, status); // Chèn trạng thái (PENDING / AI_ANSWERED)

            stmt.executeUpdate();
            System.out.println("Saved chat with status: " + status);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Lấy toàn bộ lịch sử ──────────────────────────
    public List<Chat> getAllChats() {
        List<Chat> list = new ArrayList<>();
        // ĐÃ SỬA: Đổi DESC thành ASC để tin nhắn hiển thị đúng thứ tự từ trên xuống dưới
        String sql = "SELECT * FROM chat ORDER BY id ASC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Chat c = new Chat(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getString("message"),
                        rs.getString("response")
                );
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ════════════════════════════════════════════════════════════════
    // ── CÁC HÀM LUỒNG BÁC SĨ (ĐÃ ĐỒNG BỘ VỚI DB CŨ) ─────────
    // ════════════════════════════════════════════════════════════════

    // 1. Lấy thống kê cho màn hình Dashboard (Check theo cột status)
    public int[] getDashboardStats() {
        int[] stats = new int[]{0, 0, 0};

        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending,
                SUM(CASE WHEN status IN ('DOC_ANSWERED', 'AI_ANSWERED') THEN 1 ELSE 0 END) as answered
            FROM chat
        """;

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("pending");
                stats[2] = rs.getInt("answered");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // 2. Lấy danh sách câu hỏi chưa trả lời (Check theo status = 'PENDING')
    // 2. Lấy danh sách câu hỏi chưa trả lời (Đã sửa: Gom nhóm theo patient_id)
    public List<Chat> getPendingQuestions() {
        List<Chat> list = new ArrayList<>();

        // ĐÃ SỬA: Dùng Subquery để gom nhóm, chỉ lấy tin nhắn mới nhất của từng bệnh nhân
        String sql = """
            SELECT * FROM chat 
            WHERE id IN (
                SELECT MAX(id) 
                FROM chat 
                WHERE status = 'PENDING' 
                GROUP BY patient_id
            ) 
            ORDER BY id DESC
        """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Chat(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getString("message"),
                        rs.getString("response")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Bác sĩ trả lời + Ghi Log hệ thống
    public boolean replyPatient(int chatId, String responseText, String doctorUsername) {
        String updateChat = "UPDATE chat SET response = ?, status = 'DOC_ANSWERED' WHERE id = ?";
        String insertLog = "INSERT INTO activity_logs (log_time, username, role, action, description) VALUES (datetime('now','localtime'), ?, 'doctor', 'Reply', 'Đã trả lời câu hỏi ID: ' || ?)";

        try (Connection conn = DBConnection.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateChat)) {
                ps1.setString(1, responseText);
                ps1.setInt(2, chatId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(insertLog)) {
                ps2.setString(1, doctorUsername);
                ps2.setInt(2, chatId);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}