package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class CreateGame extends Command
{

    public static final String NAME = "CREATEGAME";



    public CreateGame()
    {
        protocolRepresentation = NAME;
    }

}


