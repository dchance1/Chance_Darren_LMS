import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = "";
        Database database = new Database();

        // Loading any books currently in the database on program launch
        database.getBooks();

        // Printing menu options to console
        String menu = "-".repeat(40) + "\n" + "Application Menu\n" + "-".repeat(40) + "\n" + "'v' - to display book " +
                "collection\n" + "'d' - to remove a book from collection\n" + "'a' - to add a book to the " +
                "collection\n" + "'q' - to end application\n" + "-".repeat(40);
        System.out.printf(menu);


        while (!s.equals("q")) {
            System.out.printf("\nPlease type the letter associated with the action you would like to perform: ");
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
                    //database.getBooks();
                    database.addBooks();
                    break;
                case "q":
                    System.out.println("Program ended!");
                    break;
            }
        }
    }
}
