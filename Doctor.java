package com.dcdoctor.model;

public class Doctor extends User {

    private String specialty;

    public Doctor(int id, String full_name, String email, String role) {
        super(id, full_name, email, role);
    }

    public Doctor(int id, String full_name, String email, String role, String specialty) {
        super(id, full_name, email, role);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    // Ví dụ hàm xử lý câu hỏi
    public String answerQuestion(String question) {
        return "Doctor trả lời: " + question;
    }
}