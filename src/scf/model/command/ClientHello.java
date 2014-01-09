package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class ClientHello extends Command
{

    public static final String NAME = "CLIENTHELLO";

    public ClientHello()
    {
        protocolRepresentation = NAME + " "; // + ...
    }

}
