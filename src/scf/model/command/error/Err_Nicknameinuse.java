package scf.model.command.error;



/**
 *
 * @author Ferdinand Sauer
 */
public class Err_Nicknameinuse extends Error
{

    public static final String CODE = "110";



    public Err_Nicknameinuse()
    {
        protocolRepresentation = "ERROR " + CODE;
    }
}