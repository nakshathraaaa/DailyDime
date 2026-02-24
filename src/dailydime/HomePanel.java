package dailydime;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomePanel extends JPanel {

    private final JLabel userLabel = new JLabel("Logged in as: -", JLabel.CENTER);

    public HomePanel(DailyDimeApp app) {

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        RoundedPanel content = new RoundedPanel(30, UITheme.CARD_BACKGROUND);
        content.setLayout(new GridLayout(0, 1, 15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        // ₹ Logo
        JLabel rupeeLabel = new JLabel("₹", JLabel.CENTER);
        rupeeLabel.setFont(new Font("SansSerif", Font.BOLD, 72));
        rupeeLabel.setForeground(UITheme.PRIMARY_GREEN);

        // App Name
        JLabel titleLabel = new JLabel("Daily Dime", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        titleLabel.setForeground(UITheme.TEXT_DARK);

        
        JLabel captionLabel = new JLabel(
                "<html><div style='text-align:center;'>"
                + "Track smarter. Spend wiser.<br>"
                + "Take control of your money."
                + "</div></html>",
                JLabel.CENTER
        );
        captionLabel.setFont(UITheme.BODY_FONT);
        captionLabel.setForeground(UITheme.TEXT_DARK);

        userLabel.setFont(UITheme.HEADING_FONT);
        userLabel.setForeground(UITheme.PRIMARY_GREEN);

        content.add(rupeeLabel);
        content.add(titleLabel);
        content.add(captionLabel);
        content.add(userLabel);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        wrap.add(content, BorderLayout.CENTER);

        add(wrap, BorderLayout.CENTER);
    }

    public void setUsername(String username) {
        userLabel.setText("Logged in as: " + (username == null ? "-" : username));
    }
}