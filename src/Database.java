import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Darren Chance<br> CEN 3024 - Software Development 1<br> August 28, 2023<br> Database.java<br>
 * <p>
 * The {@code Database} class represents a library database.
 * <p>
 * The class {@code Database} contains methods for reading from a library database file, writing to a library database
 * file, adding books to a library database file, deleting books from a library database file and displaying a database
 * to a console.
 */

public class Database {
    private static String title = "";
    private static String author = "";
    private static int id = 0;
    private static String[] bookParse;
    private static Hashtable<Integer, Book> books;
    private static SortedSet<Integer> keys;

    /**
     * Method Name: getBooks
     * <p>
     * This method loads new books from the library database text file, the books are stored as book objects in a
     * Hashtable with key-value pairs. By default, the keys are the book id numbers.
     *
     * @return none
     */
    public void getBooks() {


        books = new Hashtable<Integer, Book>();
        List<String> list = Database.readFile("Library Database.txt");

        // Iterate through list of books in the library database text file. Each line of
        // text containing book details is split by ',' and values are then added to the
        // book object and the book is added to the books Hashtable. The user is then
        // notified when the process is complete.
        for (int i = 0; i < list.size(); i++) {
            bookParse = list.get(i).split(",");

            id = Integer.valueOf(bookParse[0]);
            title = bookParse[1];
            author = bookParse[2];

            Book book = new Book(id, title, author);
            books.put(id, book);
        }
        System.out.println("-- Your collection has been succesfully loaded from the library database text file --\n");

    }


    public void addBooks() {

        Scanner in = new Scanner(System.in);
        String s = "";

        boolean isValidInteger = false;

        // Request book id from user, if invalid input received user is giving option to enter again or cancel with 'c'
        while (!s.equals("c".toLowerCase())) {
            System.out.printf("Please enter the book id: ");
            s = in.nextLine();

            try {
                id = Integer.valueOf(s);
                isValidInteger = true;
                // Code below is skipped if input not valid integer
                s = "c"; // breaks out of loop on receiving valid int input
                System.out.println();
            } catch (NumberFormatException e) {
                if (!s.equals("c".toLowerCase())) {
                    System.out.println("\n-- Invalid input, enter a valid ID number or enter 'c' to return to the " +
                                       "main menu --\n");
                }
            }
        }

        // Proceed to get book title from user input only if user previously entered a valid id number and if the id
        // number does not already exist in the database. This prevents duplicate books or book with the same id key
        // being added
        if (isValidInteger) {
            if (!books.containsKey(id)) {
                System.out.printf("Please enter the book title: ");
                title = in.nextLine();
                System.out.println();
                // Get book author from user input
                System.out.printf("Please enter the book author: ");
                author = in.nextLine();
                System.out.println();
                // add book object to books Hashtable
                Book book = new Book(id, title, author);
                books.put(id, book);
                // Sort books by id and overwrite library database
                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();
                // Update library database file with books
                for (Integer i : keys) {
                    id = books.get(i).getId();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();
                    list.add(String.format("%d,%s,%s", id, title, author));
                    writeFile(list, "Library Database.txt");
                }
            } else {
                System.out.printf(
                        "-- Book already exists with id number '" + id + "' returning to the main " + "menu" + " --\n");
            }
        }
    }

    /**
     * Method Name: showBooks
     * <p>
     * This method sorts the books Hashtable by key, then displays the collection to the console in order by id.
     *
     * @return none
     */
    public void showBooks() {
        keys = new TreeSet<>(books.keySet());
        System.out.printf("-".repeat(40) + "\n");
        System.out.printf("Book Collection \n(ID Number, Title, Author)\n");
        System.out.printf("-".repeat(40) + "\n");


        // Display message to user that library is empty
        if (keys.size() <= 0) {
            System.out.printf("-- EMPTY --\n\n" + "Use 'a' menu option to load new books\n" + "from the Library " +
                              "Database text file\n");
        } else {
            // Iterate through books Hashtable and display book collection to user
            for (Integer i : keys) {
                id = books.get(i).getId();
                title = books.get(i).getTitle();
                author = books.get(i).getAuthor();
                System.out.printf("%d, %s, %s\n", id, title, author);
            }
        }

        System.out.printf("-".repeat(40) + "\n");
    }

    /**
     * Method Name: readFile
     * <p>
     * This method reads a library database text file, then adds each line of text to a list. The list is then
     * returned.
     *
     * @param file A string specifying the file name to open for reading
     * @return a list of strings separated by new lines from the read text file
     */
    private static List<String> readFile(String file) {
        List<String> list = new ArrayList<String>();
        File tempFile = new File(file);
        try {
            Scanner input = new Scanner(tempFile);
            while (input.hasNextLine()) {
                list.add(input.nextLine());
            }
        } catch (FileNotFoundException e1) {
            System.out.println("You have an error: " + e1);
        }
        return list;
    }

    /**
     * Method Name: deleteBooks
     * <p>
     * This method deletes a single book from the library database text file based on book id received from user text
     * input from console. If the book id does not exist in the database the user will receive an error message and no
     * changes will be made to the database. The database text file is then updated to reflect the change.
     *
     * @return none
     * @throws NumberFormatException if the user input is a non integer type.
     */
    public void deleteBooks() {
        System.out.printf("Please enter the book id number that you want to delete: ");
        Scanner in = new Scanner(System.in);

        Boolean isValidID = false;
        // getting input from user and if valid integer isValidID = true, else display
        // error message
        try {
            id = Integer.valueOf(in.nextLine());
            isValidID = true;
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("\n-- Invalid input, please enter a valid ID number, returning to the main menu --");
        }

        // If id number is a valid integer and the id key is in the database Hashtable
        // the book is deleted. Then the library database text file is updated to
        // reflect change. Otherwise, book is not deleted and the user is presented with
        // a message advising them what happened.
        if (isValidID) {
            String deletedBook = "";

            if (books.containsKey(id)) {
                deletedBook = books.get(id).getTitle();
                books.remove(id);

                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();

                // Clear library database file if no books left
                if (keys.size() <= 0) {
                    writeFile(list, "Library Database.txt");
                }

                // Update library database file with books
                for (Integer i : keys) {
                    id = books.get(i).getId();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();
                    list.add(String.format("%d,%s,%s", id, title, author));
                    writeFile(list, "Library Database.txt");
                }
                System.out.println("-- Book titled \"" + deletedBook + "\" successfully deleted --");
            } else {
                System.out.println("-- Book with id number: " + id + " does not exist --");
            }
        }
    }

    /**
     * Method Name: writeFile
     * <p>
     * This method writes to the library database text file, then adds each line of text to a list. The list is then
     * returned containing each line of text
     *
     * @param list A list of strings containing books details
     * @param file A string specifying the file name to open
     * @return none
     */
    public static void writeFile(List<String> list, String file) {
        File tempFile = new File(file);
        try {
            PrintWriter output = new PrintWriter(tempFile);
            for (String e : list) {
                output.println(e.toString());
            }
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("You have an error: " + e);
        }
    }

}
