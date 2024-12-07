import java.sql.*;
import java.util.logging.Logger;

public class UserDatabaseManager {
    private static final Logger logger = Logger.getLogger(UserDatabaseManager.class.getName());
    private Connection connection;

    public UserDatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:library.db");
            initializeDatabase();
            updateDatabaseSchema();
        } catch (SQLException e) {
            logger.severe("Failed to connect to the database: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'USER', 'FACULTY_MEMBER', 'STUDENT_MEMBER', 'GUEST_MEMBER')), " +
                "readerId TEXT, " +
                "email TEXT, " +
                "createdAt TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            logger.severe("Failed to initialize the users table: " + e.getMessage());
        }
    }

    private void updateDatabaseSchema() {
        try (Statement stmt = connection.createStatement()) {
            // Check if the columns already exist
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users);");
            boolean readerIdExists = false;
            boolean emailExists = false;
            boolean createdAtExists = false;
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("readerId".equalsIgnoreCase(columnName)) {
                    readerIdExists = true;
                } else if ("email".equalsIgnoreCase(columnName)) {
                    emailExists = true;
                } else if ("createdAt".equalsIgnoreCase(columnName)) {
                    createdAtExists = true;
                }
            }
            if (!readerIdExists) {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN readerId TEXT;");
            }
            if (!emailExists) {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN email TEXT;");
            }
            if (!createdAtExists) {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN createdAt TEXT;");
            }
        } catch (SQLException e) {
            logger.severe("Failed to update the users table schema: " + e.getMessage());
        }
    }

    public void addUser(User user) {
        String checkUserSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSQL = "INSERT INTO users (username, password, role, readerId, email, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserSQL)) {
            checkStmt.setString(1, user.getUsername());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                logger.severe("Username already exists: " + user.getUsername());
                return; // Or handle it in a way that suits your application
            }
        } catch (SQLException e) {
            logger.severe("Failed to check username: " + e.getMessage());
            return;
        }

        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            stmt.setString(4, user.getReaderId());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getCreatedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed to add user: " + user.getUsername() + ", " + e.getMessage());
        }
    }

    public User getUserById(int userId) {
        String querySQL = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                String readerId = rs.getString("readerId");
                String email = rs.getString("email");
                String createdAt = rs.getString("createdAt");

                return new User(id, username, password, role, readerId, email, createdAt) {
                    @Override
                    public void borrowItem(LibraryItem item) {
                        // Implementation here
                    }

                    @Override
                    public void returnItem(LibraryItem item) {
                        // Implementation here
                    }
                };
            }
        } catch (SQLException e) {
            logger.severe("Failed to get user by ID: " + e.getMessage());
        }
        return null;
    }

    public void updateUser(User user) {
        String updateSQL = "UPDATE users SET password = ?, role = ?, readerId = ?, email = ?, createdAt = ? WHERE id = ?";
        try {
            connection.setAutoCommit(false); // Start transaction
            try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
                stmt.setString(1, user.getPassword());
                stmt.setString(2, user.getRole().toString());
                stmt.setString(3, user.getReaderId());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getCreatedAt());
                stmt.setInt(6, user.getId());
                stmt.executeUpdate();
                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction on error
                logger.severe("Failed to update user: " + e.getMessage() + " SQLState: " + e.getSQLState() + " ErrorCode: " + e.getErrorCode());
            } finally {
                connection.setAutoCommit(true); // Restore default auto-commit behavior
            }
        } catch (SQLException e) {
            logger.severe("Failed to manage transaction: " + e.getMessage());
        }
    }

    public void deleteUser(int userId) {
        String deleteSQL = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed to delete user: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.severe("Failed to close the database connection: " + e.getMessage());
            }
        }
    }
}