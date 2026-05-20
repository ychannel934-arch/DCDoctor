package com.dcdoctor.UI;

import com.dcdoctor.UI.doctor.DoctorUI;
import com.dcdoctor.UI.patient.PatientUI;
import com.dcdoctor.database.UserDAO;
import com.dcdoctor.model.User;
import com.dcdoctor.UI.hospital.HospitalUI;
import javax.swing.ImageIcon;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginUI() {

        setTitle("DC Doctor - Login");
        setSize(520, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== SỬA LẠI ĐOẠN NÀY =====
        try {
            // Dấu gạch chéo đầu tiên '/' đại diện cho thư mục tài nguyên gốc (resources)
            java.net.URL iconURL = getClass().getResource("/icons/img.png");

            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                System.out.println("Không tìm thấy file ảnh! Hãy kiểm tra lại tên file hoặc thư mục.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout(10,10));

        // ===== TITLE =====
        JLabel title = new JLabel("DC DOCTOR SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // ===== CENTER LOGIN =====
        JPanel center = new JPanel(new GridLayout(5,1,10,10));

        txtUser = new JTextField();
        txtUser.setBorder(BorderFactory.createTitledBorder("Tài khoản"));

        txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Mật khẩu"));

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnRegister = new JButton("Đăng ký bệnh nhân");
        JButton btnZalo = new JButton("Đăng nhập / Đăng ký Zalo");

        center.add(txtUser);
        center.add(txtPass);
        center.add(btnLogin);
        center.add(btnRegister);
        center.add(btnZalo);

        // ===== INTERNAL LOGIN =====
        JLabel internalLabel = new JLabel("Đăng nhập nội bộ", SwingConstants.CENTER);
        internalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton doctor = new JButton("Doctor");
        JButton hospital = new JButton("Hospital");

        JPanel rolePanel = new JPanel(new GridLayout(1,3,10,10));
        rolePanel.add(doctor);
        rolePanel.add(hospital);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(internalLabel, BorderLayout.NORTH);
        bottom.add(rolePanel, BorderLayout.CENTER);

        // ===== ADD TO FRAME =====
        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnLogin.addActionListener(e -> login());

        btnRegister.addActionListener(e ->
                new RegisterUI().setVisible(true)
        );

        btnZalo.addActionListener(e ->
                new ZaloLoginUI().setVisible(true)
        );

        doctor.addActionListener(e -> {
            dispose();
            new StaffLoginUI("doctor").setVisible(true);
        });

        hospital.addActionListener(e -> {
            dispose();
            new StaffLoginUI("hospital").setVisible(true);
        });
    }

    private void login() {

        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Vui lòng nhập đầy đủ!");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if(user == null){
            JOptionPane.showMessageDialog(this,"Sai tài khoản hoặc mật khẩu!");
            return;
        }

        dispose();

        switch (user.getRole().toLowerCase()) {

            case "patient":
                // Bóc tách ID và Tên truyền vào thay vì truyền cục user
                new PatientUI(user.getId(), user.getFullName()).setVisible(true);
                break;

            case "doctor":
                new DoctorUI(user.getId()).setVisible(true);
                break;


            case "hospital":
                new HospitalUI(user).setVisible(true);
                break;

            default:
                JOptionPane.showMessageDialog(this,"Vai trò không hợp lệ!");
        }
    }

}