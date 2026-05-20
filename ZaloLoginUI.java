package com.dcdoctor.UI;

import com.dcdoctor.UI.patient.PatientUI;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ZaloLoginUI extends JFrame {

    private static final String APP_ID = "1966353959448781204";
    private static final String APP_SECRET = "S70djm6CKA1FjVA1LcYT";
    private static final String REDIRECT_URI = "http://localhost:9999/callback";

    public ZaloLoginUI() {
        setTitle("Đăng nhập bằng Zalo - DCDoctor");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("HỆ THỐNG ĐĂNG NHẬP BỆNH NHÂN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton btnLogin = new JButton("Đăng nhập với Zalo");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 104, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        btnLogin.addActionListener(e -> new Thread(this::loginWithZalo).start());

        add(lblTitle, BorderLayout.NORTH);
        add(btnLogin, BorderLayout.CENTER);
    }

    private void loginWithZalo() {
        try {
            int port = 9999;
            ServerSocket server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress("localhost", port));

            String authUrl = "https://oauth.zaloapp.com/v4/permission"
                    + "?app_id=" + APP_ID
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                    + "&state=dcdoctor_auth_process"
                    + "&prompt=select_account";

            Desktop.getDesktop().browse(new URI(authUrl));

            Socket socket = server.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String requestLine = in.readLine();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println();
            out.println("<html><body style='text-align:center;padding-top:50px;font-family:Arial;'>"
                    + "<h2 style='color:#0068ff;'>Xác thực Zalo thành công!</h2>"
                    + "<p>Vui lòng quay lại ứng dụng để tiếp tục.</p>"
                    + "</body></html>");
            out.flush();

            socket.close();
            server.close();

            String code = extractCode(requestLine);

            if (code != null) {
                String accessToken = getAccessToken(code);
                if (accessToken != null) {
                    fetchUserInfo(accessToken);
                } else {
                    showError("Lỗi: Không thể đổi mã code lấy Access Token.");
                }
            } else {
                showError("Lỗi: Zalo không trả về mã xác thực (code).");
            }

        } catch (BindException e) {
            showError("Cổng 9999 đang bận. Vui lòng tắt bớt các phiên đăng nhập cũ.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private String extractCode(String requestLine) {
        if (requestLine == null) return null;
        try {
            String path = requestLine.split(" ")[1];
            URI uri = new URI("http://localhost" + path);
            String query = uri.getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1 && "code".equals(pair[0])) {
                        return pair[1];
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getAccessToken(String code) throws Exception {
        URL url = new URL("https://oauth.zaloapp.com/v4/access_token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("secret_key", APP_SECRET);

        String params = "app_id=" + APP_ID
                + "&code=" + code
                + "&grant_type=authorization_code"
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONObject json = new JSONObject(sb.toString());
            return json.optString("access_token", null);
        }
        return null;
    }

    private void fetchUserInfo(String accessToken) {
        try {
            URL url = new URL("https://graph.zalo.me/v2.0/me?fields=id,name,picture");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("access_token", accessToken);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONObject json = new JSONObject(sb.toString());
            String zaloId = json.getString("id");
            String name = json.getString("name");
            String avatar = json.optJSONObject("picture") != null ?
                    json.getJSONObject("picture").getJSONObject("data").getString("url") : "";

            // Gọi hàm xử lý DB trực tiếp để lấy được số ID
            int dbUserId = saveOrGetZaloUser(name, zaloId, avatar);

            SwingUtilities.invokeLater(() -> {
                if (dbUserId != -1) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nChào mừng bệnh nhân: " + name);
                    this.dispose();
                    // Đã sửa: Truyền 2 tham số (ID, Tên) vào PatientUI
                    new PatientUI(dbUserId, name).setVisible(true);
                } else {
                    showError("Lỗi khi lưu thông tin Zalo vào Database.");
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi khi lấy thông tin bệnh nhân từ Zalo.");
        }
    }

    // =========================================================
    // HÀM TỰ ĐỘNG LƯU HOẶC LẤY ID TỪ DATABASE
    // =========================================================
    private int saveOrGetZaloUser(String name, String zaloId, String avatar) {
        int userId = -1;
        try (java.sql.Connection conn = com.dcdoctor.database.DBConnection.connect()) {
            if (conn == null) return -1;

            // 1. Kiểm tra xem Zalo ID này đã có trong DB chưa
            String checkSql = "SELECT id FROM zalo_users WHERE zalo_id = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, zaloId);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id"); // Có rồi thì trả về ID luôn
                }
            }

            // 2. Nếu chưa có -> Thêm mới vào DB
            String insertSql = "INSERT INTO zalo_users (name, zalo_id, avatar) VALUES (?, ?, ?)";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, zaloId);
                ps.setString(3, avatar);
                ps.executeUpdate();

                // Lấy ID tự động tăng vừa sinh ra
                java.sql.ResultSet rsKeys = ps.getGeneratedKeys();
                if (rsKeys.next()) {
                    userId = rsKeys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    private void showError(String msg) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, msg, "Thông báo lỗi", JOptionPane.ERROR_MESSAGE));
    }
}