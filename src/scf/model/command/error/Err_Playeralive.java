
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Playeralive extends Error
{
    public static final String CODE = "131";
    
    public Err_Playeralive()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}