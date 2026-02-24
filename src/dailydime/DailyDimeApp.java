package dailydime;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class DailyDimeApp extends JFrame {

    public static final String LOGIN = "login";
    public static final String HOME = "home";
    public static final String ADD_EXPENSE = "addExpense";
    public static final String UPDATE = "update";
    public static final String ANALYZE = "analyze";
    public static final String ACCOUNT = "account";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JPanel navPanel = new JPanel(new GridLayout(6, 1, 8, 8));

    private String currentUsername;

    private HomePanel homePanel;
    private AddExpensePanel addExpensePanel;
    private UpdatePanel updatePanel;
    private AnalyzePanel analyzePanel;
    private AccountPanel accountPanel;

    public DailyDimeApp() {
        DBConnection.initializeDatabase();
        configureFrame();
        buildUi();
    }

    private void configureFrame() {
        setTitle("Daily Dime");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);
    }

    private void buildUi() {

        navPanel.setBackground(UITheme.PRIMARY_GREEN);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));
        navPanel.setPreferredSize(new Dimension(180, 0));
        navPanel.setVisible(false);

        RoundedButton homeBtn = new RoundedButton("Home");
        RoundedButton addBtn = new RoundedButton("Add Expense");
        RoundedButton updateBtn = new RoundedButton("Update");
        RoundedButton analyzeBtn = new RoundedButton("Analyze");
        RoundedButton accountBtn = new RoundedButton("Account");
        RoundedButton logoutBtn = new RoundedButton("Logout");

        homeBtn.addActionListener(e -> navigateTo(HOME));
        addBtn.addActionListener(e -> navigateTo(ADD_EXPENSE));
        updateBtn.addActionListener(e -> navigateTo(UPDATE));
        analyzeBtn.addActionListener(e -> navigateTo(ANALYZE));
        accountBtn.addActionListener(e -> navigateTo(ACCOUNT));
        logoutBtn.addActionListener(e -> logout());

        navPanel.add(homeBtn);
        navPanel.add(addBtn);
        navPanel.add(updateBtn);
        navPanel.add(analyzeBtn);
        navPanel.add(accountBtn);
        navPanel.add(logoutBtn);

        LoginPanel loginPanel = new LoginPanel(this);
        homePanel = new HomePanel(this);
        addExpensePanel = new AddExpensePanel(this);
        updatePanel = new UpdatePanel(this);
        analyzePanel = new AnalyzePanel(this);
        accountPanel = new AccountPanel(this);

        cardPanel.setBackground(UITheme.BACKGROUND);

        cardPanel.add(loginPanel, LOGIN);
        cardPanel.add(homePanel, HOME);
        cardPanel.add(addExpensePanel, ADD_EXPENSE);
        cardPanel.add(updatePanel, UPDATE);
        cardPanel.add(analyzePanel, ANALYZE);
        cardPanel.add(accountPanel, ACCOUNT);

        add(navPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, LOGIN);
    }

    public void loginSuccess(String username) {
        this.currentUsername = username;
        navPanel.setVisible(true);
        homePanel.setUsername(username);
        accountPanel.setUsername(username);
        navigateTo(HOME);
    }

    public void navigateTo(String page) {
        if (HOME.equals(page)) {
            homePanel.setUsername(currentUsername);
        } else if (UPDATE.equals(page)) {
            updatePanel.refreshData();
        } else if (ANALYZE.equals(page)) {
            analyzePanel.refreshData();
        } else if (ACCOUNT.equals(page)) {
            accountPanel.setUsername(currentUsername);
        }
        cardLayout.show(cardPanel, page);
    }

    private void logout() {
        this.currentUsername = null;
        navPanel.setVisible(false);
        cardLayout.show(cardPanel, LOGIN);
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public static void main(String[] args) {

        
        FontUIResource bodyFont = new FontUIResource(UITheme.BODY_FONT);
        FontUIResource headingFont = new FontUIResource(UITheme.HEADING_FONT);

        UIManager.put("Label.font", bodyFont);
        UIManager.put("Button.font", bodyFont);
        UIManager.put("Table.font", bodyFont);
        UIManager.put("TableHeader.font", headingFont);
        UIManager.put("ComboBox.font", bodyFont);
        UIManager.put("TextField.font", bodyFont);
        UIManager.put("PasswordField.font", bodyFont);

        SwingUtilities.invokeLater(() -> new DailyDimeApp().setVisible(true));
    }
}