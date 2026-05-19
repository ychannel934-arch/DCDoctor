package com.dcdoctor.UI;

import com.dcdoctor.UI.doctor.DoctorUI; // Đã thêm Import
import com.dcdoctor.UI.hospital.HospitalUI;
import com.dcdoctor.database.UserDAO;
import com.dcdoctor.model.User;

import javax.swing.*;
import java.awt.*;

public class StaffLoginUI extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final String role;

    // Biến toàn cục (Global variable)
    private User user;

    public StaffLoginUI(String role) {
        this.role = role;

        setTitle(role + " Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));

        // ================= TITLE =================
        JLabel titleLabel = new JLabel(role + " LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        // ================= FORM =================
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        formPanel.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        // ================= BUTTON =================
        JButton loginButton = new JButton("Đăng nhập");
        JButton backButton = new JButton("Quay lại");

        styleButton(loginButton);
        styleButton(backButton);

        loginButton.addActionListener(e -> login());
        backButton.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        // ================= ADD =================
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ================= LOGIN =================
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        UserDAO dao = new UserDAO();

        // ĐÃ SỬA: Xóa chữ "User" để gán thẳng vào biến toàn cục của class, tránh sập app
        this.user = dao.login(username, password);

        if (this.user == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            return;
        }

        // check role
        if (!this.user.getRole().equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "Tài khoản không thuộc quyền " + role);
            return;
        }

        // ================= RULE SIMPLE =================
        if (role.equalsIgnoreCase("ADMIN")) {
            if (!username.contains("@gmail.com")) {
                JOptionPane.showMessageDialog(this, "Admin bắt buộc đăng nhập bằng Gmail!");
                return;
            }
        } else {
            if (username.contains("@gmail.com")) {
                JOptionPane.showMessageDialog(this, role + " không được dùng Gmail!");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Đăng nhập " + role + " thành công!");

        dispose();
        openDashboard();
    }

    // ================= OPEN UI =================
    private void openDashboard() {
        switch (role.toUpperCase()) {
            case "DOCTOR":
                // ĐÃ SỬA: Bóc đúng cái ID ra để truyền vào thay vì truyền nguyên cục User
                new DoctorUI(this.user.getId()).setVisible(true);
                break;


            case "HOSPITAL":
                new HospitalUI(this.user).setVisible(true);
                break;
        }
    }

    // ================= STYLE =================
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
    }
}