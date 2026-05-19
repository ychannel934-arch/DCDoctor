package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Giữ nguyên để map với DB nhưng không bắt buộc phải có dữ liệu
    @Column(name = "user_code")
    private String userCode;

    @Column(name = "full_name")
    private String name;

    private String email;

    // BỎ nullable = false để tránh việc Hibernate kén chọn dữ liệu cũ trong SQLite
    private String username;
    private String password;

    private String role;
    private String specialty;

    public User() {}

    public User(int id, String userCode, String name, String email, String username, String password, String role, String specialty) {
        this.id = id;
        this.userCode = userCode;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.specialty = specialty;
    }

    // ===== GETTERS & SETTERS (Giữ nguyên logic xử lý null của bạn là rất tốt) =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() {
        return (name == null || name.isEmpty() || name.equals("(null)")) ? "Người dùng hệ thống" : name;
    }
    public void setName(String name) { this.name = name; }

    public String getUsername() {
        return (username == null || username.equals("(null)")) ? "" : username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return (password == null || password.equals("(null)")) ? "" : password;
    }
    public void setPassword(String password) { this.password = password; }

    public String getRole() {
        return (role == null || role.equals("(null)")) ? "USER" : role.toUpperCase();
    }
    public void setRole(String role) { this.role = role; }

    public String getSpecialty() {
        return (specialty == null || specialty.equals("(null)")) ? "N/A" : specialty;
    }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getUserCode() {
        return (userCode == null || userCode.equals("(null)")) ? "" : userCode;
    }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getEmail() {
        return (email == null || email.equals("(null)")) ? "" : email;
    }
    public void setEmail(String email) { this.email = email; }
}