package scf.model.command;

import scf.model.Player;

/**
 *
 * @author Ferdinand Sauer
 */
public abstract class Command
{

    private Player player;
    protected String protocolRepresentation = "";

    public abstract void execute();

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
    
    public String toString() {
        return protocolRepresentation;
    }
}