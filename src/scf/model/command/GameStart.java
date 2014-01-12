package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class GameStart extends Command
{

    public static final String NAME = "GAMESTART";

    public String playerWithToken;

    public GameStart(String playerWithToken)
    {
        this.playerWithToken = playerWithToken;
        protocolRepresentation = NAME + " " + playerWithToken; // + args and/or something
    }



    public String getPlayerWithToken()
    {
        return playerWithToken;
    }



    public void setPlayerWithToken(String playerWithToken)
    {
        this.playerWithToken = playerWithToken;
    }
    
    
}


