
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Unknownplayer extends Error
{
    public static final String CODE = "130";
    public Err_Unknownplayer()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}