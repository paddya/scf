package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class GetGames extends Command
{

    public static final String NAME = "GETGAMES";

    public GetGames()
    {
        protocolRepresentation = NAME;
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
