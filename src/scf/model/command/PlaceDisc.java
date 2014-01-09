package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class PlaceDisc extends Command
{

    public static final String NAME = "PLACEDISC";

    public PlaceDisc()
    {
        protocolRepresentation = NAME + " "; // + ...
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
