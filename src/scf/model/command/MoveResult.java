
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class MoveResult extends Command
{

    public static final String NAME = "MOVERESULT";

    public MoveResult()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
