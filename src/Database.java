import java.io.File;
import java.sql.*;


public class Database {
    // This class will be used to interface with an SQLite database
    private Connection conn = null;

    public Connection connect(String fileName) {
        File dbFile = new File(fileName);
        // Connect to database if file exists or throw error
        if (dbFile.exists()) {
            try {
                String url = "jdbc:sqlite:" + fileName;
                // create a connection to the database
                conn = DriverManager.getConnection(url);
                System.out.println("Connection to SQLite DB name " + fileName + " has been established.");
            } catch (SQLException e) {
                throw new RuntimeException("System Error, please see database administrator.");
            }
        } else {
            throw new RuntimeException("Could not connect to SQLite database file '" + fileName + "' not found.");
        }
        return conn;
    }

    // Gets all books for SQLite db returns result set obj
    public ResultSet getBooks_table() {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM books_table");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
