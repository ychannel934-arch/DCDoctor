package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;

    private String doctorName;

    private LocalDateTime date;

    // Constructor rỗng (bắt buộc)
    public Appointment() {}

    public Appointment(Long patientId, String doctorName, LocalDateTime date) {
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.date = date;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}