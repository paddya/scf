
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badcolumn extends Error
{
    
    public static final String CODE = "122";
    
    public Err_Badcolumn()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}