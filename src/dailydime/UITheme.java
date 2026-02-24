package dailydime;

import java.awt.Color;
import java.awt.Font;

public final class UITheme {

    //  Premium Green Palette
    public static final Color DARK_GREEN = new Color(18, 52, 40);
    public static final Color PRIMARY_GREEN = new Color(28, 94, 63);
    public static final Color LIGHT_GREEN = new Color(210, 235, 220);

    // Clean Backgrounds
    public static final Color BACKGROUND = new Color(244, 248, 246);
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);

    //  Text
    public static final Color TEXT_DARK = new Color(33, 33, 33);
    public static final Color TEXT_MUTED = new Color(90, 90, 90);

    // Typography System
    public static final Font LOGO_FONT = new Font("Georgia", Font.BOLD, 32);
    public static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 22);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    private UITheme() {}
}