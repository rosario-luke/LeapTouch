package LeapInterface;

import com.leapmotion.leap.*;

import java.util.ArrayList;


public class PointListener extends Listener {

    private static float CONFIG_MIN_FORWARD_VEL = 50.0f;
    private static float CONFIG_HISTORY_SECONDS =  0.1f;
    private static float CONFIG_MIN_DISTANCE = 5.0f;

    private Controller myController;
    private ArrayList<ScreenGadget> myScreenGadgets;
    private ArrayList<LeapTrackPad> myTrackPads;
    private boolean isListening;
    private boolean changedGadgets = false;
    private Vector lastFingerPosition = null;
    public static InteractionBox currentInteractionBox;


    public PointListener(Controller controller) {
        myController = controller;
        myScreenGadgets = new ArrayList<ScreenGadget>();
        myTrackPads = new ArrayList<LeapTrackPad>();
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
     * Called when controller updates its frame
     * Checks for ScreenTapGestures and analyzes them to see if they hit any gadgets
     * @param controller Controller that captured frame
     */
    public void onFrame(Controller controller) {
        Frame currentFrame = controller.frame();
        updateInteractionBox(currentFrame.interactionBox());
        updateFingerPosition(currentFrame);
        checkTrackPads();
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


    /**
     * Updates the current interaction box that we use to normalize points to our screen
     * We want to use the biggest interaction box possible, thus everytime we see a larger one
     * we set it to our current InterationBox
     * @param ibox - InteractionBox of the current frame
     */
    public void updateInteractionBox(InteractionBox ibox){
        if (currentInteractionBox == null && ibox.isValid()){
            currentInteractionBox = ibox;
        }
        if (ibox.isValid() && ibox.height() > currentInteractionBox.height() &&
                ibox.width() > currentInteractionBox.width()){
            currentInteractionBox = ibox;
        }
    }


    /**
     * Loops through the current track pads and looks to see if the finger can interact
     * with the trackpad
     */
    public void checkTrackPads(){
        for (LeapTrackPad trackPad : myTrackPads){
            if (trackPad.contains(lastFingerPosition.getX(), lastFingerPosition.getY())) {
                trackPad.executeCommand(lastFingerPosition);
            }
        }
    }


    /**
     * Updates last finger position seen to the current frontmost finger pointing at the screen
     * @param frame
     */
    public void updateFingerPosition(Frame frame){
        lastFingerPosition = frame.pointables().frontmost().stabilizedTipPosition();
    }


    /**
     * Normalizes the finger position to the current interaction box that we are using
     * for use in drawing the related application coordinate
     * @return Vector containing the normalized coordinate
     */
    public Vector getNormalizedFingerPosition(){
        if (currentInteractionBox != null) {
            return currentInteractionBox.normalizePoint(lastFingerPosition);
        } else {
            return null;
        }
    }


    /**
     * Returns if the PointListener is currently updating against the LeapController
     * @return Boolean indicating if it changed
     */
    public boolean isListening(){
        return isListening;
    }


    /**
     * Checks if gadgets have changed since the last time this method was called
     * @return
     */
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


    public boolean addTrackPad(LeapTrackPad trackPad){
        for (ScreenGadget curGadg : myScreenGadgets){
            if (curGadg.contains(trackPad) || trackPad.contains(curGadg)){
                return false;
            }
        }
        myTrackPads.add(trackPad);
        myScreenGadgets.add(trackPad);
        changedGadgets = true;
        return true;
    }


    /**
     * Removes gadget from list of current Gadgets
     * @param deleteGadget Gadget to remove
     */
    public void removeGadget(ScreenGadget deleteGadget){
        myScreenGadgets.remove(deleteGadget);
        if (deleteGadget instanceof LeapTrackPad){
            myTrackPads.remove(deleteGadget);
        }
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



}
