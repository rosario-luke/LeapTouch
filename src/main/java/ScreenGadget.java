import com.leapmotion.leap.Vector;

/**
 * Class representing an area in 2D coordinate space with an associated name
 */
public class ScreenGadget {

    private String gadgetName;
    private Vector topLeft;
    private Vector bottomRight;
    private Command gadgetCommand;


    public ScreenGadget(Vector topLeftPoint, Vector bottomRightPoint, String gName){
        topLeft = topLeftPoint;
        bottomRight = bottomRightPoint;
        gadgetName = gName;
        gadgetCommand = null;
    }


    public ScreenGadget(Vector topLeftPoint, Vector bottomRightPoint, String gName, Command command){
        topLeft = topLeftPoint;
        bottomRight = bottomRightPoint;
        gadgetName = gName;
        gadgetCommand = command;
    }

    public Vector getTopLeft(){
        return topLeft;
    }


    public Vector getBottomRight(){
        return bottomRight;
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


    /**
     * Checks whether the given coordinates fall within the ScreenGadget
     * @param x
     * @param y
     * @return
     */
    public boolean contains(float x, float y ){
        return x >= topLeft.getX() && x <= bottomRight.getX()
                && y <= topLeft.getY() && y >= bottomRight.getY();
    }


    public void executeCommand() {
        if (gadgetCommand == null) {
            return;
        }

        try {
            System.out.println(gadgetCommand.getCommand());
            Process p = Runtime.getRuntime().exec("osascript -e \"tell application \\\"Spotify\\\" to playpause\"");

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
}
