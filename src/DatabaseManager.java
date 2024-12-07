import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            // Register the JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish the connection
            String url = "jdbc:sqlite:library.db";
            this.connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void ensureConnectionOpen() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:library.db";
            this.connection = DriverManager.getConnection(url);
        }
    }

    private void initializeDatabase() throws SQLException {
        ensureConnectionOpen();
        String createLibraryTableSQL = "CREATE TABLE IF NOT EXISTS library_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "type TEXT, " +
                "isAvailable INTEGER, " +
                "dueDate TEXT)";
        connection.createStatement().execute(createLibraryTableSQL);

        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT)";
        connection.createStatement().execute(createUserTableSQL);
    }

    public void addItem(LibraryItem item) throws SQLException {
        ensureConnectionOpen();
        String insertSQL = "INSERT INTO library_items (title, type, isAvailable, dueDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getType());
            stmt.setInt(3, item.isAvailable() ? 1 : 0);
            stmt.setString(4, item.getDueDate().toString());
            stmt.executeUpdate();
        }
    }

    public List<LibraryItem> getAllItems() throws SQLException {
        ensureConnectionOpen();
        String querySQL = "SELECT * FROM library_items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            List<LibraryItem> items = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String type = rs.getString("type");
                boolean isAvailable = rs.getInt("isAvailable") == 1;
                String dueDateStr = rs.getString("dueDate");
                LocalDate dueDate = LocalDate.parse(dueDateStr);

                LibraryItem item = createLibraryItem(type, id, title, isAvailable, dueDate);
                items.add(item);
            }
            return items;
        }
    }

    private LibraryItem createLibraryItem(String type, int id, String title, boolean isAvailable, LocalDate dueDate) {
        LibraryItem item;
        if ("Book".equals(type)) {
            item = new Book(id, title);
        } else if ("Magazine".equals(type)) {
            item = new Magazine(id, title);
        } else {
            throw new IllegalArgumentException("Unknown item type: " + type);
        }
        item.setAvailable(isAvailable);
        item.setDueDate(dueDate);
        return item;
    }

    public void updateItem(LibraryItem item) throws SQLException {
        ensureConnectionOpen();
        String updateSQL = "UPDATE library_items SET isAvailable = ?, dueDate = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setInt(1, item.isAvailable() ? 1 : 0);
            stmt.setString(2, item.getDueDate().toString());
            stmt.setInt(3, item.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteItem(int itemId) throws SQLException {
        ensureConnectionOpen();
        String deleteSQL = "DELETE FROM library_items WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        }
    }

    private void createSuperAdminIfNotExist() throws SQLException {
        ensureConnectionOpen();
        String superAdminUsername = "super";
        String superAdminPassword = "super";

        String checkSuperAdminSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkSuperAdminSQL)) {
            stmt.setString(1, superAdminUsername);
            ResultSet rs = stmt.executeQuery();
            if (rs.getInt(1) == 0) {
                String insertSQL = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, superAdminUsername);
                    insertStmt.setString(2, superAdminPassword);
                    insertStmt.setString(3, "SUPER_ADMIN");
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public boolean authenticate(String username, String password) throws SQLException {
        ensureConnectionOpen();
        String querySQL = "SELECT password, role FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                System.out.println("Authenticated as: " + role);
                return true;
            }
        }
        return false;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}