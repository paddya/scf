
package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Gamecreated extends Reply
{
    public static final String CODE = "002";
    
    public Rpl_Gamecreated()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
