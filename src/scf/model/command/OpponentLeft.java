package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class OpponentLeft extends Command
{

    public static final String NAME = "OPPONENTLEFT";



    public OpponentLeft()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
    }

}


