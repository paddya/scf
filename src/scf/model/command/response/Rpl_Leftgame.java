
package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Leftgame extends Reply
{
    
    public static final String CODE = "005";
    
    public Rpl_Leftgame()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
