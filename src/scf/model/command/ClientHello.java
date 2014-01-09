package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class ClientHello extends Command
{
	
	private String playerID;

    public static final String NAME = "CLIENTHELLO";

    public ClientHello(String playerID)
    {
        this.playerID = playerID;
        protocolRepresentation = String.format("%s %s", NAME, playerID);
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
