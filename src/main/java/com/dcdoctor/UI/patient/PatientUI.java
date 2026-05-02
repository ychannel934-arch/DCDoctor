package com.dcdoctor.UI.patient;

import com.dcdoctor.UI.*;
import com.dcdoctor.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PatientUI extends JFrame {

    private final String patientId;

    public PatientUI(String patientId) {
        this.patientId = patientId;

        setTitle("DC Doctor - Patient");
        setSize(430, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();

        setVisible(true);
    }

    public PatientUI(User user) {
        this("");
    }

    private void initComponents() {
        Color background = new Color(248, 248, 252);
        Color cardColor = new Color(236, 233, 245);
        Color primary = new Color(98, 76, 166);
        Color textColor = new Color(70, 52, 130);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(background);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(background);
        headerPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("👋 Xin chào");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(primary);

        JButton profileButton = new JButton("👤");
        profileButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        profileButton.setPreferredSize(new Dimension(55, 55));
        profileButton.setFocusPainted(false);
        profileButton.setBorderPainted(false);
        profileButton.setBackground(new Color(225, 213, 255));
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(profileButton, BorderLayout.EAST);

        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(background);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(0, 15, 15, 15));

        menuPanel.add(createMenuCard("Tin nhắn", "💬",
                () -> new ChatUI().setVisible(true),
                cardColor, textColor));

        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuCard("Đặt lịch khám", "📅",
                () -> new BookingUI().setVisible(true),
                cardColor, textColor));

        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuCard("Thông tin sức khỏe", "📊",
                () -> new MediaBrowseUI().setVisible(true),
                cardColor, textColor));

        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuCard("Hồ sơ bệnh án", "🩺",
                () -> new RecordUI().setVisible(true),
                cardColor, textColor));

        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuCard("Thông báo", "🔔",
                () -> new PatientNotificationUI(patientId).setVisible(true),
                cardColor, textColor));

        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuCard("Lịch sử đặt lịch", "📋",
                () -> new BookingHistoryUI().setVisible(true),
                cardColor, textColor));

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(background);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom Navigation
        JPanel bottomNav = new JPanel(new GridLayout(1, 4));
        bottomNav.setPreferredSize(new Dimension(0, 70));
        bottomNav.setBackground(Color.WHITE);

        bottomNav.add(createNavButton("🏠"));
        bottomNav.add(createNavButton("💬"));
        bottomNav.add(createNavButton("🔔"));

        JButton logoutButton = createNavButton("🚪");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
        bottomNav.add(logoutButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomNav, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createMenuCard(String title,
                                  String icon,
                                  Runnable action,
                                  Color bgColor,
                                  Color textColor) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setPreferredSize(new Dimension(370, 110));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));

        card.add(titleLabel, BorderLayout.WEST);
        card.add(iconLabel, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private JButton createNavButton(String icon) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}