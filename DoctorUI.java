package com.dcdoctor.UI.doctor;

import com.dcdoctor.UI.LoginUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DoctorUI extends JFrame {

    // ── Màu sắc chuẩn ─────────────────────────────────────────
    public static final Color BG       = new Color(0x0B0F1A);
    public static final Color SURFACE  = new Color(0x111827);
    public static final Color CARD     = new Color(0x161D2E);
    public static final Color BORDER   = new Color(0x1E293B);
    public static final Color ACCENT   = new Color(0x3B82F6);
    public static final Color SUCCESS  = new Color(0x10B981);
    public static final Color WARN     = new Color(0xF59E0B);
    public static final Color DANGER   = new Color(0xEF4444);
    public static final Color TEXT     = new Color(0xF1F5F9);
    public static final Color MUTED    = new Color(0x64748B);
    public static final Color SUB      = new Color(0x94A3B8);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel topbarTitle;

    // Biến lưu ID của bác sĩ đang đăng nhập
    private int loggedInDoctorId;

    // Quản lý trạng thái Bottom Nav
    private List<NavButton> navButtons = new ArrayList<>();

    // ÉP BUỘC PHẢI TRUYỀN ID KHI KHỞI TẠO DOCTOR UI
    public DoctorUI(int doctorId) {
        this.loggedInDoctorId = doctorId;

        setTitle("DC Doctor");
        setSize(450, 800); // Ép tỷ lệ màn hình điện thoại
        setResizable(false); // Khóa thu phóng
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(BG);
        getContentPane().setBackground(BG);

        setLayout(new BorderLayout());

        // 1. TOP BAR (Tiêu đề trên cùng)
        add(buildTopBar(), BorderLayout.NORTH);

        // 2. CENTER (Nội dung chính cuộn dọc - CardLayout)
        add(buildMainContent(), BorderLayout.CENTER);

        // 3. BOTTOM NAV (Thanh điều hướng dưới cùng)
        add(buildBottomNav(), BorderLayout.SOUTH);

        // Mở màn hình Dashboard mặc định
        activateTab("dashboard", "Tổng quan");
    }

    // ══════════════════════════════════════════
    // TOP BAR
    // ══════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(SURFACE);
        topbar.setPreferredSize(new Dimension(0, 60));
        topbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Nút Menu (Giả lập)
        JLabel menuIcon = new JLabel(" ≡ ");
        menuIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuIcon.setForeground(TEXT);
        menuIcon.setBorder(new EmptyBorder(0, 16, 0, 0));

        topbarTitle = new JLabel("Tổng quan", SwingConstants.CENTER);
        topbarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topbarTitle.setForeground(TEXT);

        // Avatar nhỏ (Góc phải)
        JLabel avatarIcon = new JLabel("⚕");
        avatarIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatarIcon.setForeground(ACCENT);
        avatarIcon.setBorder(new EmptyBorder(0, 0, 0, 16));

        topbar.add(menuIcon, BorderLayout.WEST);
        topbar.add(topbarTitle, BorderLayout.CENTER);
        topbar.add(avatarIcon, BorderLayout.EAST);

        return topbar;
    }

    // ══════════════════════════════════════════
    // MAIN CONTENT (Card Layout)
    // ══════════════════════════════════════════
    private JPanel buildMainContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        // TRUYỀN ID XUỐNG DASHBOARD PANEL Ở ĐÂY
        contentPanel.add(new DashboardPanel(this.loggedInDoctorId), "dashboard");

        contentPanel.add(new QuestionUI(),           "questions");
        contentPanel.add(new DoctorNotificationUI(), "notifications");
        contentPanel.add(buildProfilePanel(),        "profile");

        // Vẫn giữ tab AnswerUI nếu code cũ của bạn có gọi đến nó ở đâu đó,
        // nhưng theo luồng mới thì nó đã được nhúng vào trong ChatUI rồi.
        contentPanel.add(new AnswerUI(), "answer");

        return contentPanel;
    }

    // ══════════════════════════════════════════
    // BOTTOM NAVIGATION
    // ══════════════════════════════════════════
    private JPanel buildBottomNav() {
        JPanel bottomNav = new JPanel(new GridLayout(1, 4));
        bottomNav.setBackground(SURFACE);
        bottomNav.setPreferredSize(new Dimension(0, 65));
        bottomNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        // Khởi tạo các nút
        navButtons.add(new NavButton("🏠", "Trang chủ", "dashboard", "Tổng quan"));
        navButtons.add(new NavButton("💬", "Câu hỏi",   "questions", "Ca cần tư vấn"));
        navButtons.add(new NavButton("🔔", "Thông báo", "notifications", "Thông báo"));
        navButtons.add(new NavButton("👤", "Hồ sơ",     "profile", "Hồ sơ Bác sĩ"));

        for (NavButton btn : navButtons) {
            bottomNav.add(btn);
        }

        return bottomNav;
    }

    // Class đại diện cho 1 nút trong Bottom Nav
    private class NavButton extends JPanel {
        private JLabel iconLbl;
        private JLabel textLbl;
        private String cardName;
        private String titleName;

        public NavButton(String icon, String text, String cardName, String titleName) {
            this.cardName = cardName;
            this.titleName = titleName;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(SURFACE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            iconLbl = new JLabel(icon, SwingConstants.CENTER);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconLbl.setBorder(new EmptyBorder(8, 0, 2, 0));

            textLbl = new JLabel(text, SwingConstants.CENTER);
            textLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(iconLbl);
            add(textLbl);

            // Sự kiện click chuyển tab
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    activateTab(cardName, titleName);
                }
            });
        }

        public void setActive(boolean isActive) {
            iconLbl.setForeground(isActive ? ACCENT : MUTED);
            textLbl.setForeground(isActive ? ACCENT : MUTED);
        }

        public String getCardName() { return cardName; }
    }

    // Hàm đổi tab và đổi màu nút
    private void activateTab(String targetCard, String targetTitle) {
        cardLayout.show(contentPanel, targetCard);
        topbarTitle.setText(targetTitle);

        for (NavButton btn : navButtons) {
            btn.setActive(btn.getCardName().equals(targetCard));
        }
    }

    // ══════════════════════════════════════════
    // MÀN HÌNH HỒ SƠ (Chứa nút Đăng xuất)
    // ══════════════════════════════════════════
    private JPanel buildProfilePanel() {
        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setBackground(BG);
        profile.setBorder(new EmptyBorder(40, 20, 20, 20));

        // Avatar
        JLabel avatar = new JLabel("👨‍⚕️", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- LẤY DỮ LIỆU THẬT ---
        com.dcdoctor.database.DoctorDAO dao = new com.dcdoctor.database.DoctorDAO();
        com.dcdoctor.model.Doctor currentDoctor = dao.getDoctorById(this.loggedInDoctorId);

        // Kiểm tra kỹ đối tượng trả về
        String displayName;
        String displaySpec;

        if (currentDoctor != null) {
            // Thử getName() nếu getFullName() bị trống (tùy vào model của bạn)
            String nameFromDB = currentDoctor.getName();
            if (nameFromDB == null || nameFromDB.isEmpty()) {
                nameFromDB = currentDoctor.getFullName();
            }

            displayName = "BS. " + nameFromDB;
            displaySpec = "Chuyên khoa: " + (currentDoctor.getSpecialty() != null ? currentDoctor.getSpecialty() : "Đa khoa");
        } else {
            // Nếu vào đây nghĩa là DAO trả về null (không tìm thấy ID trong bảng 'user')
            displayName = "Không tìm thấy tên (ID: " + loggedInDoctorId + ")";
            displaySpec = "Vui lòng kiểm tra bảng 'user'";
        }

        JLabel name = new JLabel(displayName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(TEXT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel spec = new JLabel(displaySpec);
        spec.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spec.setForeground(SUB);
        spec.setAlignmentX(Component.CENTER_ALIGNMENT);
        spec.setBorder(new EmptyBorder(0, 0, 40, 0));
        // Nút Đăng xuất
        JButton btnLogout = new JButton("🚪 Đăng xuất");
        btnLogout.setBackground(new Color(0xEF4444, false)); // Đỏ mờ
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(200, 45));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });

        profile.add(avatar);
        profile.add(name);
        profile.add(spec);
        profile.add(btnLogout);

        return profile;
    }
}