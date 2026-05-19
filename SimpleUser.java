package com.dcdoctor.model;

public class SimpleUser extends User {

    public SimpleUser(int id, String name, String email, String role) {
        super(id, name, email, role);
    }


    public void login() {
        System.out.println("Login success");
    }
}