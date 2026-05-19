package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientUI extends JFrame {

    // ── Màu sắc mobile ───────────────────────────────────
    static final Color BG        = new Color(0xF8FAFF);
    static final Color WHITE     = Color.WHITE;
    static final Color PRIMARY   = new Color(0x3B82F6);
    static final Color PRIMARY2  = new Color(0x6366F1);
    static final Color SUCCESS   = new Color(0x10B981);
    static final Color WARN      = new Color(0xF59E0B);
    static final Color DANGER    = new Color(0xEF4444);
    static final Color PURPLE    = new Color(0x8B5CF6);
    static final Color TEAL      = new Color(0x14B8A6);
    static final Color TEXT      = new Color(0x1E293B);
    static final Color MUTED     = new Color(0x64748B);
    static final Color SUB       = new Color(0x94A3B8);
    static final Color BORDER_C  = new Color(0xE2E8F0);

    private int patientId;
    private final String patientName;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private JButton activeNavBtn;

    // THÊM: Bộ đếm thời gian cho thông báo real-time
    private Timer notificationTimer;

    // Panels
    private JPanel homePanel;

    public PatientUI(int patientId, String patientName) {
        this.patientId = patientId;
        this.patientName = patientName.isEmpty() ? "Bệnh nhân" : patientName;

        setTitle("DC Doctor – Patient");
        setSize(450, 800);
        setMinimumSize(new Dimension(360, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);

        setLayout(new BorderLayout());
        buildUI();
        setVisible(true);

        // Kích hoạt luồng quét thông báo
        startNotificationPolling();
    }

    public PatientUI() { this(0, ""); }

    // =========================================================
    // HÀM QUÉT THÔNG BÁO THEO THỜI GIAN THỰC
    // =========================================================
    private void startNotificationPolling() {
        notificationTimer = new Timer(5000, e -> {
            String selectSql = "SELECT id, message FROM notifications WHERE patient_id = ? AND is_read = 0 LIMIT 1";
            String updateSql = "UPDATE notifications SET is_read = 1 WHERE id = ?";

            try (Connection conn = DBConnection.connect()) {
                if (conn == null) return;

                int notifId = -1;
                String message = null;

                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setInt(1, this.patientId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            notifId = rs.getInt("id");
                            message = rs.getString("message");
                        }
                    }
                }

                if (notifId != -1 && message != null) {
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, notifId);
                        psUpdate.executeUpdate();
                    }

                    final String msgToDisplay = message;
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                msgToDisplay,
                                "🔔 Thông báo mới",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            } catch (SQLException ex) {
                // Bỏ qua log để console không bị rác
            }
        });
        notificationTimer.start();
    }

    // Tắt Timer khi đóng app để tránh lỗi rò rỉ bộ nhớ
    @Override
    public void dispose() {
        if (notificationTimer != null && notificationTimer.isRunning()) {
            notificationTimer.stop();
        }
        super.dispose();
    }

    // ══════════════════════════════════════════
    // BUILD TOÀN BỘ UI
    // ══════════════════════════════════════════
    private void buildUI() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG);

        // Tạo các màn hình
        homePanel = buildHomeScreen();
        contentArea.add(homePanel,                  "home");
        contentArea.add(wrapScreen(new ChatUI()),         "chat");
        contentArea.add(wrapScreen(new MediaBrowseUI()),  "media");
        contentArea.add((new BookingUI()),      "booking");
        contentArea.add(wrapScreen(new ProfileUI()),      "profile");

        contentArea.add(wrapScreen(new RecordUI(this.patientId)), "records");

        add(contentArea,     BorderLayout.CENTER);
        add(buildBottomNav(), BorderLayout.SOUTH);

        cardLayout.show(contentArea, "home");
    }

    // ── Wrap screen trong ScrollPane nếu cần ─────────────
    private JScrollPane wrapScreen(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    // ══════════════════════════════════════════
    // HOME SCREEN
    // ══════════════════════════════════════════
    private JPanel buildHomeScreen() {
        JPanel screen = new JPanel();
        screen.setBackground(BG);
        screen.setLayout(new BoxLayout(screen, BoxLayout.Y_AXIS));

        screen.add(buildHeroHeader());
        screen.add(buildQuickStats());
        screen.add(buildSectionLabel("Dịch vụ"));
        screen.add(buildServiceGrid());
        screen.add(buildSectionLabel("Hoạt động gần đây"));
        screen.add(buildRecentActivity());
        screen.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(screen);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Hero Header ───────────────────────────────────────
    private JPanel buildHeroHeader() {
        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient nền
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x3B82F6),
                        getWidth(), getHeight(), new Color(0x6366F1)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(getWidth() - 80, -30, 120, 120);
                g2.fillOval(getWidth() - 40, 60, 80, 80);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(-20, 40, 100, 100);
                g2.dispose();
            }
        };
        hero.setLayout(new BorderLayout());
        hero.setPreferredSize(new Dimension(390, 150));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        hero.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left — greeting
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel greetLbl = new JLabel("Xin chào 👋");
        greetLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greetLbl.setForeground(new Color(255, 255, 255, 200));
        greetLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(patientName);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("Chăm sóc sức khỏe mỗi ngày");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(255, 255, 255, 170));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        left.add(greetLbl);
        left.add(nameLbl);
        left.add(subLbl);

        // Right — avatar
        JLabel avatar = new JLabel(String.valueOf(patientName.charAt(0)).toUpperCase(),
                SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        avatar.setForeground(PRIMARY);
        avatar.setPreferredSize(new Dimension(52, 52));
        avatar.setOpaque(true);
        avatar.setBackground(Color.WHITE);
        avatar.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 100), 2));

        hero.add(left,   BorderLayout.CENTER);
        hero.add(avatar, BorderLayout.EAST);
        return hero;
    }

    // ── Quick Stats ───────────────────────────────────────
    private JPanel buildQuickStats() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(14, 16, 6, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        panel.add(makeStatMini("3",  "Lịch hẹn",   PRIMARY));
        panel.add(makeStatMini("12", "Tin nhắn",    SUCCESS));
        panel.add(makeStatMini("5",  "Thông báo",   WARN));

        return panel;
    }

    private JPanel makeStatMini(String value, String label, Color color) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(color);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLbl.setForeground(MUTED);
        lblLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(valLbl);
        card.add(lblLbl);
        return card;
    }

    // ── Section Label ─────────────────────────────────────
    private JPanel buildSectionLabel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 16, 8, 16));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    // ── Service Grid ──────────────────────────────────────
    private JPanel buildServiceGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 10));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(0, 16, 0, 16));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        grid.add(makeServiceCard("💬", "Tin nhắn",    new Color(0xEFF6FF), PRIMARY,
                () -> switchTo("chat")));
        grid.add(makeServiceCard("📅", "Đặt lịch",   new Color(0xF0FDF4), SUCCESS,
                () -> switchTo("booking")));
        grid.add(makeServiceCard("📰", "Bài viết",   new Color(0xFFF7ED), WARN,
                () -> switchTo("media")));
        grid.add(makeServiceCard("🩺", "Bệnh án",      new Color(0xFDF4FF), PURPLE,
                () -> switchTo("records")));
        grid.add(makeServiceCard("🔔", "Thông báo",  new Color(0xF0FDFA), TEAL,
                () -> new PatientNotificationUI("").setVisible(true)));
        grid.add(makeServiceCard("📋", "Lịch sử",    new Color(0xFFF1F2), DANGER,
                () -> new BookingHistoryUI().setVisible(true)));

        return grid;
    }

    private JPanel makeServiceCard(String emoji, String label, Color bg, Color color, Runnable action) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 12, 14, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(emoji, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLbl.setOpaque(true);
        iconLbl.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        iconLbl.setPreferredSize(new Dimension(44, 44));
        iconLbl.setMaximumSize(new Dimension(44, 44));
        iconLbl.setBorder(BorderFactory.createLineBorder(
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 40), 1));

        JLabel lblLbl = new JLabel(label, SwingConstants.CENTER);
        lblLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLbl.setForeground(color.darker());
        lblLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLbl.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(iconLbl);
        card.add(lblLbl);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(new EmptyBorder(12, 10, 12, 10));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(14, 12, 14, 12));
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }

    // ── Recent Activity ───────────────────────────────────
    private JPanel buildRecentActivity() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 16, 0, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        String[][] acts = {
                {"💬", "Bác sĩ Nguyễn đã trả lời câu hỏi của bạn",  "10 phút trước",  "blue"},
                {"📅", "Lịch hẹn khám ngày mai lúc 9:00 AM",          "1 giờ trước",    "green"},
                {"🔔", "Nhắc nhở uống thuốc: Metformin 500mg",         "2 giờ trước",    "warn"},
        };

        for (String[] act : acts) {
            panel.add(makeActivityItem(act[0], act[1], act[2], act[3]));
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    private JPanel makeActivityItem(String icon, String text, String time, String type) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        Color dotColor = type.equals("green") ? SUCCESS : type.equals("warn") ? WARN : PRIMARY;

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(new Color(dotColor.getRed(), dotColor.getGreen(), dotColor.getBlue(), 20));
        iconLbl.setBorder(new EmptyBorder(6, 8, 6, 8));
        iconLbl.setPreferredSize(new Dimension(38, 38));

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLbl.setForeground(TEXT);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED);
        timeLbl.setBorder(new EmptyBorder(2, 0, 0, 0));

        textCol.add(textLbl);
        textCol.add(timeLbl);

        item.add(iconLbl,  BorderLayout.WEST);
        item.add(textCol, BorderLayout.CENTER);
        return item;
    }

    // ══════════════════════════════════════════
    // BOTTOM NAVIGATION
    // ══════════════════════════════════════════
    private JPanel buildBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_C);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(0, 68));

        JButton btnHome    = makeNavBtn("🏠", "Trang chủ", true);
        JButton btnChat    = makeNavBtn("💬", "Chat",       false);
        JButton btnBooking = makeNavBtn("📅", "Lịch hẹn",  false);
        JButton btnProfile = makeNavBtn("👤", "Hồ sơ",     false);

        btnHome.addActionListener(e -> {
            switchTo("home");
            updateNav(btnHome, btnChat, btnBooking, btnProfile);
        });
        btnChat.addActionListener(e -> {
            switchTo("chat");
            updateNav(btnChat, btnHome, btnBooking, btnProfile);
        });
        btnBooking.addActionListener(e -> {
            switchTo("booking");
            updateNav(btnBooking, btnHome, btnChat, btnProfile);
        });
        btnProfile.addActionListener(e -> {
            switchTo("profile");
            updateNav(btnProfile, btnHome, btnChat, btnBooking);
        });

        activeNavBtn = btnHome;

        nav.add(btnHome);
        nav.add(btnChat);
        nav.add(btnBooking);
        nav.add(btnProfile);
        return nav;
    }

    private JButton makeNavBtn(String icon, String label, boolean active) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 15));
                    g2.fillRoundRect(10, 4, getWidth()-20, getHeight()-8, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setSelected(active);

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLbl = new JLabel(label, SwingConstants.CENTER);
        lblLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLbl.setForeground(active ? PRIMARY : MUTED);
        lblLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.add(Box.createVerticalGlue());
        btn.add(iconLbl);
        btn.add(lblLbl);
        btn.add(Box.createVerticalGlue());

        btn.setBackground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addChangeListener(e -> lblLbl.setForeground(btn.isSelected() ? PRIMARY : MUTED));

        return btn;
    }

    private void updateNav(JButton active, JButton... others) {
        active.setSelected(true);
        for (JButton b : others) b.setSelected(false);
        activeNavBtn = active;
        active.repaint();
        for (JButton b : others) b.repaint();
    }

    private void switchTo(String screen) {
        cardLayout.show(contentArea, screen);
    }
}