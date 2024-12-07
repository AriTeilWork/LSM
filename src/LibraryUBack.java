import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LibraryUBack extends JFrame {
    private LoginDialog loginDialog;
    private LibraryService libraryService;
    private User currentUser;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> typeFilter;

    public LibraryUBack(LibraryService service) {
        this.libraryService = service;

        // Display login dialog
        loginDialog = new LoginDialog(this);
        String username = loginDialog.getUsername();
        String password = loginDialog.getPassword();
        currentUser = libraryService.authenticateUser(username, password);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Invalid login credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(15);
        typeFilter = new JComboBox<>(new String[]{"All", "Book", "Magazine"});
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        add(filterPanel, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton borrowButton = new JButton("Borrow");
        JButton returnButton = new JButton("Return");
        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);

        JButton addItemButton = new JButton("Add Book");
        JButton removeItemButton = new JButton("Remove Book");
        JButton addUserButton = new JButton("Add User");
        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(addUserButton);

        addItemButton.addActionListener(e -> addBook());
        removeItemButton.addActionListener(e -> removeBook());
        addUserButton.addActionListener(e -> addUser());

        add(buttonPanel, BorderLayout.SOUTH);

        borrowButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int itemId = (int) table.getValueAt(selectedRow, 0);
                libraryService.borrowItem(itemId, currentUser.getId());
                updateTable();
            }
        });

        returnButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int itemId = (int) table.getValueAt(selectedRow, 0);
                libraryService.returnItem(itemId);
                updateTable();
            }
        });

        updateTable();
        setVisible(true);
    }

    private void addBook() {
        String title = JOptionPane.showInputDialog(this, "Enter book title:");
        String type = JOptionPane.showInputDialog(this, "Enter book type (Book, Magazine):");
        String dueDateStr = JOptionPane.showInputDialog(this, "Enter due date (yyyy-mm-dd):");

        try {
            LocalDate dueDate = LocalDate.parse(dueDateStr);

            LibraryItem newItem;
            if ("Book".equalsIgnoreCase(type)) {
                newItem = new Book(generateItemId(), title);
            } else if ("Magazine".equalsIgnoreCase(type)) {
                newItem = new Magazine(generateItemId(), title);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid item type");
                return;
            }

            newItem.setAvailable(true);
            newItem.setDueDate(dueDate);
            libraryService.addLibraryItem(newItem);

            updateTable();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please enter a date in the format yyyy-mm-dd.");
        }
    }

    private void removeBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int itemId = (int) table.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this item?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    libraryService.removeLibraryItem(itemId);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Item removed successfully!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error removing item: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
        }
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        String password = JOptionPane.showInputDialog(this, "Enter password:");

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        String[] roles = {"FacultyMember", "StudentMember", "GuestMember"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        int result = JOptionPane.showConfirmDialog(this, roleComboBox, "Select role", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        String roleStr = (String) roleComboBox.getSelectedItem();

        try {
            Role role;
            switch (roleStr) {
                case "FacultyMember":
                    role = Role.FACULTY_MEMBER;
                    break;
                case "StudentMember":
                    role = Role.STUDENT_MEMBER;
                    break;
                case "GuestMember":
                    role = Role.GUEST_MEMBER;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role.");
            }

            User newUser = new User(username, password, role, "reader" + (int) (Math.random() * 1000)) {
                @Override
                public void borrowItem(LibraryItem item) {
                    // Implementation here
                }

                @Override
                public void returnItem(LibraryItem item) {
                    // Implementation here
                }
            };

            UserDatabaseManager userDbManager = new UserDatabaseManager();
            userDbManager.addUser(newUser);
            userDbManager.closeConnection();

            JOptionPane.showMessageDialog(this, "User added successfully!");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid role.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }
    private void updateTable() {
        String searchText = searchField.getText();
        String type = (String) typeFilter.getSelectedItem();
        List<LibraryItem> items = libraryService.getFilteredItems(searchText, type);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Title", "Type", "Available", "Due Date"}, 0);
        for (LibraryItem item : items) {
            model.addRow(new Object[]{item.getId(), item.getTitle(), item.getType(), item.isAvailable(), item.getDueDate()});
        }
        table.setModel(model);
    }

    private int generateItemId() {
        return (int) (Math.random() * 1000);
    }

    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService(new DatabaseManager());
        new LibraryUI(libraryService);
    }
}
