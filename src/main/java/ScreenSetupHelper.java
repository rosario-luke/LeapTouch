import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import java.util.LinkedList;

/**
 * Old class for creating ScreenGadgets. Might go back later to this method if it proves useful
 * Not used, essentially dead code for now
 */
public class ScreenSetupHelper extends Listener {

    private static float movementTolerance = 5;
    private Controller controller;
    private LinkedList<Vector[]> lastFrames;
    private boolean hasFinishedSetup;
    private Object syncObject;
    private Vector pPoint;
    private Vector pNormal;

    public ScreenSetupHelper() {

        System.out.println("Initializing controller...");
        syncObject = new Object();
        lastFrames = new LinkedList<Vector[]>();
        controller = new Controller();
    }


    public ScreenGadget waitForSetUp() throws InterruptedException {
        controller.addListener(this);
        hasFinishedSetup = false;

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

                pPoint = calculateAvgPosition();
                if (pPoint.equals(new Vector(0, 0, 0))) {
                    return;
                }

                pNormal = calculateAvgNormal();


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


    private Vector calculateAvgPosition(){
        Vector avgPosition = Vector.zero();
        for (int i = 0; i < lastFrames.size(); i++) {
            avgPosition = avgPosition.plus(lastFrames.get(i)[0]);
        }
        avgPosition = avgPosition.divide(lastFrames.size());
        return avgPosition;
    }


    private Vector calculateAvgNormal(){
        Vector avgNormal = Vector.zero();
        for (int i =0; i < lastFrames.size(); i++){
            avgNormal = avgNormal.plus(lastFrames.get(i)[1]);
        }

        avgNormal = avgNormal.divide(lastFrames.size());
        return avgNormal;
    }


    private Vector createCornersFromPoint(Vector p, Vector n){
        float SCREEN_SIZE = 200;

        Vector revN = n.opposite();
        Vector rotatePoint = p.plus(revN.times(SCREEN_SIZE));
        System.out.println("Rotate Point: " + rotatePoint);

        Vector one = rotateAboutZAxis(rotateAboutXAxis(rotatePoint, 90), 45);
        System.out.println("One : " + one);
        Vector two = rotateAboutZAxis(rotateAboutXAxis(rotatePoint, 90), -45);
        System.out.println("two : " + two);
        Vector three = rotateAboutZAxis(rotateAboutXAxis(rotatePoint, -90), 45);
        System.out.println("three : " + three);
        Vector four = rotateAboutZAxis(rotateAboutXAxis(rotatePoint, -90), -45);
        System.out.println("four : " + four);
        return one;

    }

    private Vector rotateAboutXAxis(Vector r, float degrees){
        double theta = Math.toRadians(degrees);
        float xN = r.getX();
        float yN = (r.getY() * (float)Math.cos(theta)) - (r.getZ() * (float)Math.sin(theta));
        float zN = (r.getY() * (float)Math.sin(theta)) + (r.getZ() * (float)Math.cos(theta));
        return new Vector(xN, yN, zN);
    }


    private Vector rotateAboutYAxis(Vector r, float degrees){
        double theta = Math.toRadians(degrees);
        float xN = (r.getZ() * (float)Math.sin(theta)) + (r.getX() * (float)Math.cos(theta));
        float yN = r.getY();
        float zN = (r.getZ() * (float)Math.cos(theta)) - (r.getX() * (float)Math.sin(theta));
        return new Vector(xN, yN, zN);
    }


    private Vector rotateAboutZAxis(Vector r, float degrees){
        double theta = Math.toRadians(degrees);
        float xN = (r.getX() * (float)Math.cos(theta)) - (r.getY() * (float)Math.sin(theta));
        float yN = (r.getX() * (float)Math.sin(theta)) + (r.getY() * (float)Math.cos(theta));
        float zN = r.getZ();
        return new Vector(xN, yN, zN);
    }


    private Vector calculatePlaneNormal(Vector a, Vector b, Vector c){
        Vector d = (b.minus(a)).cross(c.minus(a));
        return d.normalized();
    }


    private ScreenGadget createScreenGadget() {

        System.out.println("Point : " + pPoint + "     Normal: " + pNormal);
        boolean validInput = false;
        String yesOrNo = null;
        while (!validInput) {
            System.out.println("Are you okay with these Vectors? (Y/N)");

            yesOrNo = ConsoleUtilities.getConsoleInput();
            validInput = yesOrNo.toUpperCase().equals("Y") || yesOrNo.toUpperCase().equals("N");
            if (!validInput) {
                System.out.println("Invalid input! Please enter /'Y/' or /'N/'");
            }
        }
        if(!(yesOrNo.toUpperCase().equals("Y"))){
            return null;
        }
        System.out.print("Gadget Name: ");
        String gadgetName = ConsoleUtilities.getConsoleInput();
        return new ScreenGadget(pPoint, pNormal, gadgetName);
    }
}
