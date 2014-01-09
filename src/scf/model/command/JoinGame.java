package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class JoinGame extends Command
{

    public static final String NAME = "JOINGAME";

    public JoinGame()
    {
        protocolRepresentation = NAME + " "; // + ...
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
