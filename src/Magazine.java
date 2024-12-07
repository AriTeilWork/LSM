import java.time.LocalDate;

public class Magazine extends LibraryItem {

    public Magazine(int id, String title) {
        super(id, title, "Magazine", true, LocalDate.now());
    }

    @Override
    public void borrowItem() {
        this.setDueDate(LocalDate.now().plusWeeks(2));
        this.setAvailable(false);
        System.out.println("Borrowed magazine until: " + this.getDueDate());
    }

    @Override
    public String getType() {
        return "Magazine";
    }
}