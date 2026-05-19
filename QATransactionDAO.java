// ==========================================
// File: QATransactionDAO.java
// Package: com.dcdoctor.database
// ==========================================
package com.dcdoctor.database;

import com.dcdoctor.model.QATransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QATransactionDAO {

    // Thêm giao dịch hỏi đáp mới
    public static boolean addTransaction(String patientName,
                                         String doctorName,
                                         String question,
                                         String status) {
        String sql = """
                INSERT INTO qa_transactions
                (patient_name, doctor_name, question, transaction_date, status)
                VALUES (?, ?, ?, datetime('now','localtime'), ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patientName);
            ps.setString(2, doctorName);
            ps.setString(3, question);
            ps.setString(4, status);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy toàn bộ giao dịch
    public static List<QATransaction> getAllTransactions() {
        List<QATransaction> list = new ArrayList<>();

        String sql = """
                SELECT * FROM qa_transactions
                ORDER BY transaction_date DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new QATransaction(
                        rs.getInt("transaction_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("question"),
                        rs.getString("transaction_date"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Cập nhật trạng thái giao dịch
    public static boolean updateStatus(int transactionId, String status) {
        String sql = """
                UPDATE qa_transactions
                SET status = ?
                WHERE transaction_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, transactionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}