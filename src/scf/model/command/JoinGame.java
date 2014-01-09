package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class JoinGame extends Command
{

    public static final String NAME = "JOINGAME";
	
	private String gameId;

    public JoinGame(String gameId)
    {
        protocolRepresentation = NAME + " "; // + ...
    }



    public String getGameId()
    {
        return gameId;
    }

}
