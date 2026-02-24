package dailydime;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LoginPanel extends JPanel {
    private final DailyDimeApp app;
    private final RoundedTextField usernameField = new RoundedTextField("Username", 18);
    private final RoundedPasswordField passwordField = new RoundedPasswordField("Password", 18);

    public LoginPanel(DailyDimeApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 110));
        centerWrap.setOpaque(false);

        RoundedPanel loginCard = new RoundedPanel(28, UITheme.CARD_BACKGROUND);
        loginCard.setLayout(new GridLayout(0, 1, 10, 12));
        loginCard.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        JLabel title = new JLabel("Daily Dime Login", JLabel.CENTER);
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_DARK);

        RoundedButton loginBtn = new RoundedButton("Login");
        loginBtn.addActionListener(e -> handleLogin());

        loginCard.add(title);
        loginCard.add(new JLabel());
        loginCard.add(usernameField);
        loginCard.add(passwordField);
        loginCard.add(loginBtn);
        loginCard.setSize(360, 280);

        centerWrap.add(loginCard);
        add(centerWrap, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = usernameField.getActualText();
        String password = passwordField.getActualText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    app.loginSuccess(username);
                    usernameField.clear();
                    passwordField.clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}