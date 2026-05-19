package com.dcdoctor.database;

import com.dcdoctor.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // 1. KIỂM TRA TRÙNG LỊCH (QUAN TRỌNG: Ngăn chặn 2 bệnh nhân đặt cùng 1 bác sĩ vào 1 giờ)
    public boolean isConflict(int doctorId, String date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // 2. BOOK APPOINTMENT (Trả về boolean để UI biết đường show thông báo)
    public boolean bookAppointment(Appointment a) {
        String sql = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time, notes, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getPatientId());
            stmt.setInt(2, a.getDoctorId());
            stmt.setString(3, a.getAppointmentDate());
            stmt.setString(4, a.getAppointmentTime());
            stmt.setString(5, a.getNotes());
            // Trạng thái mặc định nếu null thì là Pending
            stmt.setString(6, a.getStatus() != null ? a.getStatus() : "Pending");

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi insert lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    // 3. GET ALL APPOINTMENTS (Đã map chuẩn 7 cột cho Constructor mới)
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment a = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("notes"),
                        rs.getString("status")
                );
                list.add(a);
            }

        } catch (Exception e) {
            System.err.println("Lỗi get all lịch hẹn: " + e.getMessage());
        }

        return list;
    }

    // 4. LẤY LỊCH SỬ KHÁM CỦA MỘT BỆNH NHÂN (Có JOIN để lấy tên Bác sĩ)
    public List<String[]> getHistoryByPatient(int patientId) {
        List<String[]> list = new ArrayList<>();
        // JOIN bảng appointments với bảng users (hoặc bảng doctors tùy cấu trúc DB của ông) để lấy tên Bác sĩ
        String sql = "SELECT a.appointment_date, a.appointment_time, u.full_name as doctor_name, a.notes, a.status " +
                "FROM appointments a " +
                "JOIN users u ON a.doctor_id = u.id " + // Giả sử thông tin bác sĩ lưu ở bảng users
                "WHERE a.patient_id = ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("appointment_date");
                row[1] = rs.getString("appointment_time");
                row[2] = rs.getString("doctor_name");
                row[3] = rs.getString("notes");
                row[4] = rs.getString("status");
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy lịch sử: " + e.getMessage());
        }
        return list;
    }

    // 5. LẤY DANH SÁCH LỊCH CHỜ DUYỆT CHO BÁC SĨ
    public List<String[]> getPendingForDoctor(int doctorId) {
        List<String[]> list = new ArrayList<>();
        // JOIN để lấy tên Bệnh nhân từ bảng users
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, u.full_name as patient_name, a.notes " +
                "FROM appointments a " +
                "JOIN users u ON a.patient_id = u.id " +
                "WHERE a.doctor_id = ? AND a.status = 'Pending' " +
                "ORDER BY a.appointment_date ASC, a.appointment_time ASC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("id")); // Lấy ID của lịch hẹn để lát nữa UPDATE
                row[1] = rs.getString("appointment_date");
                row[2] = rs.getString("appointment_time");
                row[3] = rs.getString("patient_name");
                row[4] = rs.getString("notes");
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy lịch chờ duyệt: " + e.getMessage());
        }
        return list;
    }// 6. DUYỆT HOẶC TỪ CHỐI LỊCH HẸN
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi update trạng thái lịch: " + e.getMessage());
            return false;
        }
    }


}