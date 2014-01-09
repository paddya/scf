package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class LeaveGame extends Command
{

    public static final String NAME = "LEAVEGAME";

    public LeaveGame()
    {
        protocolRepresentation = NAME;
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
