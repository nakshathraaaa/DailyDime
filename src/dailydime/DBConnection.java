package dailydime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:dailydime.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users(" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL" +
                ")";

       String expensesTable = "CREATE TABLE IF NOT EXISTS expenses(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT, " + 
                            "date TEXT, " +
                            "category TEXT, " +
                            "amount REAL" +
                            ")";

        String defaultAdmin = 
"INSERT OR IGNORE INTO users(username, password) VALUES('admin', '1234')";

String user1 = 
"INSERT OR IGNORE INTO users(username, password) VALUES('Nakshathra', 'nash1234')";

String user2 = 
"INSERT OR IGNORE INTO users(username, password) VALUES('Nithin', 'nithin1234')";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(expensesTable);
            stmt.execute(defaultAdmin);
            stmt.execute(user1);
            stmt.execute(user2);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}