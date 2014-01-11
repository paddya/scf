package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class LeaveGame extends Command
{

    public static final String NAME = "LEAVEGAME";



    public LeaveGame()
    {
        protocolRepresentation = NAME;
    }
}


