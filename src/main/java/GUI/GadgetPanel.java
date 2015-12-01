package GUI;

import LeapInterface.GUIGadgetSetup;
import LeapInterface.PointListener;
import LeapInterface.ScreenGadget;
import com.leapmotion.leap.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * The GadgetPanel is a panel that can be dropped in anywhere and draws all the current gadgets used
 * by the PointListener passed to it in the constructor. The GadgetPanel can also be used to draw where the current
 * finger position is, as well as draw coordinates given to it by the GUIGadgetSetup
 */
public class GadgetPanel extends JPanel {

    private PointListener pointListener;
    private GUIGadgetSetup gadgetSetup;
    private float FINGER_RADIUS_RATIO = 0.035f;
    private float CORNER_RADIUS_RATIO = 0.015f;


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


    /**
     * Sets the GUIGadgetSetup that the panel uses
     * @param setup
     */
    public void setGadgetSetup(GUIGadgetSetup setup){
        gadgetSetup = setup;
    }


    /**
     * Clears the GUIGadgetSetup
     */
    public void clearGadgetSetup(){
        gadgetSetup = null;
    }


    /**
     * Custom method for painting all the current gadgets that the PointListener listens to
     * @param g The graphics object to draw to
     */
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


    /**
     * Draws all the fingers on the graphics object using the normalized points stored in the PointListener
     * @param g Graphics object used for drawing
     */
    public void drawFingers(Graphics g){
        g.setColor(Color.green);
        Vector fingerPosition = pointListener.getNormalizedFingerPosition();
        if (fingerPosition == null){
            return;
        }
        float circleX = getWidth() * fingerPosition.getX();
        float circleY = getHeight() * (1 - fingerPosition.getY());
        float radius = getWidth() * FINGER_RADIUS_RATIO;
        drawCircle(g, (int)circleX, (int)circleY, (int) radius);
    }


    /**
     * Draws the GUIGadgetSetup components such as current TopLeft and TopRight
     * @param g Graphics object to draw with
     */
    public void drawSetup(Graphics g){
        Vector topLeft = gadgetSetup.getNormalizedTopLeft();
        Vector bottomRight = gadgetSetup.getNormalizedBottomRight();
        float radius = getWidth() * CORNER_RADIUS_RATIO;

        if (topLeft != null){
            g.setColor(Color.orange);
            float circleX = getWidth() * topLeft.getX();
            float circleY = getHeight() * (1 - topLeft.getY());
            drawCircle(g, (int)circleX, (int)circleY, (int) radius);
        }

        if (bottomRight != null){
            g.setColor(Color.blue);
            float circleX = getWidth() * bottomRight.getX();
            float circleY = getHeight() * (1 - bottomRight.getY());
            drawCircle(g, (int)circleX, (int)circleY, (int) radius);
        }
    }


    /**
     * Draws a circle
     * @param g Graphics object to draw with
     * @param centerX Center x-coordinate for the circle
     * @param centerY Center y-coordinate for the circle
     * @param radius Radius of the circle
     */
    public void drawCircle(Graphics g, int centerX, int centerY, int radius){
        int topLeftX = centerX - radius;
        int topLeftY = centerY - radius;
        g.drawOval(topLeftX, topLeftY, radius*2, radius*2);
    }
}
