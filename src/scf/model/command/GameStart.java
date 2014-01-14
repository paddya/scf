package scf.model.command;



/**
 *
 * @author Ferdinand Sauer
 */
public class GameStart extends Command
{

    public static final String NAME = "GAMESTART";

    public String playerWithToken;
    
    public String opponentName;

    public GameStart(String opponentName, String playerWithToken)
    {
        this.playerWithToken = playerWithToken;
        this.opponentName = opponentName;
        protocolRepresentation = String.format("%s %s %s", NAME, opponentName, playerWithToken); // + args and/or something
    }



    public String getPlayerWithToken()
    {
        return playerWithToken;
    }



    public void setPlayerWithToken(String playerWithToken)
    {
        this.playerWithToken = playerWithToken;
    }



    public String getOpponentName()
    {
        return opponentName;
    }



    public void setOpponentName(String opponentName)
    {
        this.opponentName = opponentName;
    }
    
}


