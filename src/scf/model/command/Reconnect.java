
package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class Reconnect extends Command
{

    private String playerID;
    public static final String NAME = "RECONNECT";

    public Reconnect(String playerID)
    {
        this.playerID = playerID;
        protocolRepresentation = NAME + " "; // + ...
    }



    public String getPlayerID()
    {
        return playerID;
    }



    public void setPlayerID(String playerID)
    {
        this.playerID = playerID;
    }
    
    
}
