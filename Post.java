package com.dcdoctor.adminserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "media") // Khớp hoàn toàn với bảng media trong SQLite
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Đổi sang int để khớp hoàn toàn với kiểu INTEGER của SQLite

    @Column(nullable = false)
    private String title;

    private String category; // Khớp với cột category trong DB

    @Column(columnDefinition = "TEXT")
    private String content;

    // Constructor rỗng (Bắt buộc cho JPA)
    public Post() {}

    public Post(String title, String category, String content) {
        this.title = title;
        this.category = category;
        this.content = content;
    }

    // --- GETTERS AND SETTERS ---
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}