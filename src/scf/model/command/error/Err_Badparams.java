
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badparams extends Error
{
    public static final String CODE = "103";
    
    public Err_Badparams()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}