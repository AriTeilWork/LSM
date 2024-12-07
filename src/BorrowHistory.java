import java.time.LocalDate;

public class BorrowHistory {
    private int id;
    private int userId;
    private int libraryItemId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public BorrowHistory(int id, int userId, int libraryItemId, LocalDate borrowDate, LocalDate returnDate, boolean isReturned) {
        this.id = id;
        this.userId = userId;
        this.libraryItemId = libraryItemId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLibraryItemId() {
        return libraryItemId;
    }

    public void setLibraryItemId(int libraryItemId) {
        this.libraryItemId = libraryItemId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
}