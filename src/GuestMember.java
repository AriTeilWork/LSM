import java.util.ArrayList;
import java.util.List;

class GuestMember extends User {
    private static final int BORROW_LIMIT = 1;
    private List<LibraryItem> borrowedItems = new ArrayList<>();

    public GuestMember(int id, String username, String password, Role role, String readerId, String email, String createdAt) {
        super(id, username, password, role, readerId, email, createdAt);
    }

    public GuestMember(String username, String password, Role role, String readerId) {
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