package LeapInterface;

import com.leapmotion.leap.Vector;

/**
 * Class representing an area in 2D coordinate space with an associated name
 */
public class ScreenGadget {

    private String gadgetName;
    private Vector topLeft;
    private Vector bottomRight;
    private Command gadgetCommand;


    public ScreenGadget(Vector topLeft, Vector bottomRight, String gadgetName){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.gadgetName = gadgetName;
        gadgetCommand = null;
    }


    public ScreenGadget(Vector topLeft,  Vector bottomRight, String gadgetName, Command command){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.gadgetName = gadgetName;
        gadgetCommand = command;
    }


    public Vector getTopLeft(){
        return topLeft;
    }


    public Vector getBottomRight(){
        return bottomRight;
    }


    public Vector getNormalizedBottomRight() {
        if (PointListener.currentInteractionBox == null){ return null; }
        return PointListener.currentInteractionBox.normalizePoint(bottomRight);
    }


    public Vector getNormalizedTopLeft() {
        if (PointListener.currentInteractionBox == null){ return null; }
        return PointListener.currentInteractionBox.normalizePoint(topLeft);
    }


    public String getTopLeftString(){
        return "( " + topLeft.getX() + " , " + topLeft.getY() + " )";
    }


    public String getBottomRightString(){
        return "( " + bottomRight.getX() + " , " + bottomRight.getY() + " )";
    }


    public String getGadgetName(){
        return gadgetName;
    }


    public Command getGadgetCommand(){ return gadgetCommand; }


    /**
     * Checks whether the given coordinates fall within the LeapInterface.ScreenGadget
     * @param x
     * @param y
     * @return
     */
    public boolean contains(float x, float y ){
        return x >= topLeft.getX() && x <= bottomRight.getX()
                && y <= topLeft.getY() && y >= bottomRight.getY();
    }


    public boolean contains(ScreenGadget other){
        boolean containsTL = contains(other.topLeft.getX(), other.topLeft.getY());
        boolean containsBR = contains(other.bottomRight.getX(), other.bottomRight.getY());
        return containsTL || containsBR;
    }


    public void executeCommand() {
        if (gadgetCommand == null) {
            return;
        }

        try {
            System.out.println(gadgetCommand.getCommand());
            Process p = Runtime.getRuntime().exec(gadgetCommand.getCommand());

            int i;
            while ((i = p.getInputStream().read()) != -1) {
                System.out.write(i);
            }
            while ((i = p.getErrorStream().read()) != -1) {
                System.err.write(i);
            }
        } catch (Exception e) {
            System.out.println("Caught exception : " + e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }


    @Override
    public String toString(){
        return String.format("%s: %s", getGadgetName(), getGadgetCommand().getTitle());
    }


    public void setGadgetCommand(Command command){
        gadgetCommand = command;
    }
}
