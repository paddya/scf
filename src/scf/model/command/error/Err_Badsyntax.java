
package scf.model.command.error;

import static scf.model.command.error.Err_Badcolumn.CODE;



/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badsyntax extends Error
{
    
    public static final String CODE = "102";
    
    public Err_Badsyntax()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}