package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class GamesList extends Command
{

    public static final String NAME = "GAMESLIST";

    public GamesList()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
