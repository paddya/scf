
package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Joinedgame extends Reply
{
    
    public static final String CODE = "003";

    public Rpl_Joinedgame()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
