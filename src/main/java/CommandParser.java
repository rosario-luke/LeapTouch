import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CommandParser {

    private String filePath;
    private ArrayList<Command> myCommands;

    public CommandParser(String commandFile) {
        if (commandFile == null) {
            filePath = getClass().getClassLoader().getResource("premadeCommands.csv").getFile();
        } else{
            filePath = commandFile;
        }
        myCommands = new ArrayList<Command>();
        parseFile();
    }

    public void parseFile() {
        BufferedReader fileReader = null;

        //Delimiter used in CSV file
        final String DELIMITER = ",";
        try
        {
            String line = "";
            fileReader = new BufferedReader(new FileReader(filePath));

            while ((line = fileReader.readLine()) != null)
            {
                String[] tokens = line.split(DELIMITER);

                // There should only be two tokens, if there are more skip the line
                if (tokens.length != 2){
                    continue;
                }
                myCommands.add(new Command(tokens[0], tokens[1]));
            }
            System.out.println("Parsed Commands");
            for (Command c : myCommands){
                System.out.println(c.getTitle() + " : " + c.getCommand());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<Command> getCommands(){
        return myCommands;
    }


}
