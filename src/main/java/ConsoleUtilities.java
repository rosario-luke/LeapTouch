import java.util.Scanner;


public class ConsoleUtilities {

    public static String getConsoleInput(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }


    public static boolean askYesOrNo(String query){
        String yesOrNo = null;
        boolean validInput = false;
        while (!validInput) {
            System.out.println(query);

            yesOrNo = getConsoleInput();
            validInput = yesOrNo.toUpperCase().equals("Y") || yesOrNo.toUpperCase().equals("N");
            if (!validInput) {
                System.out.println("Invalid input! Please enter /'Y/' or /'N/'");
            }
        }
        return yesOrNo.toUpperCase().equals("Y");
    }


    public static void waitForInput(){
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static Integer getConsoleNumber(){
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        Integer parsedInt = null;
        boolean caughtError = true;
        while(caughtError) {
            try {
                parsedInt = Integer.parseInt(next);
                caughtError = false;
            } catch (Exception e) {
                caughtError = true;
            }
        }
        return parsedInt;
    }
}
