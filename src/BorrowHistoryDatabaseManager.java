import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowHistoryDatabaseManager {
    private Connection connection;

    public BorrowHistoryDatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:library.db");
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS borrow_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "libraryItemId INTEGER, " +
                "borrowDate TEXT, " +
                "returnDate TEXT, " +
                "isReturned INTEGER)";
        connection.createStatement().execute(createTableSQL);
    }

    public void addBorrowHistory(BorrowHistory borrowHistory) throws SQLException {
        String insertSQL = "INSERT INTO borrow_history (userId, libraryItemId, borrowDate, returnDate, isReturned) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, borrowHistory.getUserId());
            stmt.setInt(2, borrowHistory.getLibraryItemId());
            stmt.setString(3, borrowHistory.getBorrowDate().toString());
            stmt.setString(4, borrowHistory.getReturnDate() != null ? borrowHistory.getReturnDate().toString() : null);
            stmt.setInt(5, borrowHistory.isReturned() ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public List<BorrowHistory> getAllBorrowHistories() throws SQLException {
        String querySQL = "SELECT * FROM borrow_history";
        ResultSet rs = connection.createStatement().executeQuery(querySQL);

        List<BorrowHistory> histories = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int userId = rs.getInt("userId");
            int libraryItemId = rs.getInt("libraryItemId");
            LocalDate borrowDate = LocalDate.parse(rs.getString("borrowDate"));
            LocalDate returnDate = rs.getString("returnDate") != null ? LocalDate.parse(rs.getString("returnDate")) : null;
            boolean isReturned = rs.getInt("isReturned") == 1;

            BorrowHistory history = new BorrowHistory(id, userId, libraryItemId, borrowDate, returnDate, isReturned);
            histories.add(history);
        }
        return histories;
    }

    public void updateReturnStatus(int historyId, LocalDate returnDate) throws SQLException {
        String updateSQL = "UPDATE borrow_history SET returnDate = ?, isReturned = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, returnDate.toString());
            stmt.setInt(2, historyId);
            stmt.executeUpdate();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}