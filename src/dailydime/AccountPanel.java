package dailydime;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AccountPanel extends JPanel {
    private final DailyDimeApp app;
    private final JLabel usernameLabel = new JLabel("Username: -", JLabel.CENTER);
    private final RoundedPasswordField newPasswordField = new RoundedPasswordField("New Password", 14);

    public AccountPanel(DailyDimeApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        centerWrap.setOpaque(false);

        RoundedPanel card = new RoundedPanel(26, UITheme.CARD_BACKGROUND);
        card.setLayout(new GridLayout(0, 1, 10, 12));
        card.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JLabel title = new JLabel("Account", JLabel.CENTER);
        title.setFont(UITheme.HEADING_FONT);
        title.setForeground(UITheme.TEXT_DARK);

        usernameLabel.setFont(UITheme.BODY_FONT);
        usernameLabel.setForeground(UITheme.TEXT_DARK);

        RoundedButton changeBtn = new RoundedButton("Change Password");
        changeBtn.addActionListener(e -> changePassword());

        card.add(title);
        card.add(usernameLabel);
        card.add(newPasswordField);
        card.add(changeBtn);

        centerWrap.add(card);
        add(centerWrap, BorderLayout.CENTER);
    }

    public void setUsername(String username) {
        usernameLabel.setText("Username: " + (username == null ? "-" : username));
    }

    private void changePassword() {
        String username = app.getCurrentUsername();
        String newPassword = newPasswordField.getActualText();

        if (username == null) {
            JOptionPane.showMessageDialog(this, "No active user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            newPasswordField.clear();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}