import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private static String genre = "";
    DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
    //private static LocalDate date = LocalDate.now();
    private static int barcodeID;
    private static LocalDate dueDate = LocalDate.now();
    private static String status;
    private static String databaseFileName;
    private static String[] bookParse;
    private static Hashtable<Integer, Book> books;
    private static SortedSet<Integer> keys;

    public static String getDatabaseFileName() {
        return databaseFileName;
    }

    public static void setDatabaseFileName(String databaseFileName) {
        Database.databaseFileName = databaseFileName;

    }

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

            barcodeID = Integer.valueOf(bookParse[0]);
            title = bookParse[1];
            author = bookParse[2];
            genre = bookParse[3];
            // checked in / out logic
            if (bookParse[4].toLowerCase().equals(Book.CHECKED_IN.toLowerCase())) {
                status = Book.CHECKED_IN;
            } else if (bookParse[4].toLowerCase().equals(Book.CHECKED_OUT.toLowerCase())) {
                status = Book.CHECKED_OUT;
            }

            // Check for due date if book status not checked in
            if (bookParse[5].equals(Book.CHECKED_IN) || bookParse[5].strip().toLowerCase().equals("null")) {
                dueDate = null;
            } else {
                // Parse text to date if parsable, if not throw exception and advise user of the issue and set due
                // date to null;
                try {
                    dueDate = LocalDate.parse(bookParse[5], dtFormatter);
                } catch (Exception e) {
                    //System.out.println("exception caught can't parse \'" + bookParse[5] + "\' to a date");
                    String message = "Invalid date \'" + bookParse[5] + "\' entered for Barcode Number '" + barcodeID +
                                     "' date must match 'dd/mm/yyyy'";
                    printMessage("-Error Message-", message);
                    dueDate = null;
                }
            }


            Book book = new Book(barcodeID, title, author, genre, status, dueDate);
            books.put(barcodeID, book);
        }
        System.out.println("-- Your collection has been successfully loaded from the library database text file --\n");
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
                barcodeID = Integer.valueOf(s);
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
            if (!books.containsKey(barcodeID)) {
                System.out.printf("Please enter the book title: ");
                title = in.nextLine();
                System.out.println();
                // Get book author from user input
                System.out.printf("Please enter the book author: ");
                author = in.nextLine();
                System.out.println();
                // add book object to books Hashtable
                Book book = new Book(barcodeID, title, author);
                books.put(barcodeID, book);
                book.setStatus(Book.CHECKED_OUT);

                // Sort books by id and overwrite library database
                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();
                // Update library database file with books
                for (Integer i : keys) {
                    barcodeID = books.get(i).getBarcodeID();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();

                    list.add(String.format("%d,%s,%s", barcodeID, title, author));
                    writeFile(list, "Library Database.txt");
                }
            } else {
                System.out.printf(
                        "-- Book already exists with id number '" + barcodeID + "' returning to the main " + "menu" +
                        " --\n");
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
                barcodeID = books.get(i).getBarcodeID();
                title = books.get(i).getTitle();
                author = books.get(i).getAuthor();
                genre = books.get(i).getGenre();
                status = books.get(i).getStatus();
                dueDate = books.get(i).getDueDate();
                String formattedDate = "";
                if (dueDate == null) {
                    formattedDate = "null";
                } else {
                    formattedDate = dtFormatter.format(dueDate).toString();
                }
                System.out.printf("%d, %s, %s, %s, %s, %s\n", barcodeID, title, author, genre, status, formattedDate);
            }
        }

        System.out.printf("-".repeat(40) + "\n");
    }

    public void checkInBooks(){

    }
    public void checkOutBooks(){
        System.out.printf("Please enter the title of the book want to check out: ");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        //check if the book is in the system
        String message;
        if (books.containsValue(Book.bookTitle(input))) {

            int key = 0;
            String value = input;

            keys = new TreeSet<>(books.keySet());
            List<String> list = new ArrayList<String>();


            // Finds the key and checks out book, setting due date 4 weeks from today
            for (Integer i : keys) {
                key = i;
                if (input.equals(books.get(i).getTitle())) {
                    System.out.println("The key is " + key);
                    // Adding 4 weeks to current date and setting to due date
                    books.get(key).setStatus(Book.CHECKED_OUT);
                    books.get(key).setDueDate(LocalDate.now().plusWeeks(4));
                    dueDate = books.get(key).getDueDate();

                    message = "Book titled \'" + input + "\' checked out due date " + dtFormatter.format(dueDate);
                    printMessage("-Confirmation Message-", message);
                    break;
                }
            }

            keys = new TreeSet<>(books.keySet());
            // Update library database file with books
            for (Integer i : keys) {
                barcodeID = books.get(i).getBarcodeID();
                title = books.get(i).getTitle();
                author = books.get(i).getAuthor();
                genre = books.get(i).getGenre();
                status = books.get(i).getStatus();
                dueDate = books.get(i).getDueDate();
                String formattedDate = "";
                if (dueDate == null) {
                    formattedDate = "null";
                } else {
                    formattedDate = dtFormatter.format(dueDate).toString();
                }

                list.add(String.format("%d,%s,%s,%s,%s,%s", barcodeID, title, author, genre, status,
                        formattedDate));
                writeFile(list, "Library Database.txt");
            }


            showBooks();
        } else {
            message = "Book with Title \'" + input + "\' does not exist";
            printMessage("-Error Message-", message);
        }


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

    private boolean isIntegerInput(String input) {
        if (input == null) {
            return false;
        }
        try {
            int n = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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


        System.out.printf("Please enter the barcode number or book title that you want to delete: ");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        if (isIntegerInput(input)) {
            barcodeID = Integer.valueOf(input);
            String deletedBook = "";
            System.out.println();

            String message;
            if (books.containsKey(barcodeID)) {
                deletedBook = books.get(barcodeID).getTitle();
                books.remove(barcodeID);

                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();

                // Clear library database file if no books left
                if (keys.size() <= 0) {
                    writeFile(list, "Library Database.txt");
                }

                // Update library database file with books
                for (Integer i : keys) {
                    barcodeID = books.get(i).getBarcodeID();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();
                    list.add(String.format("%d,%s,%s", barcodeID, title, author));
                    writeFile(list, "Library Database.txt");
                }
                message = "Book titled \'" + deletedBook + "\' successfully deleted";
                printMessage("-Confirmation Message-", message);

                showBooks();
            } else {
                message = "Book with Barcode Number \'" + barcodeID + "\' does not exist";
                printMessage("-Error Message-", message);
            }
        } else {

            String message;
            if (books.containsValue(Book.bookTitle(input))) {

                System.out.println("Book title " + input + " found");

                int key = 0;
                String value = input;

                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();


                // Finds the key and remove book
                for (Integer i : keys) {
                    key = i;
                    if (input.equals(books.get(i).getTitle())) {
                        System.out.println("The key is " + key);
                        books.remove(key);
                        break;
                    }
                }
                // Clear library database file if no books left
                if (keys.size() <= 0) {
                    writeFile(list, "Library Database.txt");
                }
                keys = new TreeSet<>(books.keySet());
                // Update library database file with books
                for (Integer i : keys) {
                    barcodeID = books.get(i).getBarcodeID();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();
                    genre = books.get(i).getGenre();
                    status = books.get(i).getStatus();
                    dueDate = books.get(i).getDueDate();
                    String formattedDate = "";
                    if (dueDate == null) {
                        formattedDate = "null";
                    } else {
                        formattedDate = dtFormatter.format(dueDate).toString();
                    }

                    list.add(String.format("%d,%s,%s,%s,%s,%s", barcodeID, title, author, genre, status,
                            formattedDate));
                    writeFile(list, "Library Database.txt");
                }
                message = "Book titled \'" + input + "\' successfully deleted";
                printMessage("-Confirmation Message-", message);

                showBooks();
            } else {
                message = "Book with Title \'" + input + "\' does not exist";
                printMessage("-Error Message-", message);
            }
        }

        /*
        Boolean isValidID = false;
        // getting input from user and if valid integer isValidID = true, else display
        // error message
        try {
            barcodeID = Integer.valueOf(in.nextLine());

            isValidID = true;
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("\n-- Invalid input, please enter a valid ID number, returning to the main menu --");
        }
        */

        // If id number is a valid integer and the id key is in the database Hashtable
        // the book is deleted. Then the library database text file is updated to
        // reflect change. Otherwise, book is not deleted and the user is presented with
        // a message advising them what happened.
        /*
        if (isValidID) {
            String deletedBook = "";

            if (books.containsKey(barcodeID)) {
                deletedBook = books.get(barcodeID).getTitle();
                books.remove(barcodeID);

                keys = new TreeSet<>(books.keySet());
                List<String> list = new ArrayList<String>();

                // Clear library database file if no books left
                if (keys.size() <= 0) {
                    writeFile(list, "Library Database.txt");
                }

                // Update library database file with books
                for (Integer i : keys) {
                    barcodeID = books.get(i).getBarcodeID();
                    title = books.get(i).getTitle();
                    author = books.get(i).getAuthor();
                    list.add(String.format("%d,%s,%s", barcodeID, title, author));
                    writeFile(list, "Library Database.txt");
                }
                String message = "-- Book titled \"" + deletedBook + "\" successfully deleted --";
                printMessage("-Confirmation Message-", message);

                showBooks();
            } else {
                System.out.println("-- Book with id number: " + barcodeID + " does not exist --");
            }
            // hello
        }
        */
    }

    private void printMessage(String messageHeader, String message) {
        int len = (message.length() - messageHeader.length()) / 2;
        String s = " ".repeat(len) + messageHeader;
        int len2 = message.length() - s.length();

        s = "-".repeat(len) + messageHeader + "-".repeat(len2) + "\n" + message + "\n" + "-".repeat(message.length());

        System.out.println(s + "\n");
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
