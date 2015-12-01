package LeapInterface;

import com.leapmotion.leap.Vector;

import java.awt.*;
import java.awt.event.InputEvent;


public class LeapTrackPad extends ScreenGadget {

    private final float Z_THRESHOLD = 50.0f;
    private Dimension screenSize;
    private Robot myRobot;
    private boolean robotErrorOccurred = false;

    public LeapTrackPad (Vector topLeft, Vector bottomRight, String gadgetName, Command command){
        super (topLeft, bottomRight, gadgetName, command);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            myRobot = new Robot();
        } catch (Exception e){
            System.out.println("Error occurred while adding robot");
            e.printStackTrace();
            robotErrorOccurred = true;
        }
    }


    public void executeCommand(Vector lastFingerPosition){
        if (Math.abs(lastFingerPosition.getZ()) > Z_THRESHOLD || robotErrorOccurred){
            return;
        }
        Vector normalizedPoint = normalizeToGadget(lastFingerPosition);
        float projectedX = normalizedPoint.getX() * (float)screenSize.getWidth();
        float projectedY = (1-normalizedPoint.getY()) * (float)screenSize.getHeight();
        myRobot.mouseMove((int)projectedX, (int)projectedY);
    }


    public Vector normalizeToGadget(Vector position){
        float width = getBottomRight().getX() - getTopLeft().getX();
        float height = getTopLeft().getY() - getBottomRight().getY();
        float normalX = (position.getX() - getTopLeft().getX())/width;
        float normalY = (position.getY() - getBottomRight().getY())/height;
        return new Vector(normalX, normalY, 0);
    }


    public void executeCommand(){
        if (!robotErrorOccurred){
            myRobot.mousePress(InputEvent.BUTTON1_MASK);
            myRobot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
    }
}
