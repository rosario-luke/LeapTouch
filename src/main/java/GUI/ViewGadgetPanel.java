package GUI;

import LeapInterface.LeapTouchController;

import javax.swing.*;
import java.awt.*;

public class ViewGadgetPanel extends JPanel {

    private LeapTouchController myController;
    private JPanel gadgetPanel;

    public ViewGadgetPanel(LeapTouchController controller){
        myController = controller;
        setLayout(new BorderLayout());
        gadgetPanel = new GadgetPanel(controller.getPointListener());
        add(gadgetPanel, BorderLayout.CENTER);

    }


    public void paint(Graphics g){
        gadgetPanel.paint(g);
        repaint();
    }
}
