
public class Command {

    private String title;
    private String command;


    public Command(String cTitle, String cCommand){
        title = cTitle;
        command = cCommand;
    }


    public String getTitle(){
        return title;
    }


    public String getCommand(){
        return command;
    }
}
