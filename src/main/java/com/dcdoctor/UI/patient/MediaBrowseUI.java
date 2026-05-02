package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MediaBrowseUI extends JFrame {

    private JTextField txtTimKiem;
    private JComboBox<String> cboDanhMuc;
    private JTable bang;
    private DefaultTableModel model;

    public MediaBrowseUI() {
        setTitle("Duyệt Nội Dung Tiểu Đường");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        khoiTaoGiaoDien();
        taiDanhMuc();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout());

        pnlTop.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(20);
        pnlTop.add(txtTimKiem);

        pnlTop.add(new JLabel("Danh mục:"));
        cboDanhMuc = new JComboBox<>();
        cboDanhMuc.addItem("Tất cả");
        pnlTop.add(cboDanhMuc);

        JButton btnTim = new JButton("Tìm");
        pnlTop.add(btnTim);

        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Mã",
                "Tiêu đề",
                "Danh mục",
                "Nội dung"
        });

        bang = new JTable(model);
        add(new JScrollPane(bang), BorderLayout.CENTER);

        btnTim.addActionListener(e ->
                taiDuLieu(
                        txtTimKiem.getText().trim(),
                        cboDanhMuc.getSelectedItem().toString()
                )
        );
    }

    private void taiDanhMuc() {
        try (Connection conn = DBConnection.connect()) {

            String sql = "SELECT DISTINCT category FROM media";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cboDanhMuc.addItem(rs.getString("category"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải danh mục: " + e.getMessage());
        }
    }

    private void taiDuLieu(String tuKhoa, String danhMuc) {
        model.setRowCount(0);

        try (Connection conn = DBConnection.connect()) {

            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM media WHERE title LIKE ?"
            );

            if (!danhMuc.equals("Tất cả")) {
                sql.append(" AND category = ?");
            }

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(1, "%" + tuKhoa + "%");

            if (!danhMuc.equals("Tất cả")) {
                ps.setString(2, danhMuc);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("content")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }


}