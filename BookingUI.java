package com.dcdoctor.UI.patient;

import com.dcdoctor.database.AppointmentDAO;
import com.dcdoctor.database.DBConnection;
import com.dcdoctor.model.Appointment;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingUI extends JPanel {

    private static final Color BG       = new Color(0xF8FAFF);
    private static final Color WHITE    = Color.WHITE;
    private static final Color PRIMARY  = new Color(0x3B82F6);
    private static final Color SUCCESS  = new Color(0x10B981);
    private static final Color TEXT     = new Color(0x1E293B);
    private static final Color MUTED    = new Color(0x64748B);
    private static final Color BORDER_C = new Color(0xE2E8F0);
    private static final Color DANGER   = new Color(0xEF4444);

    private int selectedDoctorId = -1;
    private JTextField txtDate;
    private JTextField txtNote;
    private String selectedTime = "";
    private JButton selectedTimeBtn = null;

    private static final String[] TIMES = {
            "08:00", "08:30", "09:00", "09:30",
            "10:00", "10:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30"
    };

    public BookingUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(buildSubmitBtn(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(WHITE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(14, 16, 14, 16)
        ));
        JLabel title = new JLabel("📅  Đặt lịch khám");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setBackground(BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(16, 16, 24, 16));

        form.add(buildSectionLabel("👨‍⚕️  Chọn bác sĩ"));
        form.add(Box.createVerticalStrut(8));
        form.add(buildDoctorPicker());
        form.add(Box.createVerticalStrut(18));

        form.add(buildSectionLabel("📆  Ngày khám"));
        form.add(Box.createVerticalStrut(8));
        form.add(buildDateRow());
        form.add(Box.createVerticalStrut(18));

        form.add(buildSectionLabel("🕐  Chọn giờ"));
        form.add(Box.createVerticalStrut(8));
        form.add(buildTimePicker());
        form.add(Box.createVerticalStrut(18));

        form.add(buildSectionLabel("📝  Ghi chú (tuỳ chọn)"));
        form.add(Box.createVerticalStrut(8));
        form.add(buildNoteField());

        return form;
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ĐÃ SỬA CHUẨN QUERY VÀ CỘT THEO BẢNG USERS
    private JPanel buildDoctorPicker() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup group = new ButtonGroup();
        boolean isFirst = true;

        String sql = "SELECT id, full_name, specialty FROM users WHERE role = 'DOCTOR'";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                String spec = rs.getString("specialty");

                // Xử lý null cho specialty để UI không bị lỗi hiển thị
                spec = (spec != null) ? spec : "Chưa cập nhật chuyên khoa";

                JPanel card = buildDoctorCard(id, name, spec, group, isFirst);
                panel.add(card);
                panel.add(Box.createVerticalStrut(8));

                if (isFirst) {
                    selectedDoctorId = id;
                    isFirst = false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi load danh sách bác sĩ: " + e.getMessage());
        }

        return panel;
    }

    private JPanel buildDoctorCard(int id, String name, String spec, ButtonGroup group, boolean isFirst) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Fix lỗi String index out of bounds nếu tên bác sĩ quá ngắn
        String avatarChar = name.length() > 0 ? String.valueOf(name.charAt(name.length() - 1)).toUpperCase() : "D";
        if (name.contains("Dr. ")) {
            avatarChar = String.valueOf(name.charAt(4)).toUpperCase();
        }

        JLabel avatar = new JLabel(avatarChar, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(WHITE);
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setOpaque(true);
        avatar.setBackground(PRIMARY);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(TEXT);

        JLabel specLbl = new JLabel(spec);
        specLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        specLbl.setForeground(MUTED);

        info.add(nameLbl);
        info.add(specLbl);

        JRadioButton radio = new JRadioButton();
        radio.setOpaque(false);
        radio.setSelected(isFirst);
        radio.addActionListener(e -> {
            selectedDoctorId = id;
            updateCardBorder(card, true);
        });
        group.add(radio);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { radio.doClick(); }
        });

        card.add(avatar, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        card.add(radio,  BorderLayout.EAST);

        if (isFirst) updateCardBorder(card, true);

        return card;
    }

    private void updateCardBorder(JPanel card, boolean selected) {
        card.setBorder(new CompoundBorder(
                new LineBorder(selected ? PRIMARY : BORDER_C, selected ? 2 : 1, true),
                new EmptyBorder(11, 13, 11, 13)
        ));
        card.repaint();
    }

    private JPanel buildDateRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDate = new JTextField(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDate.setForeground(TEXT);
        txtDate.setBorder(new CompoundBorder(new LineBorder(BORDER_C, 1, true), new EmptyBorder(10, 14, 10, 14)));

        JLabel calIcon = new JLabel("📅");
        calIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        calIcon.setBorder(new EmptyBorder(0, 8, 0, 0));

        row.add(calIcon,  BorderLayout.WEST);
        row.add(txtDate,  BorderLayout.CENTER);
        return row;
    }

    private JPanel buildTimePicker() {
        JPanel grid = new JPanel(new GridLayout(3, 4, 8, 8));
        grid.setBackground(BG);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String time : TIMES) grid.add(makeTimeBtn(time));
        return grid;
    }

    private JButton makeTimeBtn(String time) {
        JButton btn = new JButton(time) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? PRIMARY : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(isSelected() ? PRIMARY.darker() : BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(MUTED);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            if (selectedTimeBtn != null) {
                selectedTimeBtn.setSelected(false);
                selectedTimeBtn.setForeground(MUTED);
            }
            btn.setSelected(true);
            btn.setForeground(WHITE);
            selectedTimeBtn = btn;
            selectedTime = time;
        });
        return btn;
    }

    private JTextField buildNoteField() {
        txtNote = new JTextField();
        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNote.setBorder(new CompoundBorder(new LineBorder(BORDER_C, 1, true), new EmptyBorder(10, 14, 10, 14)));
        txtNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txtNote;
    }

    private JPanel buildSubmitBtn() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(10, 16, 20, 16));

        JButton btnSubmit = new JButton("Xác nhận đặt lịch") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, new Color(0x6366F1));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSubmit.setForeground(WHITE);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(0, 50));

        btnSubmit.addActionListener(e -> saveBooking());

        wrap.add(btnSubmit, BorderLayout.CENTER);
        return wrap;
    }

    private void saveBooking() {
        String date = txtDate.getText().trim();
        String note = txtNote.getText().trim();

        if (selectedDoctorId == -1 || date.isEmpty() || selectedTime.isEmpty()) {
            showToast("⚠️ Vui lòng chọn đầy đủ bác sĩ, ngày và giờ khám!", DANGER);
            return;
        }

        try {
            AppointmentDAO dao = new AppointmentDAO();

            if (dao.isConflict(selectedDoctorId, date, selectedTime)) {
                showToast("❌ Bác sĩ đã kín lịch vào giờ này. Chọn giờ khác nhé!", DANGER);
                return;
            }

            Appointment newAppointment = new Appointment();
            newAppointment.setPatientId(1);
            newAppointment.setDoctorId(selectedDoctorId);
            newAppointment.setAppointmentDate(date);
            newAppointment.setAppointmentTime(selectedTime);
            newAppointment.setNotes(note);
            newAppointment.setStatus("Pending");

            boolean isSuccess = dao.bookAppointment(newAppointment);

            if (isSuccess) {
                showToast("✅ Đặt lịch thành công! " + date + " lúc " + selectedTime, SUCCESS);

                txtNote.setText("");
                if (selectedTimeBtn != null) {
                    selectedTimeBtn.setSelected(false);
                    selectedTimeBtn.setForeground(MUTED);
                    selectedTime = "";
                }
            } else {
                showToast("❌ Hệ thống đang lỗi, không thể lưu lịch!", DANGER);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đặt lịch: " + e.getMessage());
            showToast("❌ Lỗi CSDL: " + e.getMessage(), DANGER);
        } catch (Exception e) {
            System.err.println("Lỗi hệ thống: " + e.getMessage());
            showToast("❌ Lỗi không xác định", DANGER);
        }
    }

    private void showToast(String msg, Color color) {
        JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(this));
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(WHITE);
        lbl.setBackground(color);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(10, 18, 10, 18));
        toast.add(lbl);
        toast.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation((screen.width - toast.getWidth()) / 2, screen.height - toast.getHeight() - 80);
        toast.setVisible(true);
        new Timer(2500, ev -> toast.dispose()).start();
    }
}