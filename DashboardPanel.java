package com.dcdoctor.UI.doctor;

import com.dcdoctor.database.ActivityLogDAO;
import com.dcdoctor.database.AppointmentDAO;
import com.dcdoctor.database.ChatDAO;
import com.dcdoctor.model.Chat;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.dcdoctor.UI.doctor.DoctorUI.*;

public class DashboardPanel extends JPanel {

    // Thêm biến lưu ID bác sĩ đang đăng nhập
    private int currentDoctorId;

    // Yêu cầu truyền ID khi khởi tạo Dashboard
    public DashboardPanel(int doctorId) {
        this.currentDoctorId = doctorId;

        setBackground(BG);
        setLayout(new BorderLayout());

        // 1. TOP BAR (Chuẩn Mobile)
        add(buildHeader(), BorderLayout.NORTH);

        // 2. CENTER CONTENT (Cuộn dọc)
        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // ── HEADER ────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel title = new JLabel("Bảng điều khiển");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        JLabel bellIcon = new JLabel("🔔");
        bellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        bellIcon.setForeground(TEXT);
        bellIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(title, BorderLayout.WEST);
        header.add(bellIcon, BorderLayout.EAST);
        return header;
    }

    // ── NỘI DUNG CHÍNH (Xếp dọc) ──────────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 16, 24, 16));

        // Heading
        JLabel heading = new JLabel("Xin chào, Bác sĩ 👋");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Tổng quan hoạt động hôm nay");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 16, 0));

        p.add(heading);
        p.add(sub);

        // ── Thống kê (Lưới 2x2 cho Mobile) LẤY TỪ DB ────────────────────────
        JPanel statGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        statGrid.setBackground(BG);
        statGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        statGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        ChatDAO chatDAO = new ChatDAO();
        int[] stats = chatDAO.getDashboardStats();
        int total    = stats[0];
        int pending  = stats[1];
        int answered = stats[2];

        statGrid.add(makeStatCard("💬", String.valueOf(total),    "Tổng",      ACCENT));
        statGrid.add(makeStatCard("⏳", String.valueOf(pending),  "Chờ xử lý", WARN));
        statGrid.add(makeStatCard("✅", String.valueOf(answered), "Đã xong",   SUCCESS));
        statGrid.add(makeStatCard("⭐", "4.8",                    "Đánh giá",  new Color(0xF472B6)));

        p.add(statGrid);
        p.add(Box.createVerticalStrut(24));

        // ── 1. Danh sách câu hỏi ─────────────────────────
        JPanel questionPanel = buildQuestionPanel(chatDAO);
        questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(questionPanel);

        p.add(Box.createVerticalStrut(24));

        // ── 2. ĐÃ GỌI DANH SÁCH LỊCH HẸN VÀO ĐÂY ─────────────────────────
        JPanel appointmentPanel = buildAppointmentPanel();
        appointmentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(appointmentPanel);

        p.add(Box.createVerticalStrut(24));

        // ── 3. Hoạt động gần đây ─────────────────────────
        JPanel activityPanel = buildActivityPanel();
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(activityPanel);

        return p;
    }

    private JPanel makeStatCard(String emoji, String value, String label, Color accentColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.fillRect(0, 2, getWidth(), 2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconLbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(TEXT);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLbl.setForeground(MUTED);
        lblLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(valLbl);
        card.add(lblLbl);
        return card;
    }

    private JPanel buildQuestionPanel(ChatDAO chatDAO) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new LineBorder(BORDER, 1, true));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel title = new JLabel("Câu hỏi chờ xử lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel viewAll = new JLabel("Xem tất cả");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewAll.setForeground(ACCENT);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setBackground(CARD);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Chat> pendingList = chatDAO.getPendingQuestions();

        if (pendingList == null || pendingList.isEmpty()) {
            JLabel emptyLbl = new JLabel("✅ Không có ca nào cần xử lý", SwingConstants.CENTER);
            emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setBorder(new EmptyBorder(20, 0, 20, 0));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            list.add(emptyLbl);
        } else {
            int limit = Math.min(3, pendingList.size());
            for (int i = 0; i < limit; i++) {
                Chat q = pendingList.get(i);
                list.add(makeQItem("BN #" + q.getPatientId(), q.getMessage(), "Gần đây", "pending"));
            }
        }

        panel.add(header, BorderLayout.NORTH);
        panel.add(list,   BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeQItem(String name, String question, String time, String status) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(CARD);
        item.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x1A2540)),
                new EmptyBorder(12, 14, 12, 14)
        ));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatar = new JLabel(String.valueOf(name.charAt(0)).toUpperCase(), SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0x1E3A5F));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(0x2D5A9E), 1));

        JPanel textCol = new JPanel();
        textCol.setBackground(CARD);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new BorderLayout());
        nameRow.setBackground(CARD);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(TEXT);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED);

        nameRow.add(nameLbl, BorderLayout.WEST);
        nameRow.add(timeLbl, BorderLayout.EAST);

        String preview = question.length() > 30 ? question.substring(0, 30) + "..." : question;
        JLabel previewLbl = new JLabel(preview);
        previewLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        previewLbl.setForeground(SUB);
        previewLbl.setBorder(new EmptyBorder(2, 0, 4, 0));

        JLabel badgeLbl = makeBadge(status.equals("answered") ? "✓ Đã xong" : "⏳ Chờ xử lý",
                status.equals("answered") ? SUCCESS : WARN);

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setBackground(CARD);
        badgeRow.add(badgeLbl);

        textCol.add(nameRow);
        textCol.add(previewLbl);
        textCol.add(badgeRow);

        item.add(avatar,  BorderLayout.WEST);
        item.add(textCol, BorderLayout.CENTER);
        return item;
    }

    private JLabel makeBadge(String text, Color color) {
        JLabel badge = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(color);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(2, 6, 2, 6));
        return badge;
    }

    // ── DANH SÁCH LỊCH HẸN CHỜ DUYỆT ─────────────────────────
    private JPanel buildAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new LineBorder(BORDER, 1, true));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel title = new JLabel("Lịch hẹn chờ duyệt");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel viewAll = new JLabel("Xem tất cả ➔");
        viewAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewAll.setForeground(SUCCESS);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TRUYỀN ID SANG FORM CON
                new DoctorAppointmentUI(currentDoctorId);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setBackground(CARD);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        AppointmentDAO dao = new AppointmentDAO();
        // SỬ DỤNG ID THỰC TẾ CỦA BÁC SĨ TỪ CONSTRUCTOR
        List<String[]> pendingList = dao.getPendingForDoctor(this.currentDoctorId);

        if (pendingList == null || pendingList.isEmpty()) {
            JLabel emptyLbl = new JLabel("✅ Không có lịch hẹn nào chờ duyệt", SwingConstants.CENTER);
            emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setBorder(new EmptyBorder(20, 0, 20, 0));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            list.add(emptyLbl);
        } else {
            int limit = Math.min(3, pendingList.size());
            for (int i = 0; i < limit; i++) {
                String[] row = pendingList.get(i);
                list.add(makeQItem("BN: " + row[3], "Lịch khám: " + row[1], row[2], "pending"));
            }
        }

        panel.add(header, BorderLayout.NORTH);
        panel.add(list,   BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new LineBorder(BORDER, 1, true));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel title = new JLabel("Hoạt động gần đây");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        JPanel list = new JPanel();
        list.setBackground(CARD);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<String[]> activities = ActivityLogDAO.getRecentActivities();

        if (activities == null || activities.isEmpty()) {
            JLabel emptyLbl = new JLabel("  Chưa có hoạt động nào");
            emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setBorder(new EmptyBorder(15, 10, 15, 10));
            list.add(emptyLbl);
        } else {
            for (String[] act : activities) {
                list.add(makeActivityItem(act[0], act[1], act[2]));
            }
        }

        panel.add(header, BorderLayout.NORTH);
        panel.add(list,   BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeActivityItem(String dotType, String text, String time) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(CARD);
        item.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, new Color(0x1A2540)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        Color dotColor = dotType.equals("green") ? SUCCESS : dotType.equals("warn") ? WARN : ACCENT;
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 5, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 18));
        dot.setOpaque(false);

        JLabel textLbl = new JLabel("<html>" + text + "</html>");
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLbl.setForeground(SUB);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED);

        item.add(dot,     BorderLayout.WEST);
        item.add(textLbl, BorderLayout.CENTER);
        item.add(timeLbl, BorderLayout.EAST);
        return item;
    }
}