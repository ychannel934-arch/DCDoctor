package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatUI extends JPanel {

    // ── Màu sắc ───────────────────────────────────────────
    private static final Color BG       = new Color(0xF8FAFF);
    private static final Color WHITE    = Color.WHITE;
    private static final Color PRIMARY  = new Color(0x3B82F6);
    private static final Color AI_BG    = new Color(0xF1F5F9);
    private static final Color USER_BG  = new Color(0x3B82F6);
    private static final Color TEXT     = new Color(0x1E293B);
    private static final Color MUTED    = new Color(0x64748B);
    private static final Color BORDER_C = new Color(0xE2E8F0);
    private static final Color SUCCESS  = new Color(0x10B981);

    private JPanel messageContainer;
    private JScrollPane scrollPane;
    private JTextArea inputArea; // Đây chính là ô nhập (txtInput của ông)
    private JButton btnSend;
    private Timer chatListenerTimer;

    // Giả lập ID bệnh nhân đang đăng nhập (Sau này lấy từ session)
    private final int PATIENT_ID = 1;

    public ChatUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMessages(),  BorderLayout.CENTER);
        add(buildInputBar(),  BorderLayout.SOUTH);

        addSystemNote("Xin chào! Bạn có thể đặt câu hỏi về bệnh tiểu đường. AI sẽ hỗ trợ bạn.");

        startChatListener();
    }

    // ══════════════════════════════════════════
    // TOP BAR
    // ══════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(WHITE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(WHITE);

        JLabel avatar = new JLabel("🤖", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0xEFF6FF));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(0xBFDBFE), 1));

        JPanel info = new JPanel();
        info.setBackground(WHITE);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Trợ lý AI & Bác sĩ");
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(TEXT);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusRow.setBackground(WHITE);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SUCCESS);
                g2.fillOval(0, 2, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 12));
        dot.setOpaque(false);

        JLabel statusLbl = new JLabel("Đang hoạt động");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(SUCCESS);

        statusRow.add(dot);
        statusRow.add(statusLbl);

        info.add(name);
        info.add(statusRow);

        left.add(avatar);
        left.add(info);
        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ══════════════════════════════════════════
    // MESSAGE AREA
    // ══════════════════════════════════════════
    private JScrollPane buildMessages() {
        messageContainer = new JPanel();
        messageContainer.setBackground(BG);
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBorder(new EmptyBorder(14, 12, 14, 12));

        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // ══════════════════════════════════════════
    // INPUT BAR
    // ══════════════════════════════════════════
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(WHITE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C),
                new EmptyBorder(10, 14, 10, 14)
        ));

        inputArea = new JTextArea(2, 20);
        inputArea.setBackground(new Color(0xF8FAFF));
        inputArea.setForeground(TEXT);
        inputArea.setCaretColor(PRIMARY);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(new CompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        inputArea.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendQuestion(); // GỌI HÀM CHÍNH Ở ĐÂY
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(null);
        inputScroll.setPreferredSize(new Dimension(0, 56));

        btnSend = new JButton("➤") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? PRIMARY : BORDER_C);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSend.setForeground(WHITE);
        btnSend.setPreferredSize(new Dimension(44, 44));
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener(e -> sendQuestion());

        JLabel hint = new JLabel("Enter để gửi • Shift+Enter xuống dòng");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(MUTED);

        JPanel right = new JPanel();
        right.setBackground(WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(btnSend);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(WHITE);
        bottom.add(hint, BorderLayout.WEST);

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(WHITE);
        inputWrapper.add(inputScroll, BorderLayout.CENTER);
        inputWrapper.add(bottom, BorderLayout.SOUTH);

        bar.add(inputWrapper, BorderLayout.CENTER);
        bar.add(right,        BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════
    // GỬI CÂU HỎI VÀ XỬ LÝ LÔ-GIC AI (ĐÃ SỬA CHUẨN)
    // ══════════════════════════════════════════
    private void sendQuestion() {
        String question = inputArea.getText().trim();
        if (question.isEmpty()) return;

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 1. Hiển thị tin nhắn người dùng lên UI
        addBubble(question, true, time);
        inputArea.setText("");
        btnSend.setEnabled(false); // Khóa nút gửi tạm thời

        // Dùng Thread để DB không làm đơ giao diện
        new Thread(() -> {
            try { Thread.sleep(600); } catch (Exception ignored) {} // Giả lập độ trễ AI suy nghĩ

            // 2. Chọc Database tìm câu trả lời của AI
            String aiAnswer = getAIResponseFromDB(question);

            try (Connection conn = DBConnection.connect()) {
                String sql = "INSERT INTO chat (patient_id, message, response, status) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, PATIENT_ID);
                ps.setString(2, question);

                if (aiAnswer != null) {
                    // CÓ KẾT QUẢ AI
                    ps.setString(3, aiAnswer);
                    ps.setString(4, "AI_ANSWERED");
                } else {
                    // KHÔNG CÓ KẾT QUẢ -> ĐẨY CHO BÁC SĨ
                    ps.setNull(3, java.sql.Types.VARCHAR);
                    ps.setString(4, "PENDING");
                }
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // 3. Hiển thị phản hồi lên màn hình (Trong luồng UI)
            SwingUtilities.invokeLater(() -> {
                String aiTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

                if (aiAnswer != null) {
                    addBubble(aiAnswer, false, aiTime);
                } else {
                    addSystemNote("📨 Câu hỏi đã được chuyển đến bác sĩ. Vui lòng chờ phản hồi.");
                }

                btnSend.setEnabled(true); // Mở khóa nút gửi
                scrollToBottom();
            });
        }).start();

        scrollToBottom();
    }

    // Hàm truy vấn bảng ai_knowledge
    private String getAIResponseFromDB(String userQuestion) {
        String answer = null;
        String sql = "SELECT answer FROM ai_knowledge WHERE ? LIKE '%' || keyword || '%' LIMIT 1";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userQuestion.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                answer = rs.getString("answer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }

    // ══════════════════════════════════════════
    // LẮNG NGHE BÁC SĨ TRẢ LỜI (TIMER)
    // ══════════════════════════════════════════
    private void startChatListener() {
        chatListenerTimer = new Timer(3000, e -> {
            try (Connection conn = DBConnection.connect()) {
                String sql = "SELECT * FROM chat WHERE patient_id = ? AND status = 'DOC_ANSWERED'";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, PATIENT_ID);
                ResultSet rs = ps.executeQuery();

                boolean hasNewMsg = false;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String docRes = rs.getString("response");

                    addBubble("👨‍⚕️ Bác sĩ: " + docRes, false, "Vừa xong");

                    PreparedStatement updatePs = conn.prepareStatement("UPDATE chat SET status = 'READ' WHERE id = ?");
                    updatePs.setInt(1, id);
                    updatePs.executeUpdate();

                    hasNewMsg = true;
                }

                if (hasNewMsg) scrollToBottom();

            } catch (SQLException ex) {
                // Nuốt lỗi
            }
        });
        chatListenerTimer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (chatListenerTimer != null && chatListenerTimer.isRunning()) {
            chatListenerTimer.stop();
        }
    }

    // ══════════════════════════════════════════
    // RENDER UI (GIỮ NGUYÊN CODE CŨ CỦA ÔNG)
    // ══════════════════════════════════════════
    private void addBubble(String text, boolean isUser, String time) {
        JPanel row = new JPanel();
        row.setBackground(BG);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        row.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel avatar = new JLabel(isUser ? "BN" : "AI", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatar.setForeground(WHITE);
        avatar.setPreferredSize(new Dimension(28, 28));
        avatar.setMinimumSize(new Dimension(28, 28));
        avatar.setMaximumSize(new Dimension(28, 28));
        avatar.setOpaque(true);
        avatar.setBackground(isUser ? PRIMARY : new Color(0x6366F1));

        JPanel col = new JPanel();
        col.setBackground(BG);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JTextArea bubble = new JTextArea(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isUser ? USER_BG : AI_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bubble.setForeground(isUser ? WHITE : TEXT);
        bubble.setOpaque(false);
        bubble.setEditable(false);
        bubble.setLineWrap(true);
        bubble.setWrapStyleWord(true);
        bubble.setBorder(new EmptyBorder(10, 14, 10, 14));
        bubble.setMaximumSize(new Dimension(260, Integer.MAX_VALUE));
        bubble.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED);
        timeLbl.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        timeLbl.setBorder(new EmptyBorder(3, 4, 0, 4));

        col.add(bubble);
        col.add(timeLbl);

        if (isUser) {
            row.add(Box.createHorizontalGlue());
            row.add(col);
            row.add(Box.createHorizontalStrut(8));
            row.add(avatar);
        } else {
            row.add(avatar);
            row.add(Box.createHorizontalStrut(8));
            row.add(col);
            row.add(Box.createHorizontalGlue());
        }

        messageContainer.add(row);
        messageContainer.add(Box.createVerticalStrut(4));
        messageContainer.revalidate();
        messageContainer.repaint();
    }

    private void addSystemNote(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("<html><div style='text-align:center'>" + text + "</div></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MUTED);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(0xF1F5F9));
        lbl.setBorder(new CompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));

        row.add(lbl);
        messageContainer.add(row);
        messageContainer.add(Box.createVerticalStrut(8));
        messageContainer.revalidate();
        messageContainer.repaint();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}