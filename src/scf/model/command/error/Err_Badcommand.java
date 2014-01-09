
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badcommand extends Error
{
    public static final String CODE = "101";
    
    public Err_Badcommand()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}
