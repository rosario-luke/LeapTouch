package LeapInterface;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import java.util.LinkedList;


public class GUIGadgetSetup extends Listener {

    private static float movementTolerance = 5;
    private Controller controller;
    private LinkedList<Vector[]> lastFrames;
    private Vector topLeft;
    private Vector bottomRight;

    public GUIGadgetSetup(Controller controller) {
        System.out.println("Initializing controller...");
        lastFrames = new LinkedList<Vector[]>();
        this.controller = controller;
        topLeft = null;
        bottomRight = null;
        controller.addListener(this);
    }


    /**
     * Invoked when controller has a frame
     * Filters out random movements and finds coordinates to set new LeapInterface.ScreenGadget to
     * @param controller
     */
    public void onFrame(Controller controller) {
        Frame currentFrame = controller.frame();

        if (currentFrame.pointables().count() > 0) {
            Vector currentTip = currentFrame.pointables().frontmost().tipPosition();

            if (currentTip.equals(Vector.zero())) {
                return;
            }

            Vector[] nVec = {currentTip, currentFrame.pointables().frontmost().direction() };
            lastFrames.addFirst(nVec);
            if (lastFrames.size() >= 100) {
                lastFrames.removeLast();
                for (int i = 0; i < lastFrames.size() - 1; i++) {
                    float dist = lastFrames.get(i)[0].distanceTo(lastFrames.get(i + 1)[0]);
                    if (dist > movementTolerance) {
                        return;
                    }
                }

                Vector point = currentFrame.pointables().frontmost().stabilizedTipPosition();
                if (topLeft == null){
                    topLeft = point;
                    try{
                        Thread.sleep(1500);
                    } catch(Exception e){
                        System.out.println("Caught Something Bad");
                        System.out.println(e.getStackTrace());
                        System.exit(1);
                    }
                    return;
                }
                bottomRight = point;
                controller.removeListener(this);
                lastFrames.clear();
            }
        }
    }


    public Vector getNormalizedBottomRight(){
        if (bottomRight == null){
            return null;
        }
        return PointListener.currentInteractionBox.normalizePoint(bottomRight);
    }


    public Vector getNormalizedTopLeft(){
        if (topLeft == null){
            return null;
        }
        return PointListener.currentInteractionBox.normalizePoint(topLeft);
    }


    public Vector getRawTopLeft(){
        return topLeft;
    }


    public Vector getRawBottomRight(){
        return bottomRight;
    }


    public void clearTopLeft(){
        topLeft = null;
        controller.addListener(this);
    }


    public void clearBottomRight(){
        bottomRight = null;
        controller.addListener(this);
    }


    public boolean isFinished(){
        return bottomRight != null && topLeft != null;
    }


    public void cleanUp(){
        controller.removeListener(this);
    }

}
