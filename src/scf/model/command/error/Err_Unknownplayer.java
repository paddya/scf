
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Unknownplayer extends Error
{
    
    public Err_Unknownplayer()
    {
        protocolRepresentation = "ERROR 130";
    }    
}