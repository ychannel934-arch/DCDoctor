package com.dcdoctor.model;

public class User {

    protected int id;
    protected String name;
    protected String email;
    protected String role;

    // Constructor rỗng
    public User() {}

    // Constructor đầy đủ
    public User(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // ================= GETTER =================
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    // ================= SETTER =================
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ================= DEBUG =================
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}