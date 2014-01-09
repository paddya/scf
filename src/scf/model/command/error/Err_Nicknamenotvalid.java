
package scf.model.command.error;

/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Nicknamenotvalid extends Error
{
    public static final String CODE = "111";
    
    public Err_Nicknamenotvalid()
    {
        protocolRepresentation = "ERROR " + CODE;
    }    
}