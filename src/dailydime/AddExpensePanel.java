package dailydime;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AddExpensePanel extends JPanel {

    private DailyDimeApp app;  // ← reference to main app

    private final RoundedTextField dateField = new RoundedTextField("Date", 14);
    private final JComboBox<String> categoryBox =
            new JComboBox<>(new String[]{"Food", "Travel", "Bills", "Other"});
    private final RoundedTextField amountField = new RoundedTextField("Amount", 14);

    public AddExpensePanel(DailyDimeApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        centerWrap.setOpaque(false);

        RoundedPanel formCard = new RoundedPanel(26, UITheme.CARD_BACKGROUND);
        formCard.setLayout(new GridLayout(0, 1, 10, 12));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JLabel title = new JLabel("Add Expense", JLabel.CENTER);
        title.setFont(UITheme.HEADING_FONT);
        title.setForeground(UITheme.TEXT_DARK);

        categoryBox.setFont(UITheme.BODY_FONT);
        categoryBox.setBackground(java.awt.Color.WHITE);

        RoundedButton saveBtn = new RoundedButton("Save Expense");
        saveBtn.addActionListener(e -> saveExpense());

        formCard.add(title);
        formCard.add(dateField);
        formCard.add(categoryBox);
        formCard.add(amountField);
        formCard.add(saveBtn);

        centerWrap.add(formCard);
        add(centerWrap, BorderLayout.CENTER);

        fillCurrentDate();
    }

    private void fillCurrentDate() {
        dateField.setForeground(UITheme.TEXT_DARK);
        dateField.setText(LocalDate.now().toString());
    }

    private void saveExpense() {
        String date = dateField.getText().trim();
        String category = String.valueOf(categoryBox.getSelectedItem());
        String amountText = amountField.getActualText();

        if (date.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Date and amount are required.",
                    "Missing Data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Amount must be numeric.",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO expenses(username, date, category, amount) VALUES(?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, app.getCurrentUsername());  // ← logged-in user
            stmt.setString(2, date);
            stmt.setString(3, category);
            stmt.setDouble(4, amount);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Expense saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            fillCurrentDate();
            categoryBox.setSelectedIndex(0);
            amountField.clear();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}