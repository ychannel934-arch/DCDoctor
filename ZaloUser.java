package com.dcdoctor.model;

public class ZaloUser {

    private int id;
    private String zaloId;
    private String name;
    private String phone;
    private String password;
    private String avatar;

    // Constructor đầy đủ
    public ZaloUser(int id, String zaloId, String name,
                    String phone, String password, String avatar) {
        this.id = id;
        this.zaloId = zaloId;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.avatar = avatar;
    }

    // Đăng ký thủ công
    public ZaloUser(String name, String phone,
                    String password, String avatar) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.avatar = avatar;
        this.zaloId = null;
    }

    // Đăng nhập thủ công
    public ZaloUser(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Đăng nhập bằng Zalo OAuth
    // Dùng boolean để tránh trùng constructor
    public ZaloUser(String zaloId, String name,
                    String phone, String avatar,
                    boolean isZaloLogin) {
        this.zaloId = zaloId;
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
        this.password = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZaloId() {
        return zaloId == null ? "" : zaloId;
    }

    public void setZaloId(String zaloId) {
        this.zaloId = zaloId;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar == null ? "" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ZaloUser{" +
                "id=" + id +
                ", zaloId='" + zaloId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}