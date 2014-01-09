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

}
