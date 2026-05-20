package com.dcdoctor.UI.doctor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.dcdoctor.UI.doctor.DoctorUI.*;

public class DoctorNotificationUI extends JPanel {

    private JPanel notifList;
    private int unreadCount = 2;
    private JLabel countLbl;

    static class NotifData {
        String icon, type, title, desc, time;
        boolean unread;
        NotifData(String icon, String type, String title, String desc, String time, boolean unread) {
            this.icon = icon; this.type = type; this.title = title;
            this.desc = desc; this.time = time; this.unread = unread;
        }
    }

    private final NotifData[] DEMO = {
            new NotifData("💬","blue","Câu hỏi mới",
                    "Mai: \"Chỉ số bao nhiêu là ổn?\"","10:30 AM",true),
            new NotifData("💬","blue","Câu hỏi mới",
                    "Hùng hỏi về chế độ ăn uống","09:45 AM",true),
            new NotifData("✅","green","Đã trả lời",
                    "Câu trả lời cho Lan đã gửi đi","09:20 AM",false),
            new NotifData("🤖","blue","Gợi ý AI",
                    "AI đã tạo xong gợi ý cho câu hỏi của An","08:55 AM",false),
            new NotifData("⚠️","warn","Nhắc nhở",
                    "Bạn còn 3 câu hỏi chưa xử lý","08:00 AM",false),
    };

    public DoctorNotificationUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        // 1. TOP BAR
        add(buildHeader(), BorderLayout.NORTH);

        // 2. CENTER CONTENT (Cuộn dọc)
        add(buildList(), BorderLayout.CENTER);
    }

    // ── HEADER (Chuẩn Mobile) ─────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Nút Back
        JLabel backBtn = new JLabel("‹ ", SwingConstants.CENTER);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        backBtn.setForeground(TEXT);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setBackground(CARD);

        JLabel title = new JLabel("Thông báo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        countLbl = new JLabel(String.valueOf(unreadCount));
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        countLbl.setForeground(Color.WHITE);
        countLbl.setOpaque(true);
        countLbl.setBackground(DANGER);
        countLbl.setBorder(new EmptyBorder(2, 6, 2, 6));

        titlePanel.add(title);
        titlePanel.add(countLbl);

        // Nút tính năng (Gom gọn lại thành Icon cho Mobile)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setBackground(CARD);

        JLabel refreshIcon = new JLabel("🔄");
        refreshIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        refreshIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { refreshNotifications(); }
        });

        JLabel markReadIcon = new JLabel("✓");
        markReadIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        markReadIcon.setForeground(SUB);
        markReadIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markReadIcon.setToolTipText("Đánh dấu tất cả đã đọc");
        markReadIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { markAllRead(); }
        });

        actionPanel.add(refreshIcon);
        actionPanel.add(markReadIcon);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    // ── NỘI DUNG CHÍNH (Danh sách Thông báo) ──────────────────────────
    private JScrollPane buildList() {
        notifList = new JPanel();
        notifList.setBackground(BG);
        notifList.setLayout(new BoxLayout(notifList, BoxLayout.Y_AXIS));
        notifList.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Kéo data thật từ SQLite lên
        java.util.List<String[]> realNotifs = com.dcdoctor.database.NotificationDAO.getRecentNotifications();

        if (realNotifs.isEmpty()) {
            JLabel empty = new JLabel("Không có thông báo nào");
            empty.setForeground(MUTED);
            notifList.add(empty);
        } else {
            for (String[] row : realNotifs) {
                // Do DB của bạn thiếu cột icon/type nên tôi tạm hardcode giao diện cho nó đồng bộ
                NotifData data = new NotifData(
                        "💬", "blue", "Hệ thống", row[0], row[1], false
                );
                notifList.add(makeNotifItem(data));
            }
        }

        JScrollPane scroll = new JScrollPane(notifList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel makeNotifItem(NotifData nd) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setBackground(nd.unread ? new Color(0x0F1D35) : CARD);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nền đổi màu khi Hover
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(0x1A2540));
            }
            @Override public void mouseExited(MouseEvent e) {
                item.setBackground(nd.unread ? new Color(0x0F1D35) : CARD);
            }
            @Override public void mouseClicked(MouseEvent e) {
                // Xử lý khi click vào thông báo (Ví dụ: Chuyển đến màn hình Chat)
            }
        });

        // Icon
        Color iconBg = nd.type.equals("green") ? new Color(0x10b981, false) : nd.type.equals("warn")
                ? new Color(0xf59e0b, false) : new Color(0x3b82f6, false);

        JLabel iconLbl = new JLabel(nd.icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLbl.setPreferredSize(new Dimension(36, 36));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(new Color(iconBg.getRed(), iconBg.getGreen(), iconBg.getBlue(), 40));

        // Bo tròn nền icon (Dùng Panel ghi đè paintComponent)
        JPanel iconWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                g2.dispose();
            }
        };
        iconWrapper.setOpaque(false);
        iconWrapper.setBackground(new Color(iconBg.getRed(), iconBg.getGreen(), iconBg.getBlue(), 40));
        iconWrapper.setPreferredSize(new Dimension(40, 40));
        iconWrapper.add(iconLbl, BorderLayout.CENTER);

        // Phần chữ
        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel titleLbl = new JLabel(nd.title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT);

        JLabel timeLbl = new JLabel(nd.time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED);

        titleRow.add(titleLbl, BorderLayout.WEST);
        titleRow.add(timeLbl, BorderLayout.EAST);

        // Cắt gọn mô tả nếu quá dài
        String desc = nd.desc.length() > 40 ? nd.desc.substring(0, 40) + "..." : nd.desc;
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(SUB);
        descLbl.setBorder(new EmptyBorder(2, 0, 0, 0));

        textCol.add(titleRow);
        textCol.add(descLbl);

        // Chấm tròn báo chưa đọc
        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)); // Padding top để cân bằng
        right.setOpaque(false);
        if (nd.unread) {
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ACCENT);
                    g2.fillOval(0, 0, 8, 8);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(8, 8));
            right.add(dot);
        }

        item.add(iconWrapper, BorderLayout.WEST);
        item.add(textCol,     BorderLayout.CENTER);
        item.add(right,       BorderLayout.EAST);

        return item;
    }

    // ── XỬ LÝ SỰ KIỆN ─────────────────────────────────────────────────
    private void markAllRead() {
        unreadCount = 0;
        countLbl.setText("0");
        countLbl.setBackground(MUTED);

        // Reset lại background của các item (Chỉ là UI demo)
        for (Component c : notifList.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(CARD);
            }
        }
        notifList.revalidate();
        notifList.repaint();
    }

    private void refreshNotifications() {
        // 1. Xóa sạch các item đang hiện trên giao diện
        notifList.removeAll();

        // 2. Tải lại dữ liệu thật từ Database (thông qua DAO)
        java.util.List<String[]> realNotifs = com.dcdoctor.database.NotificationDAO.getRecentNotifications();

        if (realNotifs.isEmpty()) {
            JLabel empty = new JLabel("Không có thông báo nào");
            empty.setForeground(MUTED);
            notifList.add(empty);
            unreadCount = 0;
        } else {
            int count = 0;
            for (String[] row : realNotifs) {
                // row[2] là cột is_read (0: chưa đọc, 1: đã đọc)
                boolean isUnread = "0".equals(row[2]);
                if (isUnread) count++;

                NotifData data = new NotifData(
                        isUnread ? "💬" : "✅",
                        "blue", "Thông báo", row[0], row[1], isUnread
                );
                notifList.add(makeNotifItem(data));
            }
            unreadCount = count; // Cập nhật lại số lượng tin chưa đọc thật
        }

        // 3. Cập nhật lại Badge (con số thông báo)
        countLbl.setText(String.valueOf(unreadCount));
        countLbl.setBackground(unreadCount > 0 ? DANGER : MUTED);

        // 4. Vẽ lại giao diện
        notifList.revalidate();
        notifList.repaint();
    }

    // ── BOTTOM NAV ────────────────────────────────────────────────────


    private JPanel makeNavItem(String icon, String text, Color color) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(CARD);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLbl.setForeground(color);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLbl.setBorder(new EmptyBorder(6, 0, 2, 0));

        JLabel textLbl = new JLabel(text, SwingConstants.CENTER);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        textLbl.setForeground(color);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        item.add(iconLbl);
        item.add(textLbl);

        // Sự kiện click chuyển tab (Cần nối vào Frame chính)
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                // ... Xử lý chuyển tab
            }
        });

        return item;
    }
}