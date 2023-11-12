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
import java.util.TreeSet;


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
    private final Database database = new Database();
    private Hashtable<Integer, Book> books;
    private SortedSet<Integer> keys;
    private String title = "";
    private String author = "";
    private String genre = "";
    DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
    //private static LocalDate date = LocalDate.now();
    private int barcodeID;
    private LocalDate dueDate = LocalDate.now();
    private String status;
    private String fileName;

    Connection conn = null;

    public void connect() {

        String error = "";
        try {
            // db parameters

            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:LMS.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            error = e.getMessage();
            systemMessages.setText(error);
        }


    }

    public void updateSQLiteTable(){
        try {
            Statement statement = conn.createStatement();

            ResultSet rs = statement.executeQuery("select * from books_table");
            while(rs.next())
            {
                // read the result set
                System.out.println("barcode = " + rs.getString("barcode"));
                System.out.println("title = " + rs.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    private void loadTable(String fileName){



            database.readFile(this.fileName);
            fileNameField.setText("");
            database.getBooks();
            books = database.showBooks();
            //systemMessages.setText(this.fileName + " database file loaded");


            keys = new TreeSet<>(books.keySet());
            Object[] obj = new Object[6];
            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);
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

                obj[0] = barcodeID;
                obj[1] = title;
                obj[2] = author;
                obj[3] = genre;
                obj[4] = status;
                obj[5] = dueDate;

                model.addRow(obj);
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
        docStyle.setParagraphAttributes(0, docStyle.getLength(),centerAttribute,false);

        systemMessages.setText("Select a menu option below");

        createTable();
        //messageText.setText("Error message, ID not found");

        // On Button Click prompts user to enter a file name
        selectDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] obj = sidePanel.getComponents();
                for(Component o: obj){
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
                systemMessages.setText("Enter the book barcode number or title for the book you wish to delete");
                Component[] obj = sidePanel.getComponents();

                for(Component o: obj){
                    o.setVisible(false);
                }
                sidePanel.setVisible(true);
                barcodeOrTitleLabel.setVisible(true);
                barcodeOrTitleTxtField.setVisible(true);
                deleteButton.setVisible(true);

                systemMessages.setText("Enter book barcode or ID number to delete");

            }
        });
        openDatabaseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = "Text";
                fileName = fileNameField.getText();
                System.out.println(fileName);
                ///load table
                try{
                    loadTable(fileName);
                    systemMessages.setText("Database file \""+fileName+"\" loaded");
                } catch (Exception e1){
                    systemMessages.setText("The system cannot find the file specified");
                    systemMessages.setCaretPosition(0);
                }


                sidePanel.setVisible(false);
                Component[] obj = sidePanel.getComponents();
                for(Component o: obj){
                    o.setVisible(false);
                }

            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String barcodeOrTitle = "";
                barcodeOrTitle = barcodeOrTitleTxtField.getText();
                String deleteMessage = database.deleteBooks(barcodeOrTitle);
                systemMessages.setText(deleteMessage);
                loadTable(fileName);

                sidePanel.setVisible(false);
                Component[] obj = sidePanel.getComponents();
                for(Component o: obj){
                    o.setVisible(false);
                }

            }
        });
        ADDBOOKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                updateSQLiteTable();

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
                systemMessages.setText("Enter the book title you wish to check out");
                Component[] obj = sidePanel.getComponents();

                for(Component o: obj){
                    o.setVisible(false);
                }
                sidePanel.setVisible(true);
                titleLabel.setVisible(true);
                titleTxtField.setVisible(true);
                checkBookOutButton.setVisible(true);


            }
        });
        checkBookOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = titleTxtField.getText();
                String checkOutStatus = database.checkOutBooks(input);
                systemMessages.setText(checkOutStatus);
                loadTable(fileName);
                sidePanel.setVisible(false);


            }
        });
        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                systemMessages.setText("Enter the book title you wish to check in");
                Component[] obj = sidePanel.getComponents();

                for(Component o: obj){
                    o.setVisible(false);
                }
                sidePanel.setVisible(true);
                titleLabel.setVisible(true);
                titleTxtField.setVisible(true);
                checkBookInButton.setVisible(true);
            }
        });
        checkBookInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = titleTxtField.getText();
                String checkInStatus = database.checkInBooks(input);
                systemMessages.setText(checkInStatus);
                loadTable(fileName);
                sidePanel.setVisible(false);
            }
        });

        connect();




    }

    public void resetMessage() {
        messageText.setText("Select a menu option below");
    }

    private void createTable() {
        table1.setModel(new DefaultTableModel(
                null, new String[]{"Barcode", "Title", "Author",
                "Genre", "Status", "Due Date"}

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
