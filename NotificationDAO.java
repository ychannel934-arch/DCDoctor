package com.dcdoctor.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // 1. LẤY DANH SÁCH THÔNG BÁO (Đổ ra màn hình DoctorNotificationUI)
    public static List<String[]> getRecentNotifications() {
        List<String[]> notifs = new ArrayList<>();
        // Lấy 15 thông báo gần nhất
        String sql = "SELECT message, notification_date FROM notifications ORDER BY id DESC LIMIT 15";

        // Lưu ý: Nếu file DBConnection của bạn dùng .getConnection() thì sửa lại chỗ này nhé
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String message = rs.getString("message");
                String time = rs.getString("notification_date");
                if (time == null) time = "Vừa xong";

                // Đẩy vào list mảng String: [Nội dung, Thời gian]
                notifs.add(new String[]{message, time});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifs;
    }

    // 2. TẠO THÔNG BÁO MỚI (Gọi hàm này khi có ai đó gửi câu hỏi)
    public static boolean addNotification(String message) {
        String sql = "INSERT INTO notifications (message, notification_date) VALUES (?, datetime('now','localtime'))";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, message);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. XÓA TOÀN BỘ THÔNG BÁO (Dùng cho nút "Clear All" nếu sau này cần)
    public static boolean clearNotifications() {
        String sql = "DELETE FROM notifications";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate() >= 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}