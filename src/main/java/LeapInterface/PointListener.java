package LeapInterface;

import com.leapmotion.leap.*;

import java.util.ArrayList;


public class PointListener extends Listener {

    private static float CONFIG_MIN_FORWARD_VEL = 50.0f;
    private static float CONFIG_HISTORY_SECONDS =  0.1f;
    private static float CONFIG_MIN_DISTANCE = 5.0f;

    private Controller myController;
    private ArrayList<ScreenGadget> myScreenGadgets;
    private boolean shouldOutput;
    private boolean isListening;
    private boolean changedGadgets = false;
    private Vector lastFingerPosition = null;
    public static InteractionBox currentInteractionBox;


    public PointListener(Controller controller) {
        myController = controller;
        myScreenGadgets = new ArrayList<ScreenGadget>();
        shouldOutput = false;
        isListening = false;
    }


    /**
     * Adds this class to the set of listeners on the LeapController and configures settings for
     * specific Gesture recognition
     */
    public void start(){
        myController.config().setFloat("Gesture.ScreenTapMinForwardVelocity", CONFIG_MIN_FORWARD_VEL);
        myController.config().setFloat("Gesture.ScreenTap.HistorySeconds", CONFIG_HISTORY_SECONDS);
        myController.config().setFloat("Gesture.ScreenTap.MinDistance", CONFIG_MIN_DISTANCE);
        myController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP, true);
        myController.config().save();
        myController.addListener(this);
        isListening = true;
    }


    /**
     * Stops this class from listening to the controller
     */
    public void stop(){
        myController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP, false);
        myController.removeListener(this);
        isListening = false;
    }


    /**
     * Sets whether should output to console when gadgets are hit
     * @param sOutput Whether this class should output to console
     */
    public void setOutput(boolean sOutput){
        shouldOutput = sOutput;
    }


    /**
     * Called when controller updates its frame
     * Checks for ScreenTapGestures and analyzes them to see if they hit any gadgets
     * @param controller Controller that captured frame
     */
    public void onFrame(Controller controller) {
        Frame currentFrame = controller.frame();
        updateInteractionBox(currentFrame.interactionBox());
        updateFingerPosition(currentFrame);
        for (Gesture g : currentFrame.gestures()){
            if (g.type() == ScreenTapGesture.classType()){
                try {
                    analyzeGesture(g);
                } catch(Exception e){
                    System.out.println(e);
                }
            }
        }
    }


    public void updateInteractionBox(InteractionBox ibox){
        if (currentInteractionBox == null && ibox.isValid()){
            currentInteractionBox = ibox;
        }
        if (ibox.isValid() && ibox.height() > currentInteractionBox.height() &&
                ibox.width() > currentInteractionBox.width()){
            currentInteractionBox = ibox;
        }
    }


    public void updateFingerPosition(Frame frame){
        lastFingerPosition = frame.pointables().frontmost().stabilizedTipPosition();
    }


    public Vector getNormalizedFingerPosition(){
        if (currentInteractionBox != null) {
            return currentInteractionBox.normalizePoint(lastFingerPosition);
        } else {
            return null;
        }
    }


    public boolean isListening(){
        return isListening;
    }


    public boolean gadgetsChanged(){
        if (changedGadgets){
            changedGadgets = false;
            return true;
        }
        return false;
    }


    /**
     * Adds a gadget to the list of current Gadgets
     * Making sure that it does not intersect with any currently existing gadgets
     * @param newGadget Gadget to add
     * @return Whether this gadget was successfully added
     */
    public boolean addGadget(ScreenGadget newGadget){
        for (ScreenGadget curGadg : myScreenGadgets){
            if (curGadg.contains(newGadget) || newGadget.contains(curGadg)){
                return false;
            }
        }
        myScreenGadgets.add(newGadget);
        changedGadgets = true;
        System.out.println(newGadget.getTopLeftString());
        System.out.println(newGadget.getBottomRightString());
        return true;
    }


    /**
     * Removes gadget from list of current Gadgets
     * @param deleteGadget Gadget to remove
     */
    public void removeGadget(ScreenGadget deleteGadget){
        myScreenGadgets.remove(deleteGadget);
        changedGadgets = true;
    }


    /**
     * Clears all the gadgets
     */
    public void removeAllGadgets(){
        myScreenGadgets.clear();
        changedGadgets = true;
    }


    /**
     * Gets the list of current Gadgets
     * @return list of current Gadgets
     */
    public ArrayList<ScreenGadget> getScreenGadgets(){
        return myScreenGadgets;
    }


    /**
     * Analyzes the current gesture and compares it to current gadgets
     * If gesture falls within a LeapInterface.ScreenGadget's range an action is done
     * @param g Gesture to be analyzed
     */
    private void analyzeGesture(Gesture g){
        ScreenGadget closestGadget = null;
        float minDistance = Float.MAX_VALUE;
        ScreenTapGesture stg = new ScreenTapGesture(g);
        Vector p0 = stg.position();
        for (int i = 0; i < myScreenGadgets.size(); i++){
            ScreenGadget curGadg = myScreenGadgets.get(i);
            if (curGadg.contains(p0.getX(), p0.getY())){
                curGadg.executeCommand();
                System.out.println("Used Gadget : " + curGadg.getGadgetName());
            }
        }
    }

/**
     Old Gesture Analysis

            Vector p1 = p0.plus(stg.direction().times(100));
        for (int i = 0; i < myScreenGadgets.size(); i++){
            Vector intersect = findRayIntersection(p0, p1,
                    myScreenGadgets.get(i).getNormal(), myScreenGadgets.get(i).getPoint());
            if (intersect == null){
                continue;
            }
            float distance = myScreenGadgets.get(i).getPoint().distanceTo(intersect);
            if (shouldOutput && myScreenGadgets.get(i).withinDistance(distance) && distance < minDistance) {
                closestGadget = myScreenGadgets.get(i);
                minDistance = distance;
            }
        }
        if (closestGadget != null && shouldOutput) {
            System.out.println("Used Gadget : " + closestGadget.getGadgetName());
        }





    private Vector findRayIntersection(Vector p0, Vector p1, Vector pNormal, Vector pPoint) {
        float denom = (pNormal.dot(p1.minus(p0)));
        float rI = (pNormal.dot(pPoint.minus(p0))) / denom;
        if (Math.abs(denom) < 0.001 || (Math.abs(denom) > 0.001 && rI < 0)) {
            return null;
        } else {
            return p0.plus((p1.minus(p0).times(rI)));
        }
    }

 **/


}
