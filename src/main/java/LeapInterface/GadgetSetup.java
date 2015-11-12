package LeapInterface;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import java.util.ArrayList;
import java.util.LinkedList;

public class GadgetSetup extends Listener {

    private static float movementTolerance = 5;
    private Controller controller;
    private LinkedList<Vector[]> lastFrames;
    private ArrayList<Command> commands;
    private boolean hasFinishedSetup;
    private Object syncObject;
    Vector topLeft;
    Vector bottomRight;
    Vector nTopLeft;
    Vector nBottomRight;

    public GadgetSetup(ArrayList<Command> acceptedCommands) {
        System.out.println("Initializing controller...");
        syncObject = new Object();
        lastFrames = new LinkedList<Vector[]>();
        controller = new Controller();
        commands = acceptedCommands;
    }


    /**
     * Waits on the controller and LeapInterface.GadgetSetup to finish selecting corners
     * @return new LeapInterface.ScreenGadget created
     * @throws InterruptedException
     */
    public ScreenGadget waitForSetUp() throws InterruptedException {
        controller.addListener(this);
        hasFinishedSetup = false;
        topLeft = null;
        bottomRight = null;

        while(!hasFinishedSetup){
            synchronized (syncObject) {
                syncObject.wait();
            }
        }

        ScreenGadget screenGadget = createScreenGadget();
        if (screenGadget == null){
            return null;
        } else {
            return screenGadget;
        }
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

            System.out.println(currentTip);
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
                Vector normalizedPoint = currentFrame.interactionBox().normalizePoint(point, false);
                if (topLeft == null){
                    topLeft = point;
                    nTopLeft = normalizedPoint;
                    System.out.println("Set topLeft to : (" + topLeft.getX() + " , " + topLeft.getY() + " ) ");
                    try{
                        Thread.sleep(2000);
                    } catch(Exception e){
                        System.out.println("Caught Something Bad");
                        System.out.println(e.getStackTrace());
                        System.exit(1);
                    }
                    return;
                }
                bottomRight = point;
                nBottomRight = normalizedPoint;
                System.out.println("Set bottomRight to : (" + bottomRight.getX() + " , " + bottomRight.getY() + " ) ");


                System.out.println("Successfully completed setup");
                controller.removeListener(this);
                hasFinishedSetup = true;
                synchronized (syncObject) {
                    syncObject.notifyAll();
                }

                lastFrames.clear();
            }
        }
    }


    /**
     * Ask user to agree to found coordinates
     * @return Null (if user dislikes coordinates) or new LeapInterface.ScreenGadget representing found coordinates
     */
    private ScreenGadget createScreenGadget() {
        System.out.println("Set topLeft to : (" + topLeft.getX() + " , " + topLeft.getY() + " ) ");
        System.out.println("Set bottomRight to : (" + bottomRight.getX() + " , " + bottomRight.getY() + " ) ");

        boolean yesOrNo = ConsoleUtilities.askYesOrNo("Are you okay with these vectors? (Y/N)");
        if(!yesOrNo){
            return null;
        }
        System.out.print("Gadget Name: ");
        String gadgetName = ConsoleUtilities.getConsoleInput();

        if (commands.size() > 0) {
            yesOrNo = ConsoleUtilities.askYesOrNo("Would you like to add a command? (Y/N)");
            if (!yesOrNo) {
                return new ScreenGadget(topLeft, bottomRight, gadgetName);
            } else {
                Command chosenCommand = queryUserForCommand();
                return new ScreenGadget(topLeft, bottomRight, gadgetName, chosenCommand);
            }
        } else {
            return new ScreenGadget(topLeft,  bottomRight,  gadgetName);
        }

    }


    private Command queryUserForCommand(){
        System.out.println("Which command would you like to add?");
        for (int i = 0; i < commands.size(); i ++){
            System.out.println(i + ") " + commands.get(i).getTitle());
        }
        System.out.println(commands.size() + ") None" );
        Integer choice = ConsoleUtilities.getConsoleNumber();
        if (choice >= 0 && choice < commands.size()){
            return commands.get(choice);
        }
        return null;
    }
}
