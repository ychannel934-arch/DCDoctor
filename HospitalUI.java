package com.dcdoctor.UI.hospital;

import com.dcdoctor.UI.LoginUI;
import com.dcdoctor.model.User;

import javax.swing.*;
import java.awt.*;

public class HospitalUI extends JFrame {

    private final User user;

    public HospitalUI(User user) {

        this.user = user;

        setTitle("DC Doctor - Hospital Dashboard");

        setSize(900, 550);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {

        getContentPane().setBackground(
                new Color(15, 23, 42)
        );

        setLayout(new BorderLayout());

        // ===== HEADER =====

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setBackground(
                new Color(30, 41, 59)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titleLabel = new JLabel(
                "🏥 HOSPITAL DASHBOARD"
        );

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        28)
        );

        JLabel welcomeLabel = new JLabel(
                "Welcome, " +
                        (user != null
                                ? user.getFullName()
                                : "Hospital Staff")
        );

        welcomeLabel.setForeground(
                new Color(148, 163, 184)
        );

        welcomeLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        15)
        );

        JPanel textPanel = new JPanel(
                new GridLayout(2, 1)
        );

        textPanel.setOpaque(false);

        textPanel.add(titleLabel);

        textPanel.add(welcomeLabel);

        headerPanel.add(textPanel,
                BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL =====

        JPanel centerPanel = new JPanel(
                new GridLayout(2, 2, 20, 20)
        );

        centerPanel.setBackground(
                new Color(15, 23, 42)
        );

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        // ===== BUTTONS =====

        centerPanel.add(
                createCardButton(
                        "👨‍⚕️",
                        "Tạo tài khoản",
                        "Create doctor account",
                        new Color(59, 130, 246),
                        () -> new CreateDoctorUI()
                                .setVisible(true)
                )
        );

        centerPanel.add(
                createCardButton(
                        "📋",
                        "Update Record",
                        "Update patient record",
                        new Color(16, 185, 129),
                        () -> new UpdateRecordUI()
                                .setVisible(true)
                )
        );

        centerPanel.add(
                createCardButton(
                        "📊",
                        "Statistics",
                        "Hospital analytics",
                        new Color(168, 85, 247),
                        () -> showMessage(
                                "Coming Soon..."
                        )
                )
        );

        centerPanel.add(
                createCardButton(
                        "🚪",
                        "Đăng xuất",
                        "Logout system",
                        new Color(239, 68, 68),
                        this::logout
                )
        );

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createCardButton(
            String icon,
            String title,
            String subtitle,
            Color color,
            Runnable action
    ) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(card,
                        BoxLayout.Y_AXIS)
        );

        card.setBackground(
                new Color(30, 41, 59)
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(51, 65, 85),
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                25,
                                20,
                                25,
                                20
                        )
                )
        );

        card.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        JLabel iconLabel = new JLabel(icon);

        iconLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        40)
        );

        iconLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        18)
        );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitleLabel = new JLabel(subtitle);

        subtitleLabel.setForeground(
                new Color(148, 163, 184)
        );

        subtitleLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        13)
        );

        subtitleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JPanel colorBar = new JPanel();

        colorBar.setMaximumSize(
                new Dimension(1000, 6)
        );

        colorBar.setBackground(color);

        card.add(colorBar);

        card.add(Box.createVerticalStrut(20));

        card.add(iconLabel);

        card.add(Box.createVerticalStrut(15));

        card.add(titleLabel);

        card.add(Box.createVerticalStrut(8));

        card.add(subtitleLabel);

        card.add(Box.createVerticalGlue());

        card.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        action.run();
                    }
                }
        );

        return card;
    }

    private void logout() {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            dispose();

            new LoginUI().setVisible(true);
        }
    }

    private void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message
        );
    }
}