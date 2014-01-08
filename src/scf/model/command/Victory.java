
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Victory extends Command
{

    public static final String NAME = "VICTORY";

    public Victory()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
