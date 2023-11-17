import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database1 {
    // This class will be used to interface with an SQLite database
    private  Connection conn = null;

    public Connection connect(String fileName) throws Exception {


        String error = "";
        File dbFile = new File(fileName);
        if (dbFile.exists()) {
            try {
                String url = "jdbc:sqlite:" + fileName;
                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite DB name " + fileName + " has been established.");

            } catch (SQLException e) {
                throw new RuntimeException();

            }

        } else {
            throw new RuntimeException("Error message");


        }

        return conn;
    }

}
