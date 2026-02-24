package dailydime;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;

public class RoundedPasswordField extends JPasswordField {
    private final int arc;
    private final String placeholder;
    private boolean showingPlaceholder;

    public RoundedPasswordField(String placeholder, int columns) {
        super(columns);
        this.arc = 18;
        this.placeholder = placeholder;
        this.showingPlaceholder = true;

        setOpaque(false);
        setMargin(new Insets(8, 12, 8, 12));
        setFont(UITheme.BODY_FONT);
        setForeground(Color.GRAY);
        setEchoChar((char) 0);
        setText(placeholder);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(UITheme.TEXT_DARK);
                    setEchoChar('•');
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(getPassword()).trim().isEmpty()) {
                    showPlaceholder();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITheme.DARK_GREEN);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
    }

    public String getActualText() {
        return showingPlaceholder ? "" : String.valueOf(getPassword()).trim();
    }

    public void clear() {
        showPlaceholder();
    }

    private void showPlaceholder() {
        setForeground(Color.GRAY);
        setEchoChar((char) 0);
        setText(placeholder);
        showingPlaceholder = true;
    }
}