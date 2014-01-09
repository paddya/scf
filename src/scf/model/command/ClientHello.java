package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class ClientHello extends Command
{
	
	private String playerID;

    public static final String NAME = "CLIENTHELLO";

    public ClientHello()
    {
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
