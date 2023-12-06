import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.SortedSet;


public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField barcodeOrTitleTxtField;
    private JTextField titleTxtField;
    private JTable table1;
    private JPanel buttonPanel;
    private JButton clearButton;
    private JButton deleteBookButton;
    private JButton ADDBOOKButton;
    private JPanel sidePanel;
    private JButton selectDatabaseButton;
    private JLabel barcodeOrTitleLabel;
    private JLabel titleLabel;
    private JLabel fileNameLabel;
    private JTextArea messageText;
    private JTextField fileNameField;
    private JButton selectDatabaseEnterButton;
    private JScrollPane tableScrollPane;
    private JTextPane systemMessages;
    private JScrollPane headerScrollPane;
    private JButton deleteButton;
    private JButton openDatabaseFileButton;
    private JPanel leftPanel;
    private JButton exitButton;
    private JButton checkBookOutButton;
    private JButton checkOutButton;
    private JButton checkBookInButton;
    private JButton checkInButton;
    //private JTextPane thisIsSampleTextTextPane;
    private Hashtable<Integer, Book> books;
    private SortedSet<Integer> keys;
    private String title = "";
    private String author = "";
    private String genre = "";
    DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    //private static LocalDate date = LocalDate.now();
    private int barcodeID;
    private LocalDate dueDate = LocalDate.now();
    private String status;
    private String fileName;


    Connection conn = null;
    Database db = new Database();

    public void updateSQLiteTable() {
        try {
            ResultSet rs = db.getBooks_table();
            // Creating object to store rows of data from SQLite database
            Object[] obj = new Object[6];
            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                // read the result set
                obj[0] = Integer.parseInt(rs.getString("barcode"));
                obj[1] = rs.getString("title");
                obj[2] = rs.getString("author");
                obj[3] = rs.getString("genre");
                obj[4] = rs.getString("status");

                LocalDate dueDate = null;

                // Check for due date if book status not checked in
                if (obj[4].toString().equals(Book.CHECKED_IN) ||
                    obj[4].toString().strip().toLowerCase().equals("null")) {
                    dueDate = null;
                } else {
                    // Parse text to date if parsable, if not throw exception and advise user of the issue and set due
                    // date to null;
                    try {
                        dueDate = LocalDate.parse(rs.getString("due_date"), dtFormatter);
                        obj[5] = dueDate;
                        //System.out.println("due_date" + rs.getString("due_date"));
                    } catch (Exception e) {
                        String message =
                                "Invalid date \'" + rs.getString("due_date") + "\' entered for Barcode Number '" +
                                obj[0].toString() + "' date must match 'yyyy/mm/dd'";
                        systemMessages.setText(message);
                        dueDate = null;
                    }
                }
                model.addRow(obj);
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            systemMessages.setText("Table could not load, try selecting database then try again");
        }
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


    private void checkInBook(String title) {
        //String input = title;
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement =
                    "UPDATE books_table " + "SET status = 'Checked In', due_date = NULL " + "WHERE title = '" + title +
                    "'";
            int rows = stmt.executeUpdate(sqlStatement);
            updateSQLiteTable();

            if (rows == 0) {
                systemMessages.setText("Title '" + title + "' does not exist");
            } else {
                systemMessages.setText("Book '" + title + "' checked in");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    private void checkOutBook(String title) {
        String input = title;
        dueDate = LocalDate.now().plusWeeks(4);
        String dueDateText = dtFormatter.format(dueDate).toString();

        boolean isCheckedOut = false;

        try {
            Statement statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(
                    "SELECT status " + "FROM books_table " + "WHERE title = '" + input + "'");
            while (rs.next()) {
                String status = rs.getString("status");
                if (status.equals("Checked Out")) {
                    isCheckedOut = true;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
        if (!isCheckedOut) {
            try {
                Statement stmt = conn.createStatement();
                String sqlStatement =
                        "UPDATE books_table " + "SET status = 'Checked Out', " + "due_date = '" + dueDateText + "' " +
                        "WHERE title = '" + input + "'";
                int rows = stmt.executeUpdate(sqlStatement);
                updateSQLiteTable();


                if (rows == 0) {
                    systemMessages.setText("Title '" + title + "' does not exist");
                }
                if (rows > 0) {
                    systemMessages.setText("Book '" + title + "' checked out");
                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        if (isCheckedOut) {
            systemMessages.setText("Book already checked out");
        }


    }

    private void deleteBook(String barcodeOrTitle) {

        String input = barcodeOrTitle;

        if (isIntegerInput(barcodeOrTitle)) {

            try {
                Statement stmt = conn.createStatement();
                String sqlStatement = "DELETE FROM books_table " + "WHERE barcode = " + input;
                int rows = stmt.executeUpdate(sqlStatement);
                updateSQLiteTable();
                if (rows == 0) {
                    systemMessages.setText("Barcode '" + barcodeOrTitle + "' does not exist");
                }
                if (rows > 0) {
                    systemMessages.setText("Book with barcode '" + barcodeOrTitle + "' deleted");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        } else {
            System.out.println("Title Entered");

            try {
                Statement stmt = conn.createStatement();
                String sqlStatement = "DELETE FROM books_table " + "WHERE title = '" + input + "'";
                int rows = stmt.executeUpdate(sqlStatement);
                updateSQLiteTable();
                if (rows == 0) {
                    systemMessages.setText("Title '" + barcodeOrTitle + "' does not exist");
                }
                if (rows > 0) {
                    systemMessages.setText("Book title '" + barcodeOrTitle + "' deleted");
                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


    }


    MainFrame() {
        setContentPane(mainPanel);
        setTitle("LMS Main");
        //setSize(700,500);
        setBounds(200, 200, 700, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 400));


        setVisible(true);


        StyledDocument docStyle = systemMessages.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        docStyle.setParagraphAttributes(0, docStyle.getLength(), centerAttribute, false);

        systemMessages.setText("Select a menu option below");

        createTable();
        //messageText.setText("Error message, ID not found");

        // On Button Click prompts user to enter a file name
        selectDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] obj = sidePanel.getComponents();
                for (Component o : obj) {
                    o.setVisible(false);
                }

                systemMessages.setText("Enter the database file name");
                sidePanel.setVisible(true);

                fileNameLabel.setVisible(true);
                fileNameField.setVisible(true);
                openDatabaseFileButton.setVisible(true);
                fileNameField.requestFocusInWindow();


            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (!conn.isClosed()) {
                        systemMessages.setText(
                                "Enter the book barcode number or title for the book you wish to " + "delete");
                        Component[] obj = sidePanel.getComponents();

                        for (Component o : obj) {
                            o.setVisible(false);
                        }
                        sidePanel.setVisible(true);
                        barcodeOrTitleLabel.setVisible(true);
                        barcodeOrTitleTxtField.setVisible(true);
                        deleteButton.setVisible(true);

                        systemMessages.setText("Enter book barcode or ID number to delete");

                    }
                } catch (Exception e2) {
                    systemMessages.setText("Please select a database first\n" + "-- NO DATABASE LOADED --");
                }


            }
        });

        openDatabaseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = "Text";
                fileName = fileNameField.getText();

                ///load table
                try {

                    conn = db.connect(fileName);
                    //connect(fileName);
                    updateSQLiteTable();
                    systemMessages.setText("Database file '" + fileName + "' loaded");
                } catch (Exception e1) {
                    // don't try and load table
                    systemMessages.setText(e1.getMessage());

                }


                //end of load table


                sidePanel.setVisible(false);
                Component[] obj = sidePanel.getComponents();
                for (Component o : obj) {
                    o.setVisible(false);
                }

            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String barcodeOrTitle = "";
                barcodeOrTitle = barcodeOrTitleTxtField.getText();

                // delete row
                deleteBook(barcodeOrTitle);

                // end of delete code

                // UI elements closing side panel
                sidePanel.setVisible(false);
                Component[] obj = sidePanel.getComponents();
                for (Component o : obj) {
                    o.setVisible(false);
                }

            }
        });
        ADDBOOKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                //do nothing
                systemMessages.setText(
                        "The 'ADD BOOK' method is under maintenance, please follow the manual process for adding" +
                        " books to the library via the database sql file");

            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (!conn.isClosed()) {
                        systemMessages.setText("Enter the book title you wish to check out");
                        Component[] obj = sidePanel.getComponents();

                        for (Component o : obj) {
                            o.setVisible(false);
                        }
                        sidePanel.setVisible(true);
                        titleLabel.setVisible(true);
                        titleTxtField.setVisible(true);
                        checkBookOutButton.setVisible(true);
                    }
                } catch (Exception e2) {
                    systemMessages.setText("Please select a database first\n" + "-- NO DATABASE LOADED --");
                }


            }
        });
        checkBookOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = titleTxtField.getText();
                checkOutBook(input);



                sidePanel.setVisible(false);


            }
        });
        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (!conn.isClosed()) {
                        systemMessages.setText("Enter the book title you wish to check in");
                        Component[] obj = sidePanel.getComponents();

                        for (Component o : obj) {
                            o.setVisible(false);
                        }
                        sidePanel.setVisible(true);
                        titleLabel.setVisible(true);
                        titleTxtField.setVisible(true);
                        checkBookInButton.setVisible(true);
                    }
                } catch (Exception e2) {
                    systemMessages.setText("Please select a database first\n" + "-- NO DATABASE LOADED --");
                }

            }
        });
        checkBookInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = titleTxtField.getText();

                //check in code
                checkInBook(input);

                //end of checkin code


                sidePanel.setVisible(false);
            }
        });


    }

    public void resetMessage() {
        messageText.setText("Select a menu option below");
    }

    private void createTable() {
        table1.setModel(new DefaultTableModel(null, new String[]{"Barcode", "Title", "Author", "Genre", "Status",
                "Due Date"}

        ));


        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        table1.setShowHorizontalLines(true);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (int i = 0; i < 6; i++) {
            table1.getColumnModel().getColumn(i).setMinWidth(70);
            table1.getColumnModel().getColumn(i).setPreferredWidth(200);
            table1.getColumnModel().getColumn(i).setMaxWidth(300);
        }


    }


    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();

        mainFrame.sidePanel.setVisible(false);

        mainFrame.systemMessages.setText("Select a menu option below");


    }


}
