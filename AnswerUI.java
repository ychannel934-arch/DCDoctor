package com.dcdoctor.UI.doctor;

import com.dcdoctor.service.ChatService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

import static com.dcdoctor.UI.doctor.DoctorUI.*;

public class AnswerUI extends JPanel {

    private JTextField promptInput;
    private JTextArea answerArea;
    private JButton generateBtn;
    private JButton sendBtn;
    private ChatService chatService;

    public AnswerUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        try { chatService = new ChatService(); } catch (Exception ignored) {}

        // Header trên cùng (Top Bar chuẩn Mobile)
        add(buildHeader(), BorderLayout.NORTH);

        // Nội dung chính nằm ở giữa, cuộn dọc
        JPanel contentScroll = new JPanel(new BorderLayout());
        contentScroll.setBackground(BG);

        JPanel verticalStack = new JPanel();
        verticalStack.setLayout(new BoxLayout(verticalStack, BoxLayout.Y_AXIS));
        verticalStack.setBackground(BG);
        verticalStack.setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. Tóm tắt Bệnh án (Context)
        verticalStack.add(buildMedicalContext());
        verticalStack.add(Box.createVerticalStrut(20));

        // 2. Câu hỏi của bệnh nhân (Chat Bubble)
        verticalStack.add(buildPatientMessage());
        verticalStack.add(Box.createVerticalStrut(20));

        // 3. Công cụ AI (Prompt + Gen + Edit)
        verticalStack.add(buildAITool());

        contentScroll.add(verticalStack, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(contentScroll);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel backBtn = new JLabel("‹ ");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        backBtn.setForeground(TEXT);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Thêm event quay lại trang trước ở đây

        JLabel title = new JLabel("Chi tiết ca tư vấn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setBackground(CARD);
        left.add(backBtn);
        left.add(title);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JPanel buildMedicalContext() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel title = new JLabel("📋 Tóm tắt bệnh án: Lê Thị Mai");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TEXT);

        JLabel info = new JLabel("<html>Đường huyết gần nhất: <b>7.2 mmol/L</b><br>Đang dùng: <b>Metformin 500mg</b></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(SUB);

        panel.add(title, BorderLayout.NORTH);
        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPatientMessage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel nameLbl = new JLabel("Bệnh nhân hỏi lúc 10:20 AM");
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nameLbl.setForeground(MUTED);
        nameLbl.setBorder(new EmptyBorder(0, 4, 4, 0));

        JTextArea msgArea = new JTextArea("Bác sĩ ơi, chỉ số đường huyết sáng nay của tôi là 7.2 mmol/L. Như vậy có ổn không? Tôi có cần tăng liều thuốc không?");
        msgArea.setBackground(CARD);
        msgArea.setForeground(TEXT);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setEditable(false);
        msgArea.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        panel.add(nameLbl, BorderLayout.NORTH);
        panel.add(msgArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAITool() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Header AI
        JLabel title = new JLabel("🤖 Trợ lý AI Gemini");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Nhập từ khóa để AI viết câu trả lời hoàn chỉnh:");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(SUB);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ô nhập Prompt ngắn
        promptInput = new JTextField();
        promptInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        promptInput.setBackground(SURFACE);
        promptInput.setForeground(TEXT);
        promptInput.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        promptInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        promptInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Nút Generate
        generateBtn = makeBtn("⚡  Tạo câu trả lời", ACCENT, Color.WHITE);
        generateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateBtn.addActionListener(e -> generateAnswer());

        // Ô hiển thị & edit kết quả
        answerArea = new JTextArea("Kết quả AI sẽ hiện ở đây. Bác sĩ có thể chỉnh sửa trước khi gửi.");
        answerArea.setBackground(SURFACE);
        answerArea.setForeground(SUB);
        answerArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        answerArea.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane scroll = new JScrollPane(answerArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));

        // Nút Gửi
        sendBtn = makeBtn("➤  Gửi cho bệnh nhân", new Color(0x10B981), Color.WHITE); // Màu xanh lá
        sendBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sendBtn.setEnabled(false); // Khóa nút gửi cho đến khi có AI gen ra
        sendBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã gửi câu trả lời tới bệnh nhân!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(hint);
        panel.add(Box.createVerticalStrut(8));
        panel.add(promptInput);
        panel.add(Box.createVerticalStrut(10));
        panel.add(generateBtn);
        panel.add(Box.createVerticalStrut(16));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sendBtn);

        return panel;
    }

    private void generateAnswer() {
        String prompt = promptInput.getText().trim();
        if (prompt.isEmpty()) {
            answerArea.setText("⚠️ Vui lòng nhập từ khóa gợi ý (VD: Khuyên tiếp tục duy trì, không cần đổi thuốc).");
            answerArea.setForeground(DANGER);
            return;
        }

        generateBtn.setEnabled(false);
        generateBtn.setText("⏳  Đang sinh văn bản...");
        answerArea.setForeground(MUTED);
        answerArea.setText("AI đang viết dựa trên từ khóa của bạn...");

        new Thread(() -> {
            try {
                // Giả lập gọi API
                Thread.sleep(1500);
                String response = (chatService != null)
                        ? chatService.handleMessage("Viết câu trả lời bác sĩ dựa trên ý sau: " + prompt, null)
                        : "Chào chị Mai, chỉ số 7.2 mmol/L buổi sáng là mức rất ổn định và nằm trong mục tiêu kiểm soát. Chị hoàn toàn không cần tăng liều Metformin nhé. Cứ tiếp tục duy trì chế độ ăn và vận động như hiện tại.";

                SwingUtilities.invokeLater(() -> {
                    answerArea.setForeground(TEXT);
                    answerArea.setText(response);
                    generateBtn.setEnabled(true);
                    generateBtn.setText("⚡  Tạo lại");
                    sendBtn.setEnabled(true); // Mở khóa nút gửi
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    answerArea.setForeground(DANGER);
                    answerArea.setText("Lỗi: " + ex.getMessage());
                    generateBtn.setEnabled(true);
                    generateBtn.setText("⚡  Thử lại");
                });
            }
        }).start();
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}