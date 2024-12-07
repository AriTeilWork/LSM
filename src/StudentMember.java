import java.util.ArrayList;
import java.util.List;

class StudentMember extends User {
    private static final int BORROW_LIMIT = 3;
    private List<LibraryItem> borrowedItems = new ArrayList<>();

    public StudentMember(int id, String username, String password, Role role, String readerId, String email, String createdAt) {
        super(id, username, password, role, readerId, email, createdAt);
    }

    public StudentMember(String username, String password, Role role, String readerId) {
        super(username, password, role, readerId);
    }

    @Override
    public void borrowItem(LibraryItem item) {
        if (borrowedItems.size() < BORROW_LIMIT && item.isAvailable()) {
            item.borrowItem();
            borrowedItems.add(item);
        }
    }

    @Override
    public void returnItem(LibraryItem item) {
        if (borrowedItems.remove(item)) {
            item.returnItem();
        }
    }
}