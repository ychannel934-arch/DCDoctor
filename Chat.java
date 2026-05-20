package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;

    private Long doctorId; // Khớp với cột 'doctor_id' trong SQLite

    @Column(length = 2000)
    private String message;

    @Column(length = 2000)
    private String response;

    private String status; // Khớp với cột 'status' dùng để countByStatus("READ")

    private LocalDateTime createdAt;

    private int isAi;

    // Constructor rỗng (Bắt buộc phải có cho JPA/Hibernate)
    public Chat() {}

    // Constructor đầy đủ tham số để tiện dùng khi tạo mới dữ liệu
    public Chat(Long patientId, Long doctorId, String message, String response, String status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.message = message;
        this.response = response;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // --- GETTERS & SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status; // Đã sửa từ lỗi gán nhầm 'status = status'
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsAi() { return isAi; }
    public void setIsAi(int isAi) { this.isAi = isAi; }
}