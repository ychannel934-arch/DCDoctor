package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Khớp với cột 'id' (INTEGER, PK) trong ảnh

    @Column(name = "ai_response")
    private String aiResponse; // Khớp với cột 'ai_response' (TEXT) trong ảnh

    @Column(name = "notification")
    private int notification; // Khớp với cột 'notification' (INTEGER) trong ảnh

    public SystemConfig() {}

    public SystemConfig(int id, String aiResponse, int notification) {
        this.id = id;
        this.aiResponse = aiResponse;
        this.notification = notification;
    }

    // ===== GETTERS & SETTERS =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAiResponse() {
        return (aiResponse == null) ? "" : aiResponse;
    }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public int getNotification() { return notification; }
    public void setNotification(int notification) { this.notification = notification; }
}