
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Pong extends Command
{

    public static final String NAME = "PONG";

    public Pong()
    {
        protocolRepresentation = NAME;
    }

}