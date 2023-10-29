import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel tablePanel;
    private JScrollPane scrollPane1;
    private JTable table1;

    MainFrame(){
        setContentPane(mainPanel);
        setTitle("LMS Main");
        //setSize(700,500);
        setBounds(200,200,700,500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 400));
        setVisible(true);

        createTable();


    }

    private void createTable() {
        table1.setModel(new DefaultTableModel(
                new Object[][] {{"Kathy", "Smith"},{"John", "Doe"}},new String[]{"yes","no"}

        ));
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.addRow(new String[]{"HEllo", "no"});
        model.addRow(new Integer[]{1,2});

    }






}
