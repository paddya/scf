
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Nosuchgame extends Error
{
    
    public Err_Nosuchgame()
    {
        protocolRepresentation = "ERROR 120";
    }    
}