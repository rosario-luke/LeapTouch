package GUI;

import LeapInterface.LeapTouchController;

import javax.swing.*;
import java.awt.*;


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

        createVisPanel();
        createAddPanel();
        createEditPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Vis", visPanel);
        tabbedPane.addTab("Add", addPanel);
        tabbedPane.addTab("Edit", editPanel);
        topPanel.add(tabbedPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
