package com.dcdoctor.database;

import com.dcdoctor.model.ZaloUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ZaloDAO {

    // Lưu hoặc cập nhật tài khoản từ Zalo OAuth
    public static boolean saveOrUpdate(ZaloUser user) {
        String checkSql = "SELECT id FROM zalo_users WHERE zalo_id = ?";

        String insertSql = """
                INSERT INTO zalo_users
                (name, zalo_id, phone, avatar)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.connect()) {

            // Kiểm tra đã tồn tại chưa
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setString(1, user.getZaloId());

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Cập nhật thông tin
                String updateSql = """
                        UPDATE zalo_users
                        SET name = ?, phone = ?, avatar = ?
                        WHERE zalo_id = ?
                        """;

                PreparedStatement update = conn.prepareStatement(updateSql);
                update.setString(1, user.getName());
                update.setString(2, user.getPhone());
                update.setString(3, user.getAvatar());
                update.setString(4, user.getZaloId());

                return update.executeUpdate() > 0;
            }

            // Thêm mới
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getZaloId());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAvatar());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm theo Zalo ID
    public static ZaloUser findByZaloId(String zaloId) {
        String sql = "SELECT * FROM zalo_users WHERE zalo_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, zaloId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ZaloUser(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("zalo_id"),
                        rs.getString("phone"),
                        "",
                        rs.getString("avatar")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Tìm theo số điện thoại
    public static ZaloUser findByPhone(String phone) {
        String sql = "SELECT * FROM zalo_users WHERE phone = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ZaloUser(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("zalo_id"),
                        rs.getString("phone"),
                        "",
                        rs.getString("avatar")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}