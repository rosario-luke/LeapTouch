package GUI;

import LeapInterface.LeapTouchController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LeapApplication extends JFrame{

    private final int DEFAULT_WIDTH = 700;
    private final int DEFAULT_HEIGHT = 500;

    private	JTabbedPane tabbedPane;
    private	JPanel mainPanel;
    private	JPanel visPanel;
    private	JPanel addPanel;
    private JPanel editPanel;
    private LeapTouchController myController;

    
    public LeapApplication(LeapTouchController ltController)
    {
        myController = ltController;
        setTitle("Leap Motion Touch Anywhere");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setBackground(Color.gray);

        JPanel topPanel = new JPanel();
        topPanel.setLayout( new BorderLayout() );
        getContentPane().add(topPanel);

        createMainPanel();
        createVisPanel();
        createAddPanel();
        createEditPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Main", mainPanel);
        tabbedPane.addTab("Vis", visPanel);
        tabbedPane.addTab("Add", addPanel);
        tabbedPane.addTab("Edit", editPanel);
        topPanel.add(tabbedPane, BorderLayout.CENTER);
        //tabbedPane.setTabPlacement(JTabbedPane.LEFT);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void createMainPanel()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        JLabel label1 = new JLabel( "Username:" );
        label1.setBounds( 10, 15, 150, 20 );
        mainPanel.add(label1);

        JTextField field = new JTextField();
        field.setBounds(10, 35, 150, 20);
        mainPanel.add(field);

        JLabel label2 = new JLabel( "Password:" );
        label2.setBounds(10, 60, 150, 20);
        mainPanel.add(label2);

        JPasswordField fieldPass = new JPasswordField();
        fieldPass.setBounds( 10, 80, 150, 20 );
        mainPanel.add(fieldPass);

        JButton askButton = new JButton();
        askButton.setBounds(10, 120, 100, 30);
        askButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                myController.addGadget();
            }
        });

        mainPanel.add(askButton);
    }


    public void createVisPanel()
    {
        visPanel = new ViewGadgetPanel(myController);
    }


    public void createAddPanel() { addPanel = new AddGadgetPanel(myController);}


    public void createEditPanel(){
        editPanel = new EditGadgetPanel(myController);
    }


    // Main method to get things started
    public static void main( String args[] )
    {
        // Create an instance of the test application
        try {
            LeapTouchController leapController = new LeapTouchController();
            LeapApplication mainFrame = new LeapApplication(leapController);
            mainFrame.setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
