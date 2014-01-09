
package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Reconnected extends Reply
{
    public static final String CODE = "006";
    
    public Rpl_Reconnected()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
