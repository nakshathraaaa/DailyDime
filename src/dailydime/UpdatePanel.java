package dailydime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class UpdatePanel extends JPanel {

    private DailyDimeApp app;

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID", "Date", "Category", "Amount"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);
    private final RoundedTextField dateField = new RoundedTextField("Date", 10);
    private final JComboBox<String> categoryBox =
            new JComboBox<>(new String[]{"Food", "Travel", "Bills", "Other"});
    private final RoundedTextField amountField =
            new RoundedTextField("Amount", 10);

    public UpdatePanel(DailyDimeApp app) {
        this.app = app;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== Title =====
        JLabel title = new JLabel("Update / Delete Expenses");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.DARK_GREEN);
        add(title, BorderLayout.NORTH);

        // ===== Table Styling =====
        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(UITheme.LIGHT_GREEN);
        table.setSelectionForeground(UITheme.TEXT_DARK);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());

        table.getTableHeader().setFont(UITheme.HEADING_FONT);
        table.getTableHeader().setBackground(UITheme.PRIMARY_GREEN);
        table.getTableHeader().setForeground(java.awt.Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        // ===== Editor Section =====
        JPanel editor = new JPanel(new GridLayout(2, 3, 12, 12));
        editor.setOpaque(false);

        categoryBox.setFont(UITheme.BODY_FONT);
        categoryBox.setBackground(java.awt.Color.WHITE);

        editor.add(dateField);
        editor.add(categoryBox);
        editor.add(amountField);

        RoundedButton updateBtn = new RoundedButton("Update");
        RoundedButton deleteBtn = new RoundedButton("Delete");
        RoundedButton refreshBtn = new RoundedButton("Refresh");

        updateBtn.addActionListener(e -> updateExpense());
        deleteBtn.addActionListener(e -> deleteExpense());
        refreshBtn.addActionListener(e -> refreshData());

        editor.add(updateBtn);
        editor.add(deleteBtn);
        editor.add(refreshBtn);

        add(editor, BorderLayout.SOUTH);
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            dateField.setForeground(UITheme.TEXT_DARK);
            dateField.setText(String.valueOf(model.getValueAt(row, 1)));
            categoryBox.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
            amountField.setForeground(UITheme.TEXT_DARK);
            amountField.setText(String.valueOf(model.getValueAt(row, 3)));
        }
    }

    public void refreshData() {
        model.setRowCount(0);

        String sql =
                "SELECT id, date, category, amount FROM expenses " +
                "WHERE username = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, app.getCurrentUsername());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("category"),
                        String.format("₹%.2f", rs.getDouble("amount"))
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load expenses: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpense() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an expense to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String date = dateField.getText().trim();
        String category = String.valueOf(categoryBox.getSelectedItem());
        String amountText = amountField.getText().trim();

        double amount;
        try {
            amount = Double.parseDouble(amountText.replace("₹", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Amount must be numeric.",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql =
                "UPDATE expenses SET date = ?, category = ?, amount = ? " +
                "WHERE id = ? AND username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);
            stmt.setString(2, category);
            stmt.setDouble(3, amount);
            stmt.setInt(4, id);
            stmt.setString(5, app.getCurrentUsername());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Expense updated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Update failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an expense to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected expense?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) model.getValueAt(row, 0);

        String sql =
                "DELETE FROM expenses WHERE id = ? AND username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, app.getCurrentUsername());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Expense deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Delete failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}