import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Darren Chance<br> CEN 3024 - Software Development 1<br> September 4, 2023<br> Main.java<br>
 * <p>
 * The class {@code Main} is the main entry point to the program called Library Management System.
 * <p>
 * Library Management System is a console-based application. This application is developed to help users maintain a
 * collection of books. The user will have the ability to add new books to the collection, remove a book from the
 * collection using its ID number and lastly list all books currently in the collection. The data will be stored in a
 * text file and be formatted as follows:a
 * <br><br>
 *
 * <p>(IDd
 * Number,Title, Author)
 * <p>
 * 1,To Kill a Mockingbird, Harper Lee<br>
 * <p>
 * 2,1984,George Orwell<br>
 * <p>
 * 3,The Great Gatsby,F. Scott Fitzgerald<br ><br>
 *
 * <p>
 * This class presents the user with a menu of the following 4 options: display book collection, remove a book from
 * collection, add a book to the collection, end application. This call will make calls to the classes Databse.java and
 * Book.java to store data and access necessary fuctions for viewing and manipulation of data.
 * <p>
 */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = "";
        // database object created to store books at runtime and give access to methods to interact, or make changes
        // to the
        // database object
        Database database = new Database();
        // Asking user for file name of databases text file to load from
        System.out.print("Enter the database file name: ");
        database.chooseFile();
        // Loading any books currently in the database on program launch
        System.out.println();
        database.getBooks();
        // Printing menu options to console
        String menu = "-".repeat(40) +
                      "\nApplication Menu\n" +
                      "-".repeat(40) +
                      "\n'v' - to display book collection" +
                      "\n'd' - to remove a book from collection" +
                      "\n'i' - to check in a book" +
                      "\n'o' - to check out a book" +
                      "\n'a' - to add a book to the collection" +
                      "\n'q' - to end application\n" +
                      "-".repeat(40);
        System.out.println(menu);

        while (!s.equals("q")) {
            System.out.printf("Please type the letter associated with the action you would like to perform: ");
            s = in.nextLine().toLowerCase();
            System.out.println();

            switch (s) {
                case "v":
                    database.showBooks();
                    break;
                case "d":
                    database.deleteBooks();
                    break;
                case "a":
                    database.addBooks();
                    break;
                case "i":
                    database.checkInBooks();
                    break;
                case "o":
                    database.checkOutBooks();
                    break;
                case "q":
                    System.out.println("Program ended!");
                    break;
            }
        }
    }
}
