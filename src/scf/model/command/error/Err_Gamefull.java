
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Gamefull extends Error
{
    
    public static final String CODE = "121";
    
    public Err_Gamefull()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}