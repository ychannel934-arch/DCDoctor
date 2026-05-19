package com.dcdoctor.database;

import com.dcdoctor.model.*;

import java.sql.*;

public class UserDAO {

    // ================= LOGIN (Đã tối ưu SQL) =================
    public User login(String account, String password) {
        // Viết SQL chuẩn: Lọc ngay từ Database, kiểm tra tài khoản (hoặc email) VÀ mật khẩu
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account);
            ps.setString(2, account);
            ps.setString(3, password);

            ResultSet rs = ps.executeQuery();

            // Lọc chuẩn rồi thì chỉ có 1 kết quả, dùng if thay vì while
            if (rs.next()) {
                String role = rs.getString("role");
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");

                switch (role.toLowerCase()) {
                    case "patient":
                        return new Patient(id, fullName, email, role);
                    case "doctor":
                        return new Doctor(id, fullName, email, role);
                    case "hospital":
                        return new Hospital(id, fullName, email, role);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // ================= REGISTER PATIENT =================
    public boolean registerPatient(String fullName,
                                   String username,
                                   String email,
                                   String password) {

        String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";

        // ĐÃ SỬA: Thêm cột patient_id vào câu lệnh INSERT
        String insertSql =
                "INSERT INTO users " +
                        "(user_code, full_name, username, email, password, role, specialty) " +
                        "VALUES (?, ?, ?, ?, ?, 'patient', NULL)";

        try (Connection conn = DBConnection.connect()) {

            // Kiểm tra trùng username hoặc email
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, username);
                check.setString(2, email);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    return false; // Bị trùng
                }
            }

            // Sinh mã patient_id tự động (Ví dụ: P1715690000000)
            String generatePatientId = "P" + System.currentTimeMillis();

            // Tiến hành thêm mới
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, generatePatientId);
                ps.setString(2, fullName);
                ps.setString(3, username);
                ps.setString(4, email);
                ps.setString(5, password);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}