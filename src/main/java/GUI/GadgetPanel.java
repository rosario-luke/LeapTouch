package GUI;

import LeapInterface.GUIGadgetSetup;
import LeapInterface.PointListener;
import LeapInterface.ScreenGadget;
import com.leapmotion.leap.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class GadgetPanel extends JPanel {

    private PointListener pointListener;
    private GUIGadgetSetup gadgetSetup;
    private float FINGER_RADIUS_RATIO = 0.07f;
    private float CORNER_RADIUS_RATIO = 0.03f;


    public GadgetPanel(PointListener pointListener){
        this.pointListener = pointListener;
    }


    public void paint(Graphics g){
        if (pointListener.isListening()) {
            drawGadgets(g);
            drawFingers(g);
        }
        if (gadgetSetup != null){
            drawSetup(g);
        }
        repaint();
    }


    public void setGadgetSetup(GUIGadgetSetup setup){
        gadgetSetup = setup;
    }


    public void clearGadgetSetup(){
        gadgetSetup = null;
    }


    public void drawGadgets(Graphics g){
        g.setColor(Color.red);
        ArrayList<ScreenGadget> myScreenGadgets = pointListener.getScreenGadgets();
        for(ScreenGadget curGadget : myScreenGadgets){
            Vector topLeft = curGadget.getNormalizedTopLeft();
            Vector bottomRight = curGadget.getNormalizedBottomRight();
            if (topLeft == null || bottomRight == null){
                continue;
            }
            float nLX = getWidth()* curGadget.getNormalizedTopLeft().getX();
            // Subtract Y-Coordinate from 1 because (0,0) is top left in application window
            float nLY = getHeight()* (1 - curGadget.getNormalizedTopLeft().getY());
            float nRX = getWidth()* curGadget.getNormalizedBottomRight().getX();
            float nRY = getHeight()* (1 - curGadget.getNormalizedBottomRight().getY());
            g.drawRect((int) nLX, (int) nLY, (int) (nRX - nLX), (int) (nRY - nLY));
        }

    }


    public void drawFingers(Graphics g){
        g.setColor(Color.green);
        Vector fingerPosition = pointListener.getNormalizedFingerPosition();
        if (fingerPosition == null){
            return;
        }
        float circleX = getWidth() * fingerPosition.getX();
        float circleY = getHeight() * (1 - fingerPosition.getY());
        float radius = getWidth() * FINGER_RADIUS_RATIO;
        g.drawOval((int) circleX, (int) circleY, (int) radius, (int) radius);
    }


    public void drawSetup(Graphics g){
        Vector topLeft = gadgetSetup.getNormalizedTopLeft();
        Vector bottomRight = gadgetSetup.getNormalizedBottomRight();
        float radius = getWidth() * CORNER_RADIUS_RATIO;

        if (topLeft != null){
            g.setColor(Color.orange);
            float circleX = getWidth() * topLeft.getX();
            float circleY = getHeight() * (1 - topLeft.getY());
            g.drawOval((int)circleX, (int)circleY, (int) radius, (int) radius);
        }

        if (bottomRight != null){
            g.setColor(Color.blue);
            float circleX = getWidth() * bottomRight.getX();
            float circleY = getHeight() * (1 - bottomRight.getY());
            g.drawOval((int)circleX, (int)circleY, (int) radius, (int) radius);
        }
    }
}
