
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Badcommand extends Error
{
    
    public Err_Badcommand()
    {
        protocolRepresentation = "ERROR 101";
    }    
}
