package com.dcdoctor.UI.patient;

import com.dcdoctor.database.AppointmentDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookingHistoryUI extends JFrame {

    private static final Color BG       = new Color(0xF8FAFF);
    private static final Color WHITE    = Color.WHITE;
    private static final Color PRIMARY  = new Color(0x3B82F6);
    private static final Color SUCCESS  = new Color(0x10B981); // Xanh lá
    private static final Color WARN     = new Color(0xF59E0B); // Vàng/Cam
    private static final Color DANGER   = new Color(0xEF4444); // Đỏ
    private static final Color TEXT     = new Color(0x1E293B);
    private static final Color MUTED    = new Color(0x64748B);
    private static final Color BORDER_C = new Color(0xE2E8F0);

    private JPanel listPanel;

    public BookingHistoryUI() {
        setTitle("Lịch sử đặt lịch");
        setSize(390, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildList(),   BorderLayout.CENTER);

        loadData();
        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(WHITE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel back = new JLabel("←");
        back.setFont(new Font("Segoe UI", Font.BOLD, 20));
        back.setForeground(PRIMARY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
        });

        JLabel title = new JLabel("Lịch sử đặt lịch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 12, 0, 0));

        // Thêm nút Refresh cho tiện
        JLabel refresh = new JLabel("🔄");
        refresh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { loadData(); }
        });

        bar.add(back, BorderLayout.WEST);
        bar.add(title, BorderLayout.CENTER);
        bar.add(refresh, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildList() {
        listPanel = new JPanel();
        listPanel.setBackground(BG);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private void loadData() {
        listPanel.removeAll();
        AppointmentDAO dao = new AppointmentDAO();

        int currentPatientId = 1; // TODO: Cần lấy từ User Session sau khi login

        List<String[]> history = dao.getHistoryByPatient(currentPatientId);

        if (history.isEmpty()) {
            JLabel emptyLbl = new JLabel("Bạn chưa có lịch hẹn nào.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLbl);
        } else {
            for (String[] row : history) {
                // row[0]: date, row[1]: time, row[2]: doctorName, row[3]: notes, row[4]: status
                String displayDate = row[0] + " lúc " + row[1];
                String doctorName = "BS. " + row[2];
                String status = row[4];

                listPanel.add(makeBookingCard(doctorName, displayDate, status));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel makeBookingCard(String doctor, String date, String status) {
        Color statusColor;
        String statusText;
        String statusIcon;

        // Xử lý UI theo từng trạng thái (State Machine)
        if ("Confirmed".equalsIgnoreCase(status)) {
            statusColor = SUCCESS;
            statusText = "Đã xác nhận";
            statusIcon = "✓";
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            statusColor = DANGER;
            statusText = "Đã hủy";
            statusIcon = "✕";
        } else {
            statusColor = WARN;
            statusText = "Chờ duyệt";
            statusIcon = "⏳";
        }

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(statusColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel docLbl = new JLabel(doctor);
        docLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        docLbl.setForeground(TEXT);

        JLabel dateLbl = new JLabel("📅 " + date);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(MUTED);
        dateLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        info.add(docLbl);
        info.add(dateLbl);

        // Status badge
        JLabel badge = new JLabel(statusIcon + " " + statusText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(statusColor);
        badge.setOpaque(true);
        badge.setBackground(new Color(
                statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 20));
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        card.add(info,  BorderLayout.CENTER);
        card.add(badge, BorderLayout.EAST);
        return card;
    }
}