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

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
