package com.dcdoctor.UI.doctor;

import com.dcdoctor.database.AppointmentDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorAppointmentUI extends JFrame {

    private static final Color BG       = new Color(0xF8FAFF);
    private static final Color WHITE    = Color.WHITE;
    private static final Color PRIMARY  = new Color(0x3B82F6);
    private static final Color SUCCESS  = new Color(0x10B981); // Xanh lá - Duyệt
    private static final Color DANGER   = new Color(0xEF4444); // Đỏ - Từ chối
    private static final Color TEXT     = new Color(0x1E293B);
    private static final Color MUTED    = new Color(0x64748B);
    private static final Color BORDER_C = new Color(0xE2E8F0);

    private JPanel listPanel;

    // Đã bỏ gán cứng = 2, để biến này nhận giá trị từ constructor
    private int currentDoctorId;

    // Constructor giờ yêu cầu truyền doctorId vào
    public DoctorAppointmentUI(int doctorId) {
        this.currentDoctorId = doctorId;

        setTitle("Quản lý lịch hẹn - Bác sĩ");
        setSize(420, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);

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

        JLabel title = new JLabel("Lịch hẹn chờ duyệt");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 12, 0, 0));

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
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void loadData() {
        listPanel.removeAll();
        AppointmentDAO dao = new AppointmentDAO();

        // Gọi hàm lấy lịch Pending theo đúng ID của bác sĩ đang đăng nhập
        List<String[]> pendingList = dao.getPendingForDoctor(currentDoctorId);

        if (pendingList.isEmpty()) {
            JLabel emptyLbl = new JLabel("Không có lịch hẹn nào đang chờ xử lý.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(50));
            listPanel.add(emptyLbl);
        } else {
            for (String[] row : pendingList) {
                int appointmentId = Integer.parseInt(row[0]);
                String date = row[1];
                String time = row[2];
                String patientName = row[3];
                String notes = row[4];

                listPanel.add(makeActionCard(appointmentId, patientName, date, time, notes));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel makeActionCard(int appointmentId, String patient, String date, String time, String notes) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(14, 18, 14, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel("BN: " + patient);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(TEXT);

        JLabel datetimeLbl = new JLabel("🕒 " + time + " - 📅 " + date);
        datetimeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        datetimeLbl.setForeground(PRIMARY);
        datetimeLbl.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel noteLbl = new JLabel("Ghi chú: " + (notes == null || notes.isEmpty() ? "Không" : notes));
        noteLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        noteLbl.setForeground(MUTED);

        info.add(nameLbl);
        info.add(datetimeLbl);
        info.add(noteLbl);

        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 6));
        actions.setOpaque(false);

        JButton btnConfirm = createActionButton("✓ Duyệt", SUCCESS);
        btnConfirm.addActionListener(e -> processAppointment(appointmentId, "Confirmed", "Đã duyệt lịch khám!"));

        JButton btnCancel = createActionButton("✕ Từ chối", DANGER);
        btnCancel.addActionListener(e -> processAppointment(appointmentId, "Cancelled", "Đã từ chối lịch khám!"));

        actions.add(btnConfirm);
        actions.add(btnCancel);

        card.add(info, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(85, 30));
        return btn;
    }

    private void processAppointment(int appointmentId, String newStatus, String successMsg) {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thực hiện thao tác này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AppointmentDAO dao = new AppointmentDAO();
            boolean success = dao.updateAppointmentStatus(appointmentId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, successMsg);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}