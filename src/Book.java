/**
 * Darren Chance<br>
 * CEN 3024 - Software Development 1<br>
 * August 28, 2023<br>
 * Book.java<br>
 * <p>
 * <p>
 * The class {@code Book} creates a book and stores the book's details. This allows for easy retrival of details such as
 * title, book id number and author. This class does not contain any methods asside from getters and setters.
 */

public class Book {
    private String title;
    private int BarcodeID;
    private String author;

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

}
