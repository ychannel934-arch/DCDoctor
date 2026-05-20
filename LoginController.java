package com.dcdoctor.adminserver.controller;

import com.dcdoctor.adminserver.model.ActivityLog;
import com.dcdoctor.adminserver.model.User;
import com.dcdoctor.adminserver.repository.ActivityLogRepository;
import com.dcdoctor.adminserver.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // Tiêm repository vào để ghi log thật xuống SQLite
    @Autowired
    private ActivityLogRepository activityLogRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trỏ đến login.html
    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 1. Kiểm tra mật khẩu
            if (user.getPassword().equals(password)) {

                // 2. Kiểm tra quyền hạn - Chỉ cho phép ADMIN vào web quản trị
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    session.setAttribute("adminUser", user);

                    // ========================================================
// 🚀 THÊM MỚI: TỰ ĐỘNG GHI NHẬT KÝ HOẠT ĐỘNG THẬT VÀO SQLITE
                    try {
                        ActivityLog log = new ActivityLog();

                        // Đã sửa thành setUsername chuẩn theo model của bạn
                        log.setUsername(user.getEmail());
                        log.setRole(user.getRole());
                        log.setAction("Đăng nhập");

                        // Đã sửa thành setDescription chuẩn theo model của bạn
                        log.setDescription("Quản trị viên đăng nhập vào hệ thống");

                        // Định dạng ngày giờ hiện tại và sửa thành setLogTime chuẩn theo model của bạn
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        log.setLogTime(sdf.format(new Date()));

                        // Lưu xuống database SQLite
                        activityLogRepository.save(log);
                    } catch (Exception e) {
                        System.err.println("Lỗi ghi nhật ký hệ thống: " + e.getMessage());
                    }
// ========================================================

                    return "redirect:/dashboard";
                } else {
                    model.addAttribute("error", "Tài khoản không có quyền quản trị!");
                }
            } else {
                model.addAttribute("error", "Sai mật khẩu!");
            }
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}