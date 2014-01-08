
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badsyntax extends Error
{
    
    public Err_Badsyntax()
    {
        protocolRepresentation = "ERROR 102";
    }    
}