import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LibraryService {
    private static final Logger logger = Logger.getLogger(LibraryService.class.getName());
    private DatabaseManager dbManager;

    public LibraryService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public List<LibraryItem> getFilteredItems(String searchText, String type) {
        try {
            return dbManager.getAllItems().stream()
                    .filter(item -> (searchText.isEmpty() || item.getTitle().toLowerCase().contains(searchText.toLowerCase())))
                    .filter(item -> (type.equals("All") || item.getType().equals(type)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void borrowItem(int itemId, int userId) {
        try {
            LibraryItem item = dbManager.getAllItems().stream()
                    .filter(i -> i.getId() == itemId)
                    .findFirst()
                    .orElse(null);
            if (item != null && item.isAvailable()) {
                item.borrowItem();
                LocalDate dueDate = LocalDate.now().plusDays(14);
                item.setDueDate(dueDate);
                dbManager.updateItem(item);

                BorrowHistory history = new BorrowHistory(0, userId, itemId, LocalDate.now(), null, false);
                BorrowHistoryDatabaseManager historyDbManager = new BorrowHistoryDatabaseManager();
                historyDbManager.addBorrowHistory(history);
                historyDbManager.closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnItem(int itemId) {
        try {
            LibraryItem item = dbManager.getAllItems().stream()
                    .filter(i -> i.getId() == itemId)
                    .findFirst()
                    .orElse(null);
            if (item != null && !item.isAvailable()) {
                item.returnItem();
                item.setDueDate(null);
                dbManager.updateItem(item);

                BorrowHistoryDatabaseManager historyDbManager = new BorrowHistoryDatabaseManager();
                List<BorrowHistory> histories = historyDbManager.getAllBorrowHistories();
                for (BorrowHistory history : histories) {
                    if (history.getLibraryItemId() == itemId && !history.isReturned()) {
                        historyDbManager.updateReturnStatus(history.getId(), LocalDate.now());
                        break;
                    }
                }
                historyDbManager.closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLibraryItem(LibraryItem newItem) {
        try {
            dbManager.addItem(newItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLibraryItem(int itemId) {
        try {
            dbManager.deleteItem(itemId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) {
        String querySQL = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String dbUsername = rs.getString("username");
                String dbPassword = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                String readerId = rs.getString("readerId");
                String email = rs.getString("email");
                String createdAt = rs.getString("createdAt");

                return new User(id, dbUsername, dbPassword, role, readerId, email, createdAt) {
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
            logger.severe("Failed to authenticate user: " + e.getMessage());
        }
        return null;
    }
}