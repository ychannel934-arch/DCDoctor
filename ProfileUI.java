package com.dcdoctor.UI.patient;

import com.dcdoctor.UI.LoginUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileUI extends JPanel {

    public ProfileUI() {
        // Tái sử dụng màu nền từ PatientUI
        setBackground(PatientUI.BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(24, 16, 24, 16));

        // 1. Header (Avatar & Thông tin)
        add(buildProfileHeader());
        add(Box.createVerticalStrut(30));

        // Tiêu đề section
        add(buildSectionLabel("Cài đặt tài khoản"));

        // 2. Các thẻ chức năng (Bo góc, đồng bộ với Service Grid)
        add(buildMenuOption("👤", "Thông tin cá nhân", "Cập nhật dữ liệu định danh", PatientUI.PRIMARY));
        add(Box.createVerticalStrut(12));
        add(buildMenuOption("🔒", "Bảo mật & Đăng nhập", "Đổi mật khẩu, quản lý phiên", PatientUI.WARN));
        add(Box.createVerticalStrut(12));
        add(buildMenuOption("🎧", "Trợ giúp & Hỗ trợ", "Liên hệ bác sĩ hoặc admin", PatientUI.TEAL));

        // Đẩy nút đăng xuất xuống dưới cùng (nếu cửa sổ dài)
        add(Box.createVerticalGlue());

        // 3. Nút Đăng xuất
        add(buildLogoutButton());
    }

    private JPanel buildProfileHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Avatar (Khối tròn bằng Graphics2D)
        JPanel avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PatientUI.PRIMARY); // Nền xanh dương
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(84, 84));
        avatarPanel.setMaximumSize(new Dimension(84, 84));
        avatarPanel.setLayout(new BorderLayout());

        JLabel initial = new JLabel("B", SwingConstants.CENTER); // Lấy chữ cái đầu của tên
        initial.setFont(new Font("Segoe UI", Font.BOLD, 36));
        initial.setForeground(PatientUI.WHITE);
        avatarPanel.add(initial, BorderLayout.CENTER);

        // Tên
        JLabel nameLbl = new JLabel("Bệnh nhân Demo");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLbl.setForeground(PatientUI.TEXT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLbl.setBorder(new EmptyBorder(12, 0, 2, 0));

        // SĐT hoặc Zalo ID
        JLabel idLbl = new JLabel("0987 654 321");
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLbl.setForeground(PatientUI.MUTED);
        idLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(avatarPanel);
        panel.add(nameLbl);
        panel.add(idLbl);

        return panel;
    }

    private JPanel buildSectionLabel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 4, 10, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(PatientUI.TEXT);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel buildMenuOption(String icon, String title, String subTitle, Color accentColor) {
        // Thẻ chứa bo góc (Giống Activity Item của bạn)
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PatientUI.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PatientUI.BORDER_C);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon bọc trong ô vuông bo góc nền nhạt
        JPanel iconWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        iconWrapper.setOpaque(false);
        iconWrapper.setPreferredSize(new Dimension(46, 46));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconWrapper.add(iconLbl, BorderLayout.CENTER);

        // Text
        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(PatientUI.TEXT);

        JLabel subLbl = new JLabel(subTitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(PatientUI.MUTED);

        textCol.add(titleLbl);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(subLbl);

        // Nút mũi tên
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        arrow.setForeground(PatientUI.SUB);

        card.add(iconWrapper, BorderLayout.WEST);
        card.add(textCol, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);

        return card;
    }

    private JPanel buildLogoutButton() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Nút đăng xuất style viền đỏ (Outline Button)
        JButton btnLogout = new JButton("Đăng xuất") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(PatientUI.DANGER.getRed(), PatientUI.DANGER.getGreen(), PatientUI.DANGER.getBlue(), 15)); // Nền đỏ rất nhạt
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PatientUI.DANGER); // Viền đỏ đậm
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogout.setForeground(PatientUI.DANGER);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Đóng app hiện tại
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
                // Mở lại Login
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });

        panel.add(btnLogout, BorderLayout.CENTER);
        return panel;
    }
}