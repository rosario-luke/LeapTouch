package LeapInterface;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

import java.util.ArrayList;


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
     * Shows the output of the listener
     */
    public void listen(){
        listener.setOutput(true);
        ConsoleUtilities.waitForInput();
        listener.setOutput(false);
    }


    /**
     * Displays all the gadgets and their coordinates to the user
     * Allows the user to remove any Gadget inside the list
     */
    public  void removeGadgets(){
        ArrayList<ScreenGadget> gadgets = listener.getScreenGadgets();

        if (gadgets.size() == 0){
            System.out.println("No gadgets currently set");
            return;
        }

        for (int i = 0; i < gadgets.size(); i++){
            ScreenGadget curGadg = gadgets.get(i);
            System.out.println(i + ") Remove " + curGadg.getGadgetName() + " : TopLeft " + curGadg.getTopLeftString() +
                    "     BottomRight " + curGadg.getBottomRightString());
        }
        System.out.println(gadgets.size() + ") Exit");

        Integer choice = ConsoleUtilities.getConsoleNumber();

        if (choice >= 0 && choice < gadgets.size()){
            System.out.println("Removed Gadget: " + gadgets.get(choice).getGadgetName());
            listener.removeGadget(gadgets.get(choice));
        } else {
            System.out.println("Exiting");
        }
    }

    /**
     * Prints menu options
     */
    public static void printMenu(){
        System.out.println("-------- TOUCH ANYWHERE MENU ----------");
        System.out.println("1) Add Gadget");
        System.out.println("2) Listen");
        System.out.println("3) Remove Gadgets");
        System.out.println("4) Exit");
        System.out.print("#: ");
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


/**
 *     public static void main(String[] args){

 System.out.println("Started application");
 controller = new Controller();
 listener = new LeapInterface.PointListener(controller);
 commandParser = new LeapInterface.CommandParser(null);
 listener.start();

 boolean finished = false;
 Integer choice;
 while (!finished){
 printMenu();
 choice = LeapInterface.ConsoleUtilities.getConsoleNumber();

 switch(choice){
 case 1:
 addGadget();
 break;
 case 2:
 listen();
 break;
 case 3:
 removeGadgets();
 break;
 case 4:
 finished = true;
 break;
 default:
 System.out.println("Invalid Input");
 }
 }
 listener.stop();

 System.out.println("");
 System.out.println("Goodbye");
 }
 */