package com.dcdoctor.adminserver.controller;

import com.dcdoctor.adminserver.model.Chat;
import com.dcdoctor.adminserver.model.Post;
import com.dcdoctor.adminserver.model.SystemConfig;
import com.dcdoctor.adminserver.model.User;
import com.dcdoctor.adminserver.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.dcdoctor.adminserver.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QATransactionRepository qaTransactionRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private ChatRepository chatRepository;

    // Kiểm tra quyền Admin từ Session
    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("adminUser") != null;
    }

    // --- CHỨC NĂNG 5: DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            model.addAttribute("totalUsers", userRepository.count());

            // ĐỒNG BỘ: Tính tổng số giao dịch dựa trên bảng chat thực tế
            model.addAttribute("totalTransactions", chatRepository.count());

            long postCount = 0;
            try { postCount = postRepository.count(); } catch (Exception e) {}
            model.addAttribute("totalPosts", postCount);

            // ĐỒNG BỘ: Đếm số ca đã xử lý với status là 'READ' trong bảng chat
            long completed = 0;
            try { completed = chatRepository.countByStatus("READ"); } catch (Exception e) {}
            model.addAttribute("completedTasks", completed);

        } catch (Exception e) {
            System.err.println("Lỗi load dữ liệu Dashboard: " + e.getMessage());
        }

        return "dashboard";
    }
    // --- CHỨC NĂNG 4: QUẢN LÝ NGƯỜI DÙNG ---
    @GetMapping("/users")
    public String manageUsers(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // 1. Lấy toàn bộ không chừa một ai
        List<User> allUsers = userRepository.findAll();

        // 2. Dùng vòng lặp đơn giản để lọc (tránh lỗi null pointer của Stream)
        List<User> displayList = new ArrayList<>();
        for (User u : allUsers) {
            // Chỉ cần role không phải là ADMIN thì cho vào danh sách
            if (u.getRole() != null && !"ADMIN".equalsIgnoreCase(u.getRole())) {
                displayList.add(u);
            }
        }

        // 3. Đẩy dữ liệu ra
        model.addAttribute("users", displayList);
        model.addAttribute("totalUsers", displayList.size());

        return "users";
    }


    // --- QUẢN LÝ GIAO DỊCH ---
    @GetMapping("/transactions")
    public String viewTransactions(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // ĐỒNG BỘ: Lấy toàn bộ dữ liệu từ bảng chat thay vì qa_transactions
        List<Chat> chatList = chatRepository.findAll();
        model.addAttribute("transactions", chatList);

        return "transactions";
    }

    // --- NHẬT KÝ HỆ THỐNG ---
    @GetMapping("/logs")
    public String viewLogs(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            model.addAttribute("logs", activityLogRepository.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Bảng nhật ký chưa có dữ liệu hoặc không tồn tại.");
        }
        return "logs";
    }

    // --- CHỨC NĂNG 2: HIỂN THỊ DANH SÁCH BÀI VIẾT ---
    @GetMapping("/content")
    public String viewContent(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            // Lấy danh sách bài viết an toàn
            List<Post> posts = postRepository.findAll();
            model.addAttribute("posts", posts);

            // Đếm tổng số bài viết
            model.addAttribute("totalPosts", postRepository.count());
        } catch (Exception e) {
            System.err.println("Lỗi quét bảng media: " + e.getMessage());
            // Nếu lỗi, trả về danh sách rỗng để tránh sập giao diện Whitelabel
            model.addAttribute("posts", new ArrayList<Post>());
            model.addAttribute("totalPosts", 0);
        }

        return "content";
    }

    @PostMapping("/content/add")
    public String addPost(@ModelAttribute Post post, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        postRepository.save(post);
        return "redirect:/content"; // Điều hướng quay lại trang danh sách sau khi lưu thành công
    }

    @GetMapping("/content/delete/{id}")
    public String deletePost(@PathVariable int id, HttpSession session) { // Đổi Long sang int
        if (!isAdmin(session)) return "redirect:/login";
        postRepository.deleteById(id);
        return "redirect:/content?deleted";
    }

    // --- CHỨC NĂNG 3: CẤU HÌNH HỆ THỐNG (AI) ---
    @GetMapping("/settings")
    public String systemSettings(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // Tìm ID 1, nếu không có thì tạo mới với dữ liệu mặc định
        SystemConfig config = systemConfigRepository.findById(1)
                .orElse(new SystemConfig(1, "...", 1));

        model.addAttribute("config", config);
        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateConfig(@RequestParam String aiResponse,
                               @RequestParam int notification,
                               HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // Cập nhật hoặc tạo mới bản ghi ID 1
        SystemConfig config = systemConfigRepository.findById(1).orElse(new SystemConfig());
        config.setId(1);
        config.setAiResponse(aiResponse);
        config.setNotification(notification);

        systemConfigRepository.save(config);

        return "redirect:/settings?updated";
    }
}