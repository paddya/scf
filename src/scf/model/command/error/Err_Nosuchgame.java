
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Nosuchgame extends Error
{
    public static final String CODE = "120";
    
    public Err_Nosuchgame()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}