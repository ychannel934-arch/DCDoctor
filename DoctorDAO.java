package com.dcdoctor.database;

import com.dcdoctor.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorDAO {
    public Doctor getDoctorById(int doctorId) {
        // TRỌNG TÂM: Đổi từ 'doctors' thành 'users'
        // Kiểm tra kĩ tên các cột: name hay full_name, specialty có trong bảng users chưa?
        String sql = "SELECT id, full_name, email, role, specialty FROM users WHERE id = ? AND role = 'DOCTOR'";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Khớp với Constructor: (id, name, email, role, specialty)
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("specialty")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn bác sĩ từ bảng users: " + e.getMessage());
        }
        return null;
    }
}