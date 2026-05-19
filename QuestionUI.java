package com.dcdoctor.UI.doctor;

import com.dcdoctor.model.Chat;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.dcdoctor.UI.doctor.DoctorUI.*;

public class QuestionUI extends JPanel {

    private JPanel listContainer;
    private JLabel countBadge;
    private JPanel mainView; // Chứa danh sách (để dễ dàng ẩn/hiện khi mở Chat)
    private List<Chat> pendingList = new ArrayList<>();

    public QuestionUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        // View chính chứa Danh sách câu hỏi
        mainView = new JPanel(new BorderLayout());
        mainView.setBackground(BG);

        mainView.add(buildHeader(), BorderLayout.NORTH);
        mainView.add(buildListArea(), BorderLayout.CENTER);

        add(mainView, BorderLayout.CENTER);

        loadQuestions();
    }

    // ══════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Tiêu đề & Badge
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(CARD);

        JLabel title = new JLabel("Ca cần tư vấn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        countBadge = new JLabel("0");
        countBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countBadge.setForeground(Color.WHITE);
        countBadge.setOpaque(true);
        countBadge.setBackground(DANGER);
        countBadge.setBorder(new EmptyBorder(2, 8, 2, 8));

        left.add(title);
        left.add(countBadge);

        // Nút Refresh
        JLabel refreshBtn = new JLabel("🔄");
        refreshBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                loadQuestions();
            }
        });

        header.add(left, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        return header;
    }

    // ══════════════════════════════════════════
    // LIST AREA (Khu vực cuộn danh sách thẻ)
    // ══════════════════════════════════════════
    private JScrollPane buildListArea() {
        listContainer = new JPanel();
        listContainer.setBackground(BG);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ══════════════════════════════════════════
    // RENDER MỘT THẺ CÂU HỎI (Card)
    // ══════════════════════════════════════════
    private JPanel makeQuestionCard(Chat chat) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(CARD);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hiệu ứng Hover
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0x1A2540));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(CARD);
            }
            @Override public void mouseClicked(MouseEvent e) {
                openChat(chat);
            }
        });

        // Avatar
        JLabel avatar = new JLabel("P" + chat.getPatientId(), SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0x1E3A5F));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(0x2D5A9E), 1));

        // Nội dung chữ
        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        // Hàng 1: Tên + Giờ
        JPanel nameRow = new JPanel(new BorderLayout());
        nameRow.setOpaque(false);

        JLabel nameLbl = new JLabel("Bệnh nhân #" + chat.getPatientId());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(TEXT);

        JLabel timeLbl = new JLabel("Hôm nay"); // Tạm hardcode time, thực tế lấy từ DB
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLbl.setForeground(MUTED);

        nameRow.add(nameLbl, BorderLayout.WEST);
        nameRow.add(timeLbl, BorderLayout.EAST);

        // Hàng 2: Đoạn trích câu hỏi
        String qStr = chat.getMessage();
        String preview = qStr.length() > 40 ? qStr.substring(0, 40) + "..." : qStr;
        JLabel previewLbl = new JLabel(preview);
        previewLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        previewLbl.setForeground(SUB);
        previewLbl.setBorder(new EmptyBorder(4, 0, 6, 0));

        // Hàng 3: Badge trạng thái
        JLabel badge = new JLabel("⏳ Cần xử lý");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(WARN);
        badge.setOpaque(true);
        badge.setBackground(new Color(245, 158, 11, 25));
        badge.setBorder(new EmptyBorder(2, 6, 2, 6));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(badge);

        textCol.add(nameRow);
        textCol.add(previewLbl);
        textCol.add(badgeRow);

        card.add(avatar, BorderLayout.WEST);
        card.add(textCol, BorderLayout.CENTER);

        // Wrapper để tạo khoảng cách giữa các card
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setBorder(new EmptyBorder(0, 0, 12, 0));
        wrapper.add(card);

        return wrapper;
    }

    // ══════════════════════════════════════════
    // MỞ MÀN HÌNH CHAT
    // ══════════════════════════════════════════
    private void openChat(Chat chat) {
        // Tạo màn hình ChatUI mới (Mã ChatUI tôi đã cung cấp ở bước trước)
        ChatUI chatUI = new ChatUI(this, chat);

        // Ẩn view danh sách đi
        mainView.setVisible(false);

        // Đắp giao diện chat lên trên cùng
        add(chatUI, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // GHI ĐÈ hàm remove để bắt sự kiện khi ChatUI bị đóng (bấm nút Back)
    @Override
    public void remove(Component comp) {
        super.remove(comp);
        // Nếu component bị gỡ ra là màn hình ChatUI -> Hiện lại danh sách
        if (comp instanceof ChatUI) {
            mainView.setVisible(true);
            loadQuestions(); // Load lại data lỡ bác sĩ vừa trả lời xong
        }
    }

    // ══════════════════════════════════════════
    // LOAD DATA
    // ══════════════════════════════════════════
    // ══════════════════════════════════════════
    // LOAD DATA (Đã sửa lại luồng truy vấn)
    // ══════════════════════════════════════════
    private void loadQuestions() {
        listContainer.removeAll();
        pendingList.clear();

        // 1. Quét thẳng database bằng SQL thay vì dựa vào hàm getAllChats
        try (java.sql.Connection conn = com.dcdoctor.database.DBConnection.connect();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM chat WHERE status = 'PENDING'")) {

            while (rs.next()) {
                // Dùng constructor có tham số giống hệt lúc bạn làm data giả
                Chat chat = new Chat(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getString("message"),
                        rs.getString("response")
                );
                pendingList.add(chat);
            }
        } catch (Exception e) {
            System.err.println("Lỗi quét tin nhắn PENDING: " + e.getMessage());
        }

        // Cập nhật số lượng trên header
        countBadge.setText(String.valueOf(pendingList.size()));
        countBadge.setBackground(pendingList.size() > 0 ? DANGER : MUTED);

        if (pendingList.isEmpty()) {
            JLabel emptyLbl = new JLabel("✅ Không có ca nào cần xử lý", SwingConstants.CENTER);
            emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLbl.setForeground(MUTED);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(Box.createVerticalStrut(50));
            listContainer.add(emptyLbl);
        } else {
            for (Chat c : pendingList) {
                listContainer.add(makeQuestionCard(c));
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

}