
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Pong extends Command
{

    public static final String NAME = "RECONNECT";

    public Pong()
    {
        protocolRepresentation = NAME;
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}