
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Reconnect extends Command
{

    public static final String NAME = "RECONNECT";

    public Reconnect()
    {
        protocolRepresentation = NAME + " "; // + ...
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
