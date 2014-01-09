package scf.model.command.response;

/**
 *
 * @author Ferdinand Sauer
 */
public class Rpl_Serverhello extends Reply
{

    public static final String CODE = "001";
    
    public Rpl_Serverhello()
    {
        protocolRepresentation = "RESPONSE " + CODE;
    }

}
