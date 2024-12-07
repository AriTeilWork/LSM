import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class DatabaseInitializer {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:library.db"; // Specify the path to your database file

        // Delete the old database if it exists
        File dbFile = new File("library.db");
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            if (deleted) {
                System.out.println("Old database deleted.");
            } else {
                System.out.println("Failed to delete old database.");
            }
        }

        // SQL script for creating the new database
        String sqlScript = """
    -- Drop the users table if it exists
    DROP TABLE IF EXISTS users;
    DROP TABLE IF EXISTS library_items;

        CREATE TABLE IF NOT EXISTS library_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            type TEXT CHECK(type IN ('Book', 'Magazine')),
            isAvailable INTEGER NOT NULL CHECK(isAvailable IN (0, 1)),
            dueToDate TEXT NOT NULL
        );

        -- Create table for users
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'USER', 'FACULTY_MEMBER', 'STUDENT_MEMBER', 'GUEST_MEMBER')),
            readerId TEXT,
            email TEXT,
            createdAt TEXT
            
        );

        -- Create indexes
        CREATE INDEX IF NOT EXISTS idx_library_items_title ON library_items (title);
        CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);

        -- Insert test data into library items
        INSERT INTO library_items (title, type, isAvailable, dueToDate) VALUES
            ('The Great Gatsby', 'Book', 1, '2024-12-15'),
            ('National Geographic', 'Magazine', 1, '2024-12-20'),
            ('1984', 'Book', 0, '2024-11-30'),
            ('Time Magazine', 'Magazine', 1, '2025-01-01'),
            ('To Kill a Mockingbird', 'Book', 1, '2024-12-10'),
            ('Forbes', 'Magazine', 1, '2025-02-01'),
            ('Brave New World', 'Book', 1, '2024-12-25'),
            ('Reader''s Digest', 'Magazine', 0, '2024-11-20'),
            ('Moby Dick', 'Book', 1, '2025-01-15'),
            ('Cosmopolitan', 'Magazine', 1, '2025-03-01'),
            ('War and Peace', 'Book', 1, '2024-12-12'),
            ('Vogue', 'Magazine', 1, '2025-03-15'),
            ('Pride and Prejudice', 'Book', 1, '2024-12-18'),
            ('Science Weekly', 'Magazine', 0, '2024-12-02'),
            ('The Catcher in the Rye', 'Book', 1, '2024-12-22'),
            ('The Economist', 'Magazine', 1, '2024-12-15'),
            ('Ulysses', 'Book', 1, '2025-01-05'),
            ('Wired', 'Magazine', 1, '2025-02-28'),
            ('Don Quixote', 'Book', 0, '2025-01-10'),
            ('Popular Mechanics', 'Magazine', 1, '2025-02-10'),
            ('The Odyssey', 'Book', 1, '2024-12-01'),
            ('Scientific American', 'Magazine', 1, '2025-03-05'),
            ('Crime and Punishment', 'Book', 1, '2025-01-12'),
            ('New Scientist', 'Magazine', 1, '2025-02-22'),
            ('A Tale of Two Cities', 'Book', 1, '2024-12-14'),
            ('The Atlantic', 'Magazine', 0, '2024-12-04'),
            ('Jane Eyre', 'Book', 1, '2024-12-29'),
            ('The New Yorker', 'Magazine', 1, '2025-02-18'),
            ('The Iliad', 'Book', 1, '2024-12-11'),
            ('Rolling Stone', 'Magazine', 1, '2025-01-20'),
            ('Anna Karenina', 'Book', 0, '2024-12-08'),
            ('GQ', 'Magazine', 1, '2025-03-02'),
            ('The Hobbit', 'Book', 1, '2024-12-19'),
            ('Discover', 'Magazine', 0, '2024-12-09'),
            ('The Divine Comedy', 'Book', 1, '2024-12-23'),
            ('Esquire', 'Magazine', 1, '2025-01-31'),
            ('Les Misérables', 'Book', 1, '2024-12-07'),
            ('Fast Company', 'Magazine', 1, '2025-02-25'),
            ('Wuthering Heights', 'Book', 1, '2025-01-21'),
            ('Men''s Health', 'Magazine', 1, '2025-01-12'),
            ('Hamlet', 'Book', 1, '2024-12-30'),
            ('Elle', 'Magazine', 0, '2024-12-06'),
            ('Macbeth', 'Book', 1, '2024-12-28'),
            ('National Review', 'Magazine', 1, '2025-02-03'),
            ('Othello', 'Book', 0, '2025-01-08'),
            ('Harvard Business Review', 'Magazine', 1, '2025-02-15'),
            ('King Lear', 'Book', 1, '2024-12-26'),
            ('Artforum', 'Magazine', 0, '2024-11-30'),
            ('Romeo and Juliet', 'Book', 1, '2025-01-25'),
            ('Architectural Digest', 'Magazine', 1, '2025-03-03'),
            ('Frankenstein', 'Book', 1, '2024-12-31'),
            ('The Walrus', 'Magazine', 1, '2025-02-20'),
            ('Dracula', 'Book', 1, '2025-01-30'),
            ('Bon Appétit', 'Magazine', 1, '2025-03-08'),
            ('The Picture of Dorian Gray', 'Book', 1, '2024-12-27'),
            ('Vanity Fair', 'Magazine', 1, '2025-03-07'),
            ('Great Expectations', 'Book', 1, '2025-01-01'),
            ('Sports Illustrated', 'Magazine', 1, '2025-03-04'),
            ('The Scarlet Letter', 'Book', 1, '2025-01-15'),
            ('The Week', 'Magazine', 1, '2025-02-26'),
            ('The Metamorphosis', 'Book', 0, '2025-01-09'),
            ('Marie Claire', 'Magazine', 1, '2025-03-11'),
            ('Lolita', 'Book', 1, '2024-12-21'),
            ('People', 'Magazine', 1, '2025-03-10'),
            ('Heart of Darkness', 'Book', 1, '2024-12-17'),
            ('Newsweek', 'Magazine', 0, '2024-12-05'),
            ('On the Road', 'Book', 1, '2025-01-03'),
            ('Bloomberg Businessweek', 'Magazine', 1, '2025-02-19'),
            ('The Jungle', 'Book', 1, '2025-01-13'),
            ('Entertainment Weekly', 'Magazine', 1, '2025-03-09');

        -- Insert users into users table
        INSERT INTO users (username, password, role) VALUES 
            ('super', 'super', 'SUPER_ADMIN'),
            ('admin', 'admin', 'ADMIN'),
            ('user', 'user', 'USER');
        """;

        // Create the new database and insert data
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlScript);
            System.out.println("Database created and initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing the database: " + e.getMessage());
        }
    }
}
