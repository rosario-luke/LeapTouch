package LeapInterface;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;


public class LeapTouchController extends Listener{

    private Controller controller;
    private PointListener listener;
    private CommandParser commandParser;


    public LeapTouchController(){
        controller = new Controller();
        listener = new PointListener(controller);
        commandParser = new CommandParser(null);
        listener.start();
    }


    public PointListener getPointListener(){
        return listener;
    }


    public CommandParser getCommandParser(){
        return commandParser;
    }


    public boolean addScreenGadget(ScreenGadget screenGadget){
        return listener.addGadget(screenGadget);
    }


    public boolean addTrackPad(LeapTrackPad trackPad) {
        return listener.addTrackPad(trackPad);
    }


    public GadgetSetup getGadgetSetup(){
        return new GadgetSetup(commandParser.getCommands());
    }


    public Controller getController(){
        return controller;
    }


    /**
     * Stops the currently running listener and gets a new LeapInterface.ScreenGadget
     */
    public void addGadget(){
        listener.stop();
        boolean shouldContinue = true;
        while(shouldContinue) {
            ScreenGadget newGadget = getScreenGadgetWithGadgetSetup();
            while (newGadget == null) {
                newGadget = getScreenGadgetWithGadgetSetup();
            }
            boolean success = listener.addGadget(newGadget);
            if (!success){
                System.out.println("Cannot add gadget that intersects another");
                shouldContinue = ConsoleUtilities.askYesOrNo("Do you want to try again?");
            } else {
                System.out.println("Successfully Added");
                shouldContinue = false;
            }
        }
        listener.start();
    }


    /**
     * Helper function for addGadget that interacts with LeapInterface.GadgetSetup class
     * @return a new LeapInterface.ScreenGadget or null if an error occurred
     */
    private ScreenGadget getScreenGadgetWithGadgetSetup(){
        GadgetSetup screenSetupHelper = new GadgetSetup(commandParser.getCommands());
        boolean caughtError = true;
        while(caughtError) {
            try {
                ScreenGadget nGadget = screenSetupHelper.waitForSetUp();
                while (nGadget == null){
                    nGadget = screenSetupHelper.waitForSetUp();
                }
                caughtError = false;
                return nGadget;
            } catch (InterruptedException e) {
                System.out.println("Caught interrupt exception while waiting for set up");
                return null;
            }
        }
        return null;
    }
}

