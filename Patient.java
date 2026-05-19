package com.dcdoctor.model;

public class Patient extends User {

    private String medicalHistory;

    public Patient(int id, String name, String email, String medicalHistory) {
        super(id, name, email, "PATIENT"); // 🔥 FIX
        this.medicalHistory = medicalHistory;
    }

    public void login() {
        System.out.println("Patient logged in");
    }

    public void viewRecord() {
        System.out.println("History: " + medicalHistory);
    }
}