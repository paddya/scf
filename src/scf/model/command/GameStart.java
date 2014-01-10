package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class GameStart extends Command
{

    public static final String NAME = "GAMESTART";



    public GameStart()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
    }
}


