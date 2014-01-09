package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Ping extends Command
{

    public static final String NAME = "PING";

    public Ping()
    {
        protocolRepresentation = NAME;
    }

}
