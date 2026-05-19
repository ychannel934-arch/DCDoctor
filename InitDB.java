package com.dcdoctor.database;

import java.sql.Connection;
import java.sql.Statement;

public class InitDB {

    public static void init() {
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            // ================= USERS =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_code TEXT UNIQUE,
                        full_name TEXT,
                        email TEXT UNIQUE,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL,
                        specialty TEXT
                    )
                    """);

            // ================= ZALO USERS =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS zalo_users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        zalo_id TEXT UNIQUE NOT NULL,
                        phone TEXT,
                        avatar TEXT
                    )
                    """);

            // ================= APPOINTMENTS (Đã chuẩn hóa) =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS appointments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient_id INTEGER NOT NULL,
                        doctor_id INTEGER NOT NULL,
                        appointment_date TEXT NOT NULL,
                        appointment_time TEXT NOT NULL,
                        notes TEXT,
                        status TEXT DEFAULT 'Pending'
                    )
                    """);

            // ================= CHAT (Đã chuẩn hóa) =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS chat (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient_id INTEGER NOT NULL,
                        doctor_id INTEGER,
                        message TEXT NOT NULL,
                        response TEXT,
                        status TEXT DEFAULT 'PENDING',
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            // ================= MEDIA =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS media (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT,
                        category TEXT,
                        content TEXT
                    )
                    """);

            // ================= SYSTEM CONFIG =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS system_config (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        ai_response TEXT,
                        notification INTEGER
                    )
                    """);

            // ================= PATIENT RECORDS (Đã chuẩn hóa) =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS patient_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient_id INTEGER NOT NULL,
                        doctor_id INTEGER NOT NULL,
                        diagnosis TEXT,
                        treatment TEXT,
                        medication TEXT,
                        record_date TEXT
                    )
                    """);

            // ================= NOTIFICATIONS (Thêm patient_id & is_read cho Real-time) =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient_id INTEGER NOT NULL,
                        message TEXT,
                        is_read INTEGER DEFAULT 0,
                        notification_date TEXT
                    )
                    """);

            // ================= QA TRANSACTIONS =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS qa_transactions (
                        transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient_name TEXT NOT NULL,
                        doctor_name TEXT,
                        question TEXT NOT NULL,
                        transaction_date TEXT,
                        status TEXT
                    )
                    """);

            // ================= ACTIVITY LOGS =================
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS activity_logs (
                        log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        log_time TEXT,
                        username TEXT,
                        role TEXT,
                        action TEXT,
                        description TEXT
                    )
                    """);

            // ================= USERS DATA (Đã sửa lỗi dư cột patient_id) =================
            stmt.execute("""
                    INSERT OR IGNORE INTO users
                    (full_name, email, username, password, role, specialty)
                    VALUES
                    ('Nguyễn Gia Huy','huy123@gmail.com','huy', '123', 'PATIENT', NULL),
                    ('Dr. Trần Minh','minhtran1@gmail.com','doctor1', '123', 'DOCTOR', 'Nội tiết'),
                    ('Administrator', 'admin90@gmail.com','admin','123', 'ADMIN', NULL),
                    ('Bệnh viện Trung Tâm','BVTT123@gmail.com','hospital1', '123', 'HOSPITAL', NULL)
                    """);

            // ================= NOTIFICATIONS DATA (Sửa lại cho khớp cấu trúc) =================
            stmt.execute("""
                    INSERT INTO notifications
                    (patient_id, message, is_read, notification_date)
                    VALUES
                    (1, 'Chào mừng bạn đến với hệ thống DC Doctor', 0, date('now','localtime')),
                    (1, 'Hệ thống vừa được cập nhật phiên bản mới', 0, date('now','localtime'))
                    """);

            // ================= ACTIVITY LOG SAMPLE =================
            stmt.execute("""
                    INSERT OR IGNORE INTO activity_logs
                    (log_id, log_time, username, role, action, description)
                    VALUES
                    (1,
                     datetime('now','localtime'),
                     'admin90@gmail.com',
                     'ADMIN',
                     'Đăng nhập',
                     'Quản trị viên đăng nhập vào hệ thống')
                    """);

            System.out.println("====================================");
            System.out.println("✅ DATABASE READY!");
            System.out.println("✅ ZALO LOGIN READY!");
            System.out.println("====================================");

        } catch (Exception e) {
            System.err.println("❌ DATABASE INIT ERROR:");
            e.printStackTrace();
        }
    }
}