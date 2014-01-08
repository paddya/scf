
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

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
