package dailydime;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AnalyzePanel extends JPanel {

    private DailyDimeApp app;

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID", "Date", "Category", "Amount"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);
    private final JLabel totalLabel = new JLabel("Total Expenses: ₹0.00");
    private final ChartPanel chartPanel = new ChartPanel();

    public AnalyzePanel(DailyDimeApp app) {

        this.app = app;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TITLE =====
        JLabel title = new JLabel("Expense Analysis");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // ===== TABLE STYLING (Same as Update Page) =====
        table.setRowHeight(32);
        table.setFont(UITheme.BODY_FONT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(UITheme.CARD_BACKGROUND);

        table.getTableHeader().setFont(UITheme.HEADING_FONT);
        table.getTableHeader().setBackground(UITheme.PRIMARY_GREEN);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        // ===== CENTER SPLIT (Table + Chart) =====
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);

        chartPanel.setPreferredSize(new Dimension(300, 0));

        centerPanel.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(chartPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTOM TOTAL =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        totalLabel.setFont(UITheme.HEADING_FONT);
        totalLabel.setForeground(UITheme.TEXT_DARK);

        bottomPanel.add(totalLabel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {

        model.setRowCount(0);

        Map<String, Double> categoryTotals = new LinkedHashMap<>();
        categoryTotals.put("Food", 0.0);
        categoryTotals.put("Travel", 0.0);
        categoryTotals.put("Bills", 0.0);
        categoryTotals.put("Other", 0.0);

        double total = 0;

        String sql =
                "SELECT id, date, category, amount FROM expenses " +
                "WHERE username = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, app.getCurrentUsername());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id");
                String date = rs.getString("date");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");

                model.addRow(new Object[]{id, date, category, amount});

                total += amount;
                categoryTotals.put(category,
                        categoryTotals.getOrDefault(category, 0.0) + amount);
            }

            totalLabel.setText(String.format("Total Expenses: ₹%.2f", total));
            chartPanel.setCategoryTotals(categoryTotals);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to analyze expenses: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CHART PANEL =================

    private static class ChartPanel extends JPanel {

        private final Map<String, Double> categoryTotals = new LinkedHashMap<>();

        public ChartPanel() {
            setBackground(UITheme.BACKGROUND);
        }

        public void setCategoryTotals(Map<String, Double> totals) {
            categoryTotals.clear();
            categoryTotals.putAll(totals);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (categoryTotals.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int margin = 40;

            int barWidth =
                    (width - margin * 2) /
                            Math.max(1, categoryTotals.size());

            double max = 1;

            for (double value : categoryTotals.values()) {
                max = Math.max(max, value);
            }

            int x = margin;
            int baseline = height - margin;

            g2.setColor(UITheme.TEXT_DARK);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(margin, baseline, width - margin, baseline);

            Color[] colors = {
                    new Color(135, 188, 161),
                    new Color(242, 191, 120),
                    new Color(157, 178, 214),
                    new Color(224, 154, 150)
            };

            int i = 0;

            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {

                int barHeight =
                        (int) ((entry.getValue() / max) *
                                (height - margin * 2));

                int y = baseline - barHeight;

                g2.setColor(colors[i % colors.length]);
                g2.fillRoundRect(x + 8, y,
                        barWidth - 16, barHeight,
                        14, 14);

                g2.setColor(UITheme.TEXT_DARK);
                g2.drawString(entry.getKey(), x + 10, baseline + 18);
                g2.drawString(
                        String.format("₹%.0f", entry.getValue()),
                        x + 10,
                        y - 8
                );

                x += barWidth;
                i++;
            }

            g2.dispose();
        }
    }
}