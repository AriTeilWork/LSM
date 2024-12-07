import java.time.LocalDate;

public abstract class LibraryItem {
    private int id;
    private String title;
    private String type;
    private boolean available;
    private LocalDate dueDate;

    public LibraryItem(int id, String title, String type, boolean available, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.available = available;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public abstract void borrowItem();

    public void returnItem() {
        this.available = true;
        this.dueDate = null;
    }
}