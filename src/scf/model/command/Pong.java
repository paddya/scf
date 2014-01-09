
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

}