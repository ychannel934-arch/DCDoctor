package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_transactions")
public class QATransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String patientName;

    private String doctorName;

    @Column(length = 1000)
    private String question;

    private LocalDateTime transactionDate;

    private String status;

    // Constructor rỗng (bắt buộc)
    public QATransaction() {}

    public QATransaction(String patientName,
                         String doctorName,
                         String question,
                         LocalDateTime transactionDate,
                         String status) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.question = question;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    // Getters & Setters

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}