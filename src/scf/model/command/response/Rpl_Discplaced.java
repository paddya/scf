
package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Discplaced extends Reply
{

    public static final String CODE = "004";
    
    public Rpl_Discplaced()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
