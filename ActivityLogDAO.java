package com.dcdoctor.database;

import com.dcdoctor.model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    // ── CÁC HÀM CŨ CỦA BẠN ────────────────────────────────────────────

    // Thêm nhật ký hoạt động
    public static boolean addLog(String username,
                                 String role,
                                 String action,
                                 String description) {
        String sql = """
                INSERT INTO activity_logs
                (log_time, username, role, action, description)
                VALUES (datetime('now','localtime'), ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, action);
            ps.setString(4, description);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy toàn bộ nhật ký hoạt động
    public static List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT *
                FROM activity_logs
                ORDER BY log_time DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getString("log_time"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getString("description")
                );

                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    // Xóa toàn bộ nhật ký (tùy chọn)
    public static boolean clearLogs() {
        String sql = "DELETE FROM activity_logs";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate() >= 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════════
    // ── HÀM MỚI DÀNH CHO LUỒNG BÁC SĨ (DOCTOR DASHBOARD) ────────────
    // ════════════════════════════════════════════════════════════════

    // LẤY NHẬT KÝ HOẠT ĐỘNG (Dữ liệu trả về phù hợp với giao diện UI)
    public static List<String[]> getRecentActivities() {
        List<String[]> logs = new ArrayList<>();
        // Lấy 5 hoạt động gần nhất của role 'doctor'
        String sql = "SELECT log_time, description FROM activity_logs WHERE role = 'doctor' ORDER BY log_id DESC LIMIT 5";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String time = rs.getString("log_time");
                if (time == null) time = "Vừa xong";

                logs.add(new String[]{
                        "green", // Màu chấm tròn trên giao diện
                        rs.getString("description"),
                        time
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
}