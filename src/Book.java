import java.time.LocalDate;

public class Book extends LibraryItem {

    public Book(int id, String title) {
        super(id, title, "Book", true, LocalDate.now());
    }

    @Override
    public void borrowItem() {
        this.setDueDate(LocalDate.now().plusWeeks(4));
        this.setAvailable(false);
        System.out.println("Borrowed book until: " + this.getDueDate());
    }

    @Override
    public String getType() {
        return "Book";
    }
}