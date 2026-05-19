package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activity_logs") // Đảm bảo tên bảng này có trong SQLite của bạn
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId; // Đổi từ Long sang int cho khớp Swing

    private String logTime; // Đổi từ LocalDateTime sang String cho khớp Swing

    private String username;
    private String role;
    private String action;

    @Column(length = 1000)
    private String description;

    public ActivityLog() {}

    // Getters & Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public String getLogTime() { return logTime; }
    public void setLogTime(String logTime) { this.logTime = logTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}