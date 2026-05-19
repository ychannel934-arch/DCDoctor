package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecordUI extends JPanel {

    private int patientId;

    public RecordUI(int patientId) {
        this.patientId = patientId;
        setBackground(PatientUI.BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildRecordList());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PatientUI.BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 16, 10, 16));

        JLabel title = new JLabel("Hồ sơ bệnh án điện tử");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PatientUI.TEXT);

        JLabel sub = new JLabel("Lịch sử khám và điều trị");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(PatientUI.MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(sub);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel buildRecordList() {
        JPanel list = new JPanel();
        list.setBackground(PatientUI.BG);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(10, 16, 20, 16));

        String sql = """
                SELECT pr.record_date, u.full_name AS doctor_name, pr.diagnosis, pr.treatment, pr.medication 
                FROM patient_records pr 
                JOIN users u ON pr.doctor_id = u.id 
                WHERE pr.patient_id = ? 
                ORDER BY pr.record_date DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, this.patientId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;

                // Format lại ngày tháng từ YYYY-MM-DD sang DD/MM/YYYY
                String rawDate = rs.getString("record_date");
                String formattedDate = rawDate;
                try {
                    LocalDate date = LocalDate.parse(rawDate);
                    formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {}

                String doctorName = rs.getString("doctor_name");
                String diagnosis = rs.getString("diagnosis");
                String treatment = rs.getString("treatment");
                String medication = rs.getString("medication");

                if (medication == null || medication.trim().isEmpty()) {
                    medication = "Không có";
                }

                // Truyền tách biệt 3 thông số
                list.add(makeRecordCard(formattedDate, doctorName, diagnosis, treatment, medication));
                list.add(Box.createVerticalStrut(14));
            }

            if (!hasData) {
                JLabel emptyLbl = new JLabel("Bạn chưa có hồ sơ bệnh án nào trong hệ thống.");
                emptyLbl.setForeground(PatientUI.MUTED);
                list.add(emptyLbl);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JLabel errLbl = new JLabel("Lỗi tải dữ liệu: " + e.getMessage());
            errLbl.setForeground(Color.RED);
            list.add(errLbl);
        }

        return list;
    }

    private JPanel makeRecordCard(String date, String doctor, String diagnosis, String treatment, String medication) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PatientUI.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PatientUI.BORDER_C);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                g2.setColor(PatientUI.PRIMARY);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.fillRect(3, 0, 3, getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 16));
        // Cho phép card giãn nở chiều cao vô hạn để chứa text dài
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        // Đã xóa Emoji bị lỗi
        JLabel dateLbl = new JLabel("Ngày: " + date);
        dateLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateLbl.setForeground(PatientUI.PRIMARY);

        JLabel statusLbl = new JLabel("Hoàn tất", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLbl.setForeground(PatientUI.SUCCESS);
        statusLbl.setBorder(new CompoundBorder(
                new LineBorder(PatientUI.SUCCESS, 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));

        topRow.add(dateLbl, BorderLayout.WEST);
        topRow.add(statusLbl, BorderLayout.EAST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Đã xóa Emoji bị lỗi
        JLabel docLbl = new JLabel("BS. " + doctor);
        docLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        docLbl.setForeground(PatientUI.TEXT);
        docLbl.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Bọc HTML và set width cứng = 260px để ép nội dung tự động xuống dòng
        String htmlStyle = "style='width: 260px; margin-bottom: 5px;'";

        JLabel diagLbl = new JLabel("<html><div " + htmlStyle + "><b>Chẩn đoán:</b> " + diagnosis + "</div></html>");
        JLabel treatLbl = new JLabel("<html><div " + htmlStyle + "><b>Điều trị:</b> " + treatment + "</div></html>");
        JLabel rxLbl = new JLabel("<html><div " + htmlStyle + "><b>Đơn thuốc:</b> " + medication + "</div></html>");

        Font contentFont = new Font("Segoe UI", Font.PLAIN, 12);
        diagLbl.setFont(contentFont);
        treatLbl.setFont(contentFont);
        rxLbl.setFont(contentFont);

        diagLbl.setForeground(PatientUI.SUB);
        treatLbl.setForeground(PatientUI.SUB);
        rxLbl.setForeground(PatientUI.SUB);

        content.add(docLbl);
        content.add(diagLbl);
        content.add(treatLbl);
        content.add(rxLbl);

        card.add(topRow, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }
}