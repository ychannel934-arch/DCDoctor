package com.dcdoctor.UI.hospital;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CreateDoctorUI extends JFrame {

    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField specialtyField;

    private JButton createButton;
    private JButton clearButton;

    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public CreateDoctorUI() {

        setTitle("DC Doctor - Create Doctor");
        setSize(950, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

        loadDoctors();
    }

    private void initComponents() {

        getContentPane().setBackground(new Color(15, 23, 42));

        setLayout(new BorderLayout(15, 15));

        // ===== TITLE =====

        JLabel titleLabel = new JLabel("🏥 CREATE DOCTOR ACCOUNT");

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 10, 20)
        );

        add(titleLabel, BorderLayout.NORTH);

        // ===== MAIN PANEL =====

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));

        mainPanel.setBackground(new Color(15, 23, 42));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        // ===== FORM PANEL =====

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));

        formPanel.setBackground(new Color(30, 41, 59));

        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(51, 65, 85), 1
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // ===== LABELS =====

        JLabel fullNameLabel = createLabel(
                "Họ và tên", labelFont
        );

        JLabel usernameLabel = createLabel(
                "Tên đăng nhập", labelFont
        );

        JLabel passwordLabel = createLabel(
                "Mật khẩu", labelFont
        );

        JLabel specialtyLabel = createLabel(
                "Chuyên khoa", labelFont
        );

        // ===== FIELDS =====

        fullNameField = createStyledField(inputFont);

        usernameField = createStyledField(inputFont);

        passwordField = new JPasswordField();

        stylePasswordField(passwordField, inputFont);

        specialtyField = createStyledField(inputFont);

        // ===== ADD FORM =====

        formPanel.add(fullNameLabel);

        formPanel.add(fullNameField);

        formPanel.add(usernameLabel);

        formPanel.add(usernameField);

        formPanel.add(passwordLabel);

        formPanel.add(passwordField);

        formPanel.add(specialtyLabel);

        formPanel.add(specialtyField);

        // ===== BUTTONS =====

        createButton = new JButton("Tạo Tài Khoản");

        stylePrimaryButton(createButton);

        clearButton = new JButton("Làm Mới");

        styleDangerButton(clearButton);

        formPanel.add(createButton);

        formPanel.add(clearButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // ===== TABLE =====

        String[] columns = {
                "ID",
                "Họ và Tên",
                "Tên Đăng Nhập",
                "Mật Khẩu",
                "Chuyên Khoa"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        doctorTable = new JTable(tableModel);

        doctorTable.setBackground(new Color(30, 41, 59));

        doctorTable.setForeground(Color.WHITE);

        doctorTable.setRowHeight(38);

        doctorTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        doctorTable.setSelectionBackground(
                new Color(59, 130, 246)
        );

        doctorTable.setGridColor(
                new Color(51, 65, 85)
        );

        doctorTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        doctorTable.getTableHeader().setBackground(
                new Color(59, 130, 246)
        );

        doctorTable.getTableHeader().setForeground(
                Color.WHITE
        );

        JScrollPane scrollPane = new JScrollPane(doctorTable);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.getViewport().setBackground(
                new Color(15, 23, 42)
        );

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // ===== EVENTS =====

        createButton.addActionListener(
                e -> createDoctorAccount()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );
    }

    private JLabel createLabel(String text, Font font) {

        JLabel label = new JLabel(text);

        label.setForeground(Color.WHITE);

        label.setFont(font);

        return label;
    }

    private JTextField createStyledField(Font font) {

        JTextField field = new JTextField();

        field.setBackground(new Color(51, 65, 85));

        field.setForeground(Color.WHITE);

        field.setCaretColor(Color.WHITE);

        field.setFont(font);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(71, 85, 105)
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 10, 10, 10
                        )
                )
        );

        return field;
    }

    private void stylePasswordField(
            JPasswordField field,
            Font font
    ) {

        field.setBackground(new Color(51, 65, 85));

        field.setForeground(Color.WHITE);

        field.setCaretColor(Color.WHITE);

        field.setFont(font);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(71, 85, 105)
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 10, 10, 10
                        )
                )
        );
    }

    private void stylePrimaryButton(JButton button) {

        button.setBackground(new Color(59, 130, 246));

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 20, 12, 20
                )
        );
    }

    private void styleDangerButton(JButton button) {

        button.setBackground(new Color(239, 68, 68));

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 20, 12, 20
                )
        );
    }

    private void createDoctorAccount() {

        String fullName = fullNameField.getText().trim();

        String username = usernameField.getText().trim();

        String password = new String(
                passwordField.getPassword()
        );

        String specialty = specialtyField.getText().trim();

        if (fullName.isEmpty()
                || username.isEmpty()
                || password.isEmpty()
                || specialty.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin!"
            );

            return;
        }

        String sql = """
                INSERT INTO users
                (full_name, username, password, role, specialty)
                VALUES (?, ?, ?, 'DOCTOR', ?)
                """;

        try (
                Connection conn = DBConnection.connect();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, fullName);

            ps.setString(2, username);

            ps.setString(3, password);

            ps.setString(4, specialty);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Tạo tài khoản bác sĩ thành công!"
            );

            clearFields();

            loadDoctors();

        } catch (
                SQLIntegrityConstraintViolationException e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tên đăng nhập đã tồn tại!"
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi cơ sở dữ liệu: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void loadDoctors() {
        // 1. Xóa toàn bộ các dòng hiện có trên bảng để nạp mới
        tableModel.setRowCount(0);

        String sql = """
            SELECT id, full_name, username,
            password, specialty
            FROM users
            WHERE role = 'DOCTOR'
            ORDER BY id DESC
            """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            // 2. Khởi tạo biến đếm số thứ tự bắt đầu từ 1
            int stt = 1;

            while (rs.next()) {
                // 3. Thêm dòng mới vào tableModel
                tableModel.addRow(new Object[]{
                        stt++, // Sử dụng biến đếm và tăng dần (1, 2, 3...)
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("specialty")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi cơ sở dữ liệu: " + e.getMessage()
            );
        }
    }

    private void clearFields() {

        fullNameField.setText("");

        usernameField.setText("");

        passwordField.setText("");

        specialtyField.setText("");

        fullNameField.requestFocus();
    }
}