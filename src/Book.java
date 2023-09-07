import java.time.LocalDate;

/**
 * Darren Chance<br>
 * CEN 3024 - Software Development 1<br>
 * August 28, 2023<br>
 * Book.java<br>
 * <p>
 * <p>
 * The class {@code Book} creates a book and stores the book's details. This allows for easy retrival of details such as
 * title, book id number and author. This class does not contain any methods aside from getters and setters.
 */

public class Book {
    /**
     * Book is checked out in.
     */
    public static final String CHECKED_OUT = "Checked Out";
    /**
     * Book is checked in.
     */
    public static final String CHECKED_IN = "Checked In";
    private String title;
    private String author;
    private int BarcodeID;
    private String status;
    private LocalDate dueDate = null;
    private String genre;

    public Book(int barcodeID, String title, String author, String genre, String status, LocalDate dueDate) {
        this.title = title;
        this.author = author;
        BarcodeID = barcodeID;
        this.status = status;
        this.dueDate = dueDate;
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Book(int BarcodeID, String title, String author) {
        super();
        this.author = author;
        this.BarcodeID = BarcodeID;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setAuthor(String author, String auth) {
        this.author = author;
    }

    public int getBarcodeID() {
        return BarcodeID;
    }

    public void setBarcodeID(int barcodeID) {
        this.BarcodeID = barcodeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        if (status != CHECKED_IN && status != CHECKED_OUT) {
            throw new IllegalArgumentException("setStatus must be" + " one of: CHECKED_IN, or CHECKED_OUT");
        }

        if (status == CHECKED_IN) {
            status = CHECKED_IN;
        }

        if (status == CHECKED_OUT) {
            status = CHECKED_OUT;
        }
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public static Book bookTitle(String title) {

        return new Book(1, title, null, null, null, null);

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book that = (Book) o;

        return title.equals(that.title);
    }
}
