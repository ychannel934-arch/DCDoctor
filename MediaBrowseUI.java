package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MediaBrowseUI extends JPanel {

    // ── Màu sắc ───────────────────────────────────────────
    private static final Color BG       = new Color(0xF8FAFF);
    private static final Color WHITE    = Color.WHITE;
    private static final Color PRIMARY  = new Color(0x3B82F6);
    private static final Color TEXT     = new Color(0x1E293B);
    private static final Color MUTED    = new Color(0x64748B);
    private static final Color BORDER_C = new Color(0xE2E8F0);
    private static final Color WARN     = new Color(0xF59E0B);
    private static final Color SUCCESS  = new Color(0x10B981);
    private static final Color PURPLE   = new Color(0x8B5CF6);
    private static final Color DANGER   = new Color(0xEF4444);

    private JTextField txtSearch;
    private JComboBox<String> cboCategory;
    private JPanel articleList;
    private JScrollPane listScroll;

    // Demo data khi chưa có DB
    private static final Object[][] DEMO = {
            {1, "Chế độ ăn cho người tiểu đường type 2",   "Dinh dưỡng",
                    "Người bệnh tiểu đường type 2 nên ăn nhiều rau xanh, ngũ cốc nguyên hạt..."},
            {2, "Tập thể dục đúng cách cho bệnh nhân tiểu đường", "Vận động",
                    "Tập thể dục 30 phút mỗi ngày giúp kiểm soát đường huyết hiệu quả..."},
            {3, "Insulin — Những điều cần biết",            "Thuốc",
                    "Insulin là hormone quan trọng giúp tế bào hấp thụ glucose từ máu..."},
            {4, "Biến chứng tiểu đường và cách phòng ngừa", "Sức khỏe",
                    "Biến chứng thường gặp gồm bệnh tim mạch, thần kinh, thận và mắt..."},
            {5, "Theo dõi đường huyết tại nhà",              "Kỹ năng",
                    "Hướng dẫn sử dụng máy đo đường huyết và ghi chép chỉ số hàng ngày..."},
    };

    public MediaBrowseUI() {
        setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildContent(),   BorderLayout.CENTER);

        loadArticles("", "Tất cả");
    }

    // ══════════════════════════════════════════
    // TOP BAR
    // ══════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(WHITE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel title = new JLabel("📰  Bài viết sức khỏe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);

        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ══════════════════════════════════════════
    // CONTENT
    // ══════════════════════════════════════════
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);

        content.add(buildSearchBar(),  BorderLayout.NORTH);
        content.add(buildCategories(), BorderLayout.CENTER);

        // Article list
        articleList = new JPanel();
        articleList.setBackground(BG);
        articleList.setLayout(new BoxLayout(articleList, BoxLayout.Y_AXIS));
        articleList.setBorder(new EmptyBorder(8, 14, 14, 14));

        listScroll = new JScrollPane(articleList);
        listScroll.setBorder(null);
        listScroll.getViewport().setBackground(BG);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.getVerticalScrollBar().setUnitIncrement(12);

        content.add(listScroll, BorderLayout.SOUTH);

        // Đặt lại layout để scroll chiếm hết phần còn lại
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(buildSearchBar(),  BorderLayout.NORTH);
        wrapper.add(buildCategoryRow(), BorderLayout.CENTER);
        wrapper.add(listScroll,        BorderLayout.SOUTH);

        // Dùng BorderLayout đúng cách
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.add(buildSearchBar(),   BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setBackground(BG);
        middle.add(buildCategoryRow(), BorderLayout.NORTH);
        middle.add(listScroll,         BorderLayout.CENTER);

        outer.add(middle, BorderLayout.CENTER);
        return outer;
    }

    // ══════════════════════════════════════════
    // SEARCH BAR
    // ══════════════════════════════════════════
    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(BG);
        bar.setBorder(new EmptyBorder(12, 14, 8, 14));

        // Search field
        JPanel searchWrap = new JPanel(new BorderLayout(6, 0));
        searchWrap.setBackground(WHITE);
        searchWrap.setBorder(new CompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        txtSearch = new JTextField();
        txtSearch.setBackground(WHITE);
        txtSearch.setForeground(TEXT);
        txtSearch.setCaretColor(PRIMARY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(null);
        txtSearch.setToolTipText("Tìm kiếm bài viết...");

        // Placeholder
        txtSearch.setText("Tìm kiếm bài viết...");
        txtSearch.setForeground(MUTED);
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm bài viết...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm bài viết...");
                    txtSearch.setForeground(MUTED);
                }
            }
        });

        txtSearch.addActionListener(e -> doSearch());

        searchWrap.add(searchIcon, BorderLayout.WEST);
        searchWrap.add(txtSearch,  BorderLayout.CENTER);

        // Nút tìm
        JButton btnSearch = makeBtn("Tìm", PRIMARY, WHITE);
        btnSearch.addActionListener(e -> doSearch());

        bar.add(searchWrap, BorderLayout.CENTER);
        bar.add(btnSearch,  BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════
    // CATEGORY CHIPS
    // ══════════════════════════════════════════
    private JPanel buildCategories() { return new JPanel(); } // placeholder

    private JPanel buildCategoryRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(BG);
        row.setBorder(new EmptyBorder(0, 14, 10, 14));

        String[] cats = {"Tất cả", "Dinh dưỡng", "Vận động", "Thuốc", "Sức khỏe", "Kỹ năng"};
        Color[]  colors = {PRIMARY, SUCCESS, new Color(0xF97316), PURPLE, DANGER, new Color(0x14B8A6)};

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < cats.length; i++) {
            final String cat  = cats[i];
            final Color color = colors[i];
            JToggleButton chip = makeChip(cat, color, i == 0);
            chip.addActionListener(e -> {
                String kw = txtSearch.getText().equals("Tìm kiếm bài viết...") ? "" : txtSearch.getText();
                loadArticles(kw, cat);
            });
            group.add(chip);
            row.add(chip);
        }
        return row;
    }

    private JToggleButton makeChip(String label, Color color, boolean selected) {
        JToggleButton chip = new JToggleButton(label, selected) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(color);
                } else {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chip.setForeground(selected ? WHITE : color.darker());
        chip.setBorderPainted(false);
        chip.setFocusPainted(false);
        chip.setContentAreaFilled(false);
        chip.setOpaque(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setBorder(new EmptyBorder(6, 14, 6, 14));

        chip.addChangeListener(e ->
                chip.setForeground(chip.isSelected() ? WHITE : color.darker())
        );
        return chip;
    }

    // ══════════════════════════════════════════
    // LOAD ARTICLES
    // ══════════════════════════════════════════
    private void doSearch() {
        String kw = txtSearch.getText().equals("Tìm kiếm bài viết...") ? "" : txtSearch.getText();
        loadArticles(kw, "Tất cả");
    }

    private void loadArticles(String keyword, String category) {
        articleList.removeAll();

        List<Object[]> results = new ArrayList<>();

        try (Connection conn = DBConnection.connect()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM media WHERE title LIKE ?");
            if (!category.equals("Tất cả")) sql.append(" AND category = ?");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(1, "%" + keyword + "%");
            if (!category.equals("Tất cả")) ps.setString(2, category);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("content")
                });
            }
        } catch (Exception e) {
            // Dùng demo data
            for (Object[] row : DEMO) {
                String title = row[1].toString().toLowerCase();
                String cat   = row[2].toString();
                boolean matchKw  = keyword.isEmpty() || title.contains(keyword.toLowerCase());
                boolean matchCat = category.equals("Tất cả") || cat.equals(category);
                if (matchKw && matchCat) results.add(row);
            }
        }

        if (results.isEmpty()) {
            JLabel empty = new JLabel("Không tìm thấy bài viết nào 😔", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            empty.setForeground(MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(32, 0, 0, 0));
            articleList.add(empty);
        } else {
            for (Object[] row : results) {
                articleList.add(makeArticleCard(
                        (int) row[0],
                        row[1].toString(),
                        row[2].toString(),
                        row[3].toString()
                ));
                articleList.add(Box.createVerticalStrut(10));
            }
        }

        articleList.revalidate();
        articleList.repaint();
    }

    // ══════════════════════════════════════════
    // ARTICLE CARD
    // ══════════════════════════════════════════
    private JPanel makeArticleCard(int id, String title, String category, String content) {
        Color catColor = getCategoryColor(category);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Left accent bar
                g2.setColor(catColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(14, 18, 14, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Top row — title + category badge
        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT);

        JLabel catBadge = new JLabel(category);
        catBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        catBadge.setForeground(catColor);
        catBadge.setOpaque(true);
        catBadge.setBackground(new Color(catColor.getRed(), catColor.getGreen(), catColor.getBlue(), 20));
        catBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        topRow.add(titleLbl,  BorderLayout.CENTER);
        topRow.add(catBadge,  BorderLayout.EAST);

        // Preview content
        String preview = content.length() > 80 ? content.substring(0, 80) + "..." : content;
        JLabel previewLbl = new JLabel("<html>" + preview + "</html>");
        previewLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        previewLbl.setForeground(MUTED);

        // Bottom — read more
        JLabel readMore = new JLabel("Đọc thêm →");
        readMore.setFont(new Font("Segoe UI", Font.BOLD, 11));
        readMore.setForeground(PRIMARY);

        card.add(topRow,     BorderLayout.NORTH);
        card.add(previewLbl, BorderLayout.CENTER);
        card.add(readMore,   BorderLayout.SOUTH);

        // Hover + click
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 80), 1, true),
                        new EmptyBorder(13, 17, 13, 13)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(14, 18, 14, 14));
            }
            @Override public void mouseClicked(MouseEvent e) {
                openDetail(id, title, category, content);
            }
        });

        return card;
    }

    private Color getCategoryColor(String cat) {
        return switch (cat) {
            case "Dinh dưỡng" -> SUCCESS;
            case "Vận động"   -> new Color(0xF97316);
            case "Thuốc"      -> PURPLE;
            case "Sức khỏe"   -> DANGER;
            case "Kỹ năng"    -> new Color(0x14B8A6);
            default           -> PRIMARY;
        };
    }

    private void openDetail(int id, String title, String category, String content) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                title, true);
        dialog.setSize(370, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detail = new MediaDetailUI(title, category, content);
        dialog.add(detail, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ══════════════════════════════════════════
    // HELPER
    // ══════════════════════════════════════════
    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }
}