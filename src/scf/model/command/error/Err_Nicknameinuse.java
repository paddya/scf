
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Nicknameinuse extends Error
{
    
    public Err_Nicknameinuse()
    {
        protocolRepresentation = "ERROR 110";
    }    
}